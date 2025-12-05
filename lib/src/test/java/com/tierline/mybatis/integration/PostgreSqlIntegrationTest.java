package com.tierline.mybatis.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Properties;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Integration test for TypeHandlers with PostgreSQL.
 *
 * <p>Prerequisites: Start PostgreSQL with Docker Compose:
 *
 * <pre>
 * docker-compose up -d
 * </pre>
 *
 * <p>Run tests:
 *
 * <pre>
 * ./gradlew test
 * </pre>
 *
 * <p>Stop PostgreSQL:
 *
 * <pre>
 * docker-compose down
 * </pre>
 */
@DisplayName("PostgreSQL 統合テスト - TypeHandler の動作確認")
class PostgreSqlIntegrationTest {

  private static final String JDBC_URL = "jdbc:postgresql://localhost:65432/testdb";
  private static final String USERNAME = "test";
  private static final String PASSWORD = "test";

  private static SqlSessionFactory sqlSessionFactory;
  private static boolean postgresAvailable = false;

  @BeforeAll
  static void setUpClass() {
    // Check if PostgreSQL is available
    try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
      postgresAvailable = true;

      // Setup MyBatis
      Properties properties = new Properties();
      properties.setProperty("jdbc.driver", "org.postgresql.Driver");
      properties.setProperty("jdbc.url", JDBC_URL);
      properties.setProperty("jdbc.username", USERNAME);
      properties.setProperty("jdbc.password", PASSWORD);

      InputStream inputStream = Resources.getResourceAsStream("mybatis-config-integration.xml");
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, properties);
    } catch (SQLException | IOException e) {
      System.err.println("⚠️  PostgreSQL is not available. Start it with: docker-compose up -d");
      System.err.println("   Skipping integration tests.");
    }
  }

  @AfterAll
  static void tearDownClass() {
    if (postgresAvailable) {
      System.out.println(
          "✅ Integration tests completed. Stop PostgreSQL with: docker-compose down");
    }
  }

  @BeforeEach
  void setUp() {
    if (!postgresAvailable) {
      org.junit.jupiter.api.Assumptions.assumeTrue(
          false, "PostgreSQL not available. Start with: docker-compose up -d");
    }

    // Clean up test data
    try (SqlSession session = sqlSessionFactory.openSession()) {
      Connection conn = session.getConnection();
      try (Statement stmt = conn.createStatement()) {
        stmt.execute("TRUNCATE TABLE test_entity");
      }
      session.commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @DisplayName("すべての Optional フィールドに値がある場合、正しく保存・取得できる")
  void testInsertAndSelectWithAllValues() {
    TestEntity entity = new TestEntity();
    entity.setId(1);
    entity.setName(Optional.of("John Doe"));
    entity.setAge(Optional.of(30));
    entity.setSalary(Optional.of(50000L));
    entity.setRate(Optional.of(0.15));
    entity.setAmount(Optional.of(new BigDecimal("1234.56")));
    entity.setActive(Optional.of(true));
    entity.setBirthDate(Optional.of(LocalDate.of(1990, 1, 15)));
    entity.setCreatedAt(
        Optional.of(OffsetDateTime.of(2024, 1, 1, 10, 30, 0, 0, ZoneOffset.ofHours(9))));

    try (SqlSession session = sqlSessionFactory.openSession()) {
      TestEntityMapper mapper = session.getMapper(TestEntityMapper.class);
      mapper.insert(entity);
      session.commit();

      TestEntity result = mapper.findById(1);

      assertNotNull(result);
      assertEquals(1, result.getId());
      assertTrue(result.getName().isPresent());
      assertEquals("John Doe", result.getName().get());
      assertTrue(result.getAge().isPresent());
      assertEquals(30, result.getAge().get());
      assertTrue(result.getSalary().isPresent());
      assertEquals(50000L, result.getSalary().get());
      assertTrue(result.getRate().isPresent());
      assertEquals(0.15, result.getRate().get(), 0.001);
      assertTrue(result.getAmount().isPresent());
      assertEquals(new BigDecimal("1234.56"), result.getAmount().get());
      assertTrue(result.getActive().isPresent());
      assertTrue(result.getActive().get());
      assertTrue(result.getBirthDate().isPresent());
      assertEquals(LocalDate.of(1990, 1, 15), result.getBirthDate().get());
      assertTrue(result.getCreatedAt().isPresent());
    }
  }

  @Test
  @DisplayName("すべての Optional フィールドが空の場合、NULL として保存・取得できる")
  void testInsertAndSelectWithEmptyOptionals() {
    TestEntity entity = new TestEntity();
    entity.setId(2);
    entity.setName(Optional.empty());
    entity.setAge(Optional.empty());
    entity.setSalary(Optional.empty());
    entity.setRate(Optional.empty());
    entity.setAmount(Optional.empty());
    entity.setActive(Optional.empty());
    entity.setBirthDate(Optional.empty());
    entity.setCreatedAt(Optional.empty());

    try (SqlSession session = sqlSessionFactory.openSession()) {
      TestEntityMapper mapper = session.getMapper(TestEntityMapper.class);
      mapper.insert(entity);
      session.commit();

      TestEntity result = mapper.findById(2);

      assertNotNull(result);
      assertEquals(2, result.getId());
      assertFalse(result.getName().isPresent());
      assertFalse(result.getAge().isPresent());
      assertFalse(result.getSalary().isPresent());
      assertFalse(result.getRate().isPresent());
      assertFalse(result.getAmount().isPresent());
      assertFalse(result.getActive().isPresent());
      assertFalse(result.getBirthDate().isPresent());
      assertFalse(result.getCreatedAt().isPresent());
    }
  }

  @Test
  @DisplayName("Optional<Boolean> に false を設定した場合、正しく保存・取得できる")
  void testBooleanFalseValue() {
    TestEntity entity = new TestEntity();
    entity.setId(3);
    entity.setName(Optional.of("Test"));
    entity.setAge(Optional.empty());
    entity.setSalary(Optional.empty());
    entity.setRate(Optional.empty());
    entity.setAmount(Optional.empty());
    entity.setActive(Optional.of(false));
    entity.setBirthDate(Optional.empty());
    entity.setCreatedAt(Optional.empty());

    try (SqlSession session = sqlSessionFactory.openSession()) {
      TestEntityMapper mapper = session.getMapper(TestEntityMapper.class);
      mapper.insert(entity);
      session.commit();

      TestEntity result = mapper.findById(3);

      assertNotNull(result);
      assertTrue(result.getActive().isPresent());
      assertFalse(result.getActive().get());
    }
  }

  @Test
  @DisplayName("一部のフィールドのみ値がある場合、正しく保存・取得できる")
  void testPartialValues() {
    TestEntity entity = new TestEntity();
    entity.setId(4);
    entity.setName(Optional.of("Partial Test"));
    entity.setAge(Optional.of(25));
    entity.setSalary(Optional.empty());
    entity.setRate(Optional.empty());
    entity.setAmount(Optional.of(new BigDecimal("999.99")));
    entity.setActive(Optional.of(true));
    entity.setBirthDate(Optional.empty());
    entity.setCreatedAt(Optional.empty());

    try (SqlSession session = sqlSessionFactory.openSession()) {
      TestEntityMapper mapper = session.getMapper(TestEntityMapper.class);
      mapper.insert(entity);
      session.commit();

      TestEntity result = mapper.findById(4);

      assertNotNull(result);
      assertTrue(result.getName().isPresent());
      assertEquals("Partial Test", result.getName().get());
      assertTrue(result.getAge().isPresent());
      assertEquals(25, result.getAge().get());
      assertFalse(result.getSalary().isPresent());
      assertFalse(result.getRate().isPresent());
      assertTrue(result.getAmount().isPresent());
      assertEquals(new BigDecimal("999.99"), result.getAmount().get());
      assertTrue(result.getActive().isPresent());
      assertTrue(result.getActive().get());
      assertFalse(result.getBirthDate().isPresent());
      assertFalse(result.getCreatedAt().isPresent());
    }
  }
}

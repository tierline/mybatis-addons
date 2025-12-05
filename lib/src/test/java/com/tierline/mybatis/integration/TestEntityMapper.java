package com.tierline.mybatis.integration;

import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper for TestEntity. */
public interface TestEntityMapper {

  void insert(TestEntity entity);

  TestEntity findById(@Param("id") Integer id);
}

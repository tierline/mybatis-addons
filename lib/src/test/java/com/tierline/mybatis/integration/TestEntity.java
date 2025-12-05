package com.tierline.mybatis.integration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

/** DTO for testing TypeHandlers with PostgreSQL. */
public class TestEntity {
  private Integer id;
  private Optional<String> name;
  private Optional<Integer> age;
  private Optional<Long> salary;
  private Optional<Double> rate;
  private Optional<BigDecimal> amount;
  private Optional<Boolean> active;
  private Optional<LocalDate> birthDate;
  private Optional<OffsetDateTime> createdAt;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Optional<String> getName() {
    return name;
  }

  public void setName(Optional<String> name) {
    this.name = name;
  }

  public Optional<Integer> getAge() {
    return age;
  }

  public void setAge(Optional<Integer> age) {
    this.age = age;
  }

  public Optional<Long> getSalary() {
    return salary;
  }

  public void setSalary(Optional<Long> salary) {
    this.salary = salary;
  }

  public Optional<Double> getRate() {
    return rate;
  }

  public void setRate(Optional<Double> rate) {
    this.rate = rate;
  }

  public Optional<BigDecimal> getAmount() {
    return amount;
  }

  public void setAmount(Optional<BigDecimal> amount) {
    this.amount = amount;
  }

  public Optional<Boolean> getActive() {
    return active;
  }

  public void setActive(Optional<Boolean> active) {
    this.active = active;
  }

  public Optional<LocalDate> getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Optional<LocalDate> birthDate) {
    this.birthDate = birthDate;
  }

  public Optional<OffsetDateTime> getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Optional<OffsetDateTime> createdAt) {
    this.createdAt = createdAt;
  }
}

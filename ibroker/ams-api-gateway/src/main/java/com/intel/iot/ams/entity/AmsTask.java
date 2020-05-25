/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "AmsTask")
public class AmsTask {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Integer id;

  @Column(nullable = false)
  private int taskType;

  @Column(length = 4096)
  private String taskProperties;

  @Column private int taskPriority;

  @Column(nullable = false)
  private Date taskCreateTime;

  @Column private int taskExpiry;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public int getTaskType() {
    return taskType;
  }

  public void setTaskType(int taskType) {
    this.taskType = taskType;
  }

  public String getTaskProperties() {
    return taskProperties;
  }

  public void setTaskProperties(String taskProperties) {
    this.taskProperties = taskProperties;
  }

  public int getTaskPriority() {
    return taskPriority;
  }

  public void setTaskPriority(int taskPriority) {
    this.taskPriority = taskPriority;
  }

  public Date getTaskCreateTime() {
    return taskCreateTime;
  }

  public void setTaskCreateTime(Date taskCreateTime) {
    this.taskCreateTime = taskCreateTime;
  }

  public int getTaskExpiry() {
    return taskExpiry;
  }

  public void setTaskExpiry(int taskExpiry) {
    this.taskExpiry = taskExpiry;
  }
}

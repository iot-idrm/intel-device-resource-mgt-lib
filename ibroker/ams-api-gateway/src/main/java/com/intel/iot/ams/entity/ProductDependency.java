/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import javax.persistence.*;

@Entity
@Table(name = "ProductDependency")
public class ProductDependency {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Integer id;

  @Column(nullable = false)
  private Integer instanceId;

  @Column(nullable = false)
  private String dependencyName;

  @Column private String minVersion;

  public Integer getId() {
    return id;
  }

  public Integer getInstanceId() {
    return instanceId;
  }

  public String getDependencyName() {
    return dependencyName;
  }

  public String getMinVersion() {
    return minVersion;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setInstanceId(Integer instanceId) {
    this.instanceId = instanceId;
  }

  public void setDependencyName(String dependencyName) {
    this.dependencyName = dependencyName;
  }

  public void setMinVersion(String minVersion) {
    this.minVersion = minVersion;
  }
}

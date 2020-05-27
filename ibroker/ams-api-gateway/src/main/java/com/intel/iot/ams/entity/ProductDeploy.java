/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import javax.persistence.*;

@Entity
@Table(name = "ProductDeploy")
public class ProductDeploy {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Integer id;

  @Column private String productDeviceId;

  @Column(nullable = false)
  private String productName;

  @Column(name = "amsClientUuid", nullable = false)
  private String clientUuid;

  @Column(nullable = false)
  private String version;

  @Column private Boolean isAot;

  @Column private String projectId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getProductDeviceId() {
    return productDeviceId;
  }

  public void setProductDeviceId(String productDeviceId) {
    this.productDeviceId = productDeviceId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getClientUuid() {
    return clientUuid;
  }

  public void setClientUuid(String clientUuid) {
    this.clientUuid = clientUuid;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Boolean getIsAot() {
    return isAot;
  }

  public void setIsAot(Boolean isAot) {
    this.isAot = isAot;
  }
}

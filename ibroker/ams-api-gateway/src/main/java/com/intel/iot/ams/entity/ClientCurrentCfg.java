/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import javax.persistence.*;

@Entity
@Table(name = "ClientCurrentCfg")
public class ClientCurrentCfg {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Integer id;

  @Column(nullable = false)
  private int clientId;

  @Column(nullable = false)
  private String productName;

  @Column(nullable = false)
  private String targetType;

  @Column private String targetId;

  @Column(nullable = false)
  private String pathName;

  @Column(nullable = false)
  private String hash;

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }

  public int getClientId() {
    return clientId;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductName() {
    return productName;
  }

  public void setTargetType(String targetType) {
    this.targetType = targetType;
  }

  public String getTargetType() {
    return targetType;
  }

  public void setTargetId(String targetId) {
    this.targetId = targetId;
  }

  public String getTargetId() {
    return targetId;
  }

  public void setPathName(String pathName) {
    this.pathName = pathName;
  }

  public String getPathName() {
    return pathName;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getHash() {
    return hash;
  }
}

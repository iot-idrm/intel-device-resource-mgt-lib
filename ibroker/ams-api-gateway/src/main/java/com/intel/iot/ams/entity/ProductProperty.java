/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import javax.persistence.*;

@Entity
@Table(name = "ProductProperty")
public class ProductProperty {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Integer id;

  @Column(nullable = false)
  private String productName;

  @Column(nullable = false)
  private String propKey;

  @Column(nullable = false)
  private String propValue;

  @Column(nullable = false)
  private int valueType;

  // TODO:
  // @Column
  // private byte[] valueBlob;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getPropKey() {
    return propKey;
  }

  public String getPropValue() {
    return propValue;
  }

  public void setPropKey(String propKey) {
    this.propKey = propKey;
  }

  public void setPropValue(String propValue) {
    this.propValue = propValue;
  }

  public int getValueType() {
    return valueType;
  }

  public void setValueType(int valueType) {
    this.valueType = valueType;
  }
}

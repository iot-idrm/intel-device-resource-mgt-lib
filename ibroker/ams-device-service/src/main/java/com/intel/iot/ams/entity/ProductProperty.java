/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.entity;

public class ProductProperty {

  private Integer id;

  private String productName;

  private String propKey;

  private String propValue;

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

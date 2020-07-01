/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.entity;

public class Product {

  private Integer id;

  private String uuid;

  private String name;

  private Integer category;

  private String description;

  private String vendor;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getCategory() {
    return category;
  }

  public void setCategory(Integer category) {
    this.category = category;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getVendor() {
    return vendor;
  }

  public void setVendor(String vendor) {
    this.vendor = vendor;
  }
}

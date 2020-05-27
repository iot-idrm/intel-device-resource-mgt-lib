/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.entity;

import java.util.Date;

public class ProductDownloadPackage {

  private Integer id;

  private String hashcode;

  private Long size;

  private String format;

  private Date genDate;

  private Date expiryDate;

  private Integer category;

  private String productName;

  private Integer fromId;

  private Integer toId;

  private Date lastUsedTime;

  private Boolean isAot;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getHashcode() {
    return hashcode;
  }

  public void setHashcode(String hashcode) {
    this.hashcode = hashcode;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public Date getGenDate() {
    return genDate;
  }

  public void setGenDate(Date genDate) {
    this.genDate = genDate;
  }

  public Date getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }

  public Integer getCategory() {
    return category;
  }

  public void setCategory(Integer category) {
    this.category = category;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Integer getFromId() {
    return fromId;
  }

  public void setFromId(Integer fromId) {
    this.fromId = fromId;
  }

  public Integer getToId() {
    return toId;
  }

  public void setToId(Integer toId) {
    this.toId = toId;
  }

  public Date getLastUsedTime() {
    return lastUsedTime;
  }

  public void setLastUsedTime(Date lastUsedTime) {
    this.lastUsedTime = lastUsedTime;
  }

  public Boolean getIsAot() {
    return isAot;
  }

  public void setIsAot(Boolean isAot) {
    this.isAot = isAot;
  }

}

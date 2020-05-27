/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "ProductDownloadPackage")
public class ProductDownloadPackage {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Integer id;

  @Column(nullable = false)
  private String hashcode;

  @Column(nullable = false)
  private Long size;

  @Column(nullable = false)
  private String format;

  @Column(nullable = false)
  private Date genDate;

  @Column private Date expiryDate;

  @Column(nullable = false)
  private int category;

  @Column(nullable = false)
  private String productName;

  @Column private Integer fromId;

  @Column(nullable = false)
  private Integer toId;

  @Column(nullable = false)
  private Date lastUsedTime;

  @Column private boolean isAot;

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

  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
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

  public boolean isAot() {
    return isAot;
  }

  public void setAot(boolean isAot) {
    this.isAot = isAot;
  }
}

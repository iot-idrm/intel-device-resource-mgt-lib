/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "ProductDownloadHistory")
public class ProductDownloadHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Integer id;

  @Column(nullable = false)
  private String amsClientUuid;

  @Column(nullable = false)
  private String productName;

  @Column(nullable = false)
  private int category;

  @Column private Integer fromId;

  @Column(nullable = false)
  private Integer toId;

  @Column(nullable = false)
  private Date downloadTime;

  @Column private Boolean isAot;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getAmsClientUuid() {
    return amsClientUuid;
  }

  public void setAmsClientUuid(String amsClientUuid) {
    this.amsClientUuid = amsClientUuid;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
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

  public Date getDownloadTime() {
    return downloadTime;
  }

  public void setDownloadTime(Date downloadTime) {
    this.downloadTime = downloadTime;
  }

  public Boolean isAot() {
    return isAot;
  }

  public void setAot(Boolean isAot) {
    this.isAot = isAot;
  }
}

/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.entity;

import java.util.Date;

public class ProductDownloadHistory {

  private Integer id;

  private String amsClientUuid;

  private String productName;

  private int category;

  private Integer fromId;

  private Integer toId;

  private Date downloadTime;

  private Boolean isAot;

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

  public Boolean getIsAot() {
    return isAot;
  }

  public void setIsAot(Boolean isAot) {
    this.isAot = isAot;
  }

}

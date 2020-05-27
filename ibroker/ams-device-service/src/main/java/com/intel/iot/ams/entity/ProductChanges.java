/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.entity;

import java.util.Date;

public class ProductChanges {

  private Integer id;

  private String clientUuid;

  private String downloadId;

  private String productName;

  private Date enableTime;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getClientUuid() {
    return clientUuid;
  }

  public void setClientUuid(String clientUuid) {
    this.clientUuid = clientUuid;
  }

  public String getDownloadId() {
    return downloadId;
  }

  public void setDownloadId(String downloadId) {
    this.downloadId = downloadId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Date getEnableTime() {
    return enableTime;
  }

  public void setEnableTime(Date enableTime) {
    this.enableTime = enableTime;
  }



}

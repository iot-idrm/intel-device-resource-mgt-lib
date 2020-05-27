/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.entity;


public class ClientDeviceMapping {

  private Integer id;

  private String clientUuid;

  private String productName;

  private String productDeviceId;

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

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductDeviceId() {
    return productDeviceId;
  }

  public void setProductDeviceId(String productDeviceId) {
    this.productDeviceId = productDeviceId;
  }

}

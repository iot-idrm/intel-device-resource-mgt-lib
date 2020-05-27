/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.requestbody;

import com.google.gson.annotations.SerializedName;

public class ClientDeviceMappingRequest {

  @SerializedName("short_id")
  private String shortId;

  @SerializedName("product_name")
  private String productName;

  @SerializedName("product_device_id")
  private String productDeviceId;

  public String getShortId() {
    return shortId;
  }

  public void setShortId(String shortId) {
    this.shortId = shortId;
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

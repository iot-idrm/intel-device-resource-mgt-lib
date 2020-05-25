/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api.requestbody;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddProductForClientInfo {

  @JsonProperty(value = "client_uuid")
  private String clientUuid;

  @JsonProperty(value = "product_device_id")
  private String deviceId;

  @JsonProperty(value = "product_name")
  private String productName;

  @JsonProperty(value = "version")
  private String version;

  @JsonProperty(value = "aot_enable")
  private Boolean aotEnable;

  @JsonProperty(value = "category")
  private String category;

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getClientUuid() {
    return clientUuid;
  }

  public String getProductName() {
    return productName;
  }

  public String getVersion() {
    return version;
  }

  public void setClientUuid(String clientUuid) {
    this.clientUuid = clientUuid;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public Boolean getAotEnable() {
    return aotEnable;
  }

  public void setAotEnable(Boolean aotEnable) {
    this.aotEnable = aotEnable;
  }
}

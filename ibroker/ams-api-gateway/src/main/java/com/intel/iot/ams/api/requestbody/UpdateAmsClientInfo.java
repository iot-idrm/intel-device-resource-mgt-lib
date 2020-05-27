/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api.requestbody;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateAmsClientInfo {
  @JsonProperty(value = "client_uuid")
  private String clientUuid;

  @JsonProperty(value = "description")
  private String description;

  @JsonProperty(value = "device_name")
  private String deviceName;

  @JsonProperty(value = "product_lock")
  private Boolean productLock;

  public String getClientUuid() {
    return clientUuid;
  }

  public void setClientUuid(String clientUuid) {
    this.clientUuid = clientUuid;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public Boolean getProductLock() {
    return productLock;
  }

  public void setProductLock(Boolean productLock) {
    this.productLock = productLock;
  }
}

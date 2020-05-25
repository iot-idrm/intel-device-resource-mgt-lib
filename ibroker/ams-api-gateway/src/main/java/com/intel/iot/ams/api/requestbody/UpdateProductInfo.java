/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api.requestbody;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateProductInfo {
  @JsonProperty(value = "product_name")
  private String productName;

  @JsonProperty(value = "description")
  private String description;

  @JsonProperty(value = "default_version")
  private String defaultVersion;

  public String getProductName() {
    return productName;
  }

  public String getDescription() {
    return description;
  }

  public String getDefaultVersion() {
    return defaultVersion;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setDefaultVersion(String defaultVersion) {
    this.defaultVersion = defaultVersion;
  }
}

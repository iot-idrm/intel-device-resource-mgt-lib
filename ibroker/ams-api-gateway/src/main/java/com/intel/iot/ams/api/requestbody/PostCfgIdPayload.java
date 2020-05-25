/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api.requestbody;

import com.google.gson.annotations.SerializedName;

public class PostCfgIdPayload {

  @SerializedName(value = "default_content")
  private String content;

  @SerializedName(value = "schema")
  private String schema;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }
}

/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TemplateItem {
  @SerializedName("product_name")
  private String productName;

  @SerializedName("version")
  private String version;

  @SerializedName("configurations")
  private List<TemplateConfigItem> cfgs;

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public List<TemplateConfigItem> getCfgs() {
    return cfgs;
  }

  public void setCfgs(List<TemplateConfigItem> cfgs) {
    this.cfgs = cfgs;
  }

  public class TemplateConfigItem {
    @SerializedName("path_name")
    private String pathName;

    @SerializedName("cfg_type")
    private String cfgType;

    @SerializedName("content_name")
    private String contentName;

    public String getPathName() {
      return pathName;
    }

    public void setPathName(String pathName) {
      this.pathName = pathName;
    }

    public String getContentName() {
      return contentName;
    }

    public void setContentName(String contentName) {
      this.contentName = contentName;
    }

    public String getCfgType() {
      return cfgType;
    }

    public void setCfgType(String cfgType) {
      this.cfgType = cfgType;
    }
  }
}

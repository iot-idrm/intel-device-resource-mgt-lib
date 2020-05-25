/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.requestbody;


import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PostAllCfgInfo {

  @SerializedName("pn")
  private String productName;

  @SerializedName("tt")
  private String targetType;

  @SerializedName("tid")
  private String targetId;

  @SerializedName("current_cfgs")
  private List<CurrentCfg> cfgs;

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getTargetType() {
    return targetType;
  }

  public void setTargetType(String targetType) {
    this.targetType = targetType;
  }

  public String getTargetId() {
    return targetId;
  }

  public void setTargetId(String targetId) {
    this.targetId = targetId;
  }

  public List<CurrentCfg> getCfgs() {
    return cfgs;
  }

  public void setCfgs(List<CurrentCfg> cfgs) {
    this.cfgs = cfgs;
  }

  public class CurrentCfg {
    @SerializedName("path")
    private String path;

    @SerializedName("h")
    private String hash;

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public String getHash() {
      return hash;
    }

    public void setHash(String hash) {
      this.hash = hash;
    }

  }
}

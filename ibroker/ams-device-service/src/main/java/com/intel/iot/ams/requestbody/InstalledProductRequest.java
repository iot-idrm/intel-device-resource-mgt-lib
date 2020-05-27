/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.requestbody;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class InstalledProductRequest {
  @SerializedName("short_id")
  private String shortId;

  @SerializedName("ams_version")
  private String amsVersion;

  @SerializedName("device_type")
  private String deviceType;

  @SerializedName("fw_version")
  private String fwVersion;

  @SerializedName("wasm")
  private WasmInfo wasm;

  @SerializedName("installed_product_list")
  private List<ProductInfo> productList;

  public String getShortId() {
    return shortId;
  }

  public void setShortId(String shortId) {
    this.shortId = shortId;
  }

  public String getAmsVersion() {
    return amsVersion;
  }

  public void setAmsVersion(String amsVersion) {
    this.amsVersion = amsVersion;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public String getFwVersion() {
    return fwVersion;
  }

  public void setFwVersion(String fwVersion) {
    this.fwVersion = fwVersion;
  }

  public WasmInfo getWasm() {
    return wasm;
  }

  public void setWasm(WasmInfo wasm) {
    this.wasm = wasm;
  }

  public List<ProductInfo> getProductList() {
    return productList;
  }

  public void setProductList(List<ProductInfo> productList) {
    this.productList = productList;
  }

  public static class WasmInfo {

    @SerializedName("bytecode")
    private Boolean bytecode;

    @SerializedName("aot")
    private Boolean aot;

    @SerializedName("api_version")
    private Integer apiVersion;

    public Boolean getBytecode() {
      return bytecode;
    }

    public void setBytecode(Boolean bytecode) {
      this.bytecode = bytecode;
    }

    public Boolean getAot() {
      return aot;
    }

    public void setAot(Boolean aot) {
      this.aot = aot;
    }

    public Integer getApiVersion() {
      return apiVersion;
    }

    public void setApiVersion(Integer apiVersion) {
      this.apiVersion = apiVersion;
    }

  }

  public static class ProductInfo {
    @SerializedName("product_name")
    private String productName;

    @SerializedName("version")
    private String version;

    @SerializedName("aot")
    private Boolean aot;

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

    public Boolean getAot() {
      return aot;
    }

    public void setAot(Boolean aot) {
      this.aot = aot;
    }

  }

}

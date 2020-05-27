/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.requestbody;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ProductInstanceManifest {

  @SerializedName(value = "product_name")
  private String productName;

  @SerializedName(value = "category")
  private String category;

  @SerializedName(value = "version")
  private String version;

  @SerializedName(value = "host_product_name")
  private String hostName;

  @SerializedName(value = "description")
  private String description;

  @SerializedName(value = "vendor")
  private String vendor;

  @SerializedName(value = "cpu")
  private String cpu;

  @SerializedName(value = "platform")
  private String platform;

  @SerializedName(value = "os")
  private String os;

  @SerializedName(value = "dependencies")
  private List<DependencyInfo> dependencyList;

  @SerializedName(value = "components")
  private List<ComponentInfo> componentList;

  @SerializedName(value = "aot_enable")
  private Boolean aotEnable;

  @SerializedName(value = "wasm_enable")
  private Boolean wasmEnable;

  @SerializedName(value = "wasm_version")
  private Integer wasmVersion;

  @SerializedName(value = "min_wasm_version")
  private Integer minWasmVersion;

  public String getProductName() {
    return productName;
  }

  public String getCategory() {
    return category;
  }

  public String getVersion() {
    return version;
  }

  public String getDescription() {
    return description;
  }

  public String getVendor() {
    return vendor;
  }

  public String getCpu() {
    return cpu;
  }

  public String getPlatform() {
    return platform;
  }

  public String getOs() {
    return os;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  public void setCpu(String cpu) {
    this.cpu = cpu;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public Boolean getWasmEnable() {
    return wasmEnable;
  }

  public void setWasmEnable(Boolean wasmEnable) {
    this.wasmEnable = wasmEnable;
  }

  public Integer getWasmVersion() {
    return wasmVersion;
  }

  public void setWasmVersion(Integer wasmVersion) {
    this.wasmVersion = wasmVersion;
  }

  public Integer getMinWasmVersion() {
    return minWasmVersion;
  }

  public void setMinWasmVersion(Integer minWasmVersion) {
    this.minWasmVersion = minWasmVersion;
  }

  public List<DependencyInfo> getDependencyList() {
    return dependencyList;
  }

  public List<ComponentInfo> getComponentList() {
    return componentList;
  }

  public void setDependencyList(List<DependencyInfo> dependencyList) {
    this.dependencyList = dependencyList;
  }

  public void setComponentList(List<ComponentInfo> componentList) {
    this.componentList = componentList;
  }

  public String getHostName() {
    return hostName;
  }

  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  public Boolean getAotEnable() {
    return aotEnable;
  }

  public void setAotEnable(Boolean aotEnable) {
    this.aotEnable = aotEnable;
  }

  public class DependencyInfo {
    @SerializedName("product_name")
    private String productName;

    @SerializedName("min_version")
    private String minVersion;

    public String getProductName() {
      return productName;
    }

    public String getMinVersion() {
      return minVersion;
    }

    public void setProductName(String productName) {
      this.productName = productName;
    }

    public void setMinVersion(String minVersion) {
      this.minVersion = minVersion;
    }
  }

  public class ComponentInfo {
    @SerializedName("f")
    private String pathName;

    @SerializedName("h")
    private String hash;

    @SerializedName("v")
    private String version;

    public String getPathName() {
      return pathName;
    }

    public String getHash() {
      return hash;
    }

    public void setPathName(String pathName) {
      this.pathName = pathName;
    }

    public String getVersion() {
      return version;
    }

    public void setHash(String hash) {
      this.hash = hash;
    }

    public void setVersion(String version) {
      this.version = version;
    }
  }

}

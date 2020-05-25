/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api.requestbody;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InstallationPackageInfo {

  @SerializedName(value = "product_name")
  private String productName;

  @SerializedName(value = "category")
  private String category;

  @SerializedName(value = "host_product_name")
  private String hostName;

  @SerializedName(value = "version")
  private String version;

  @SerializedName(value = "description")
  private String description;

  @SerializedName(value = "vendor")
  private String vendor;

  @SerializedName(value = "product_properties")
  private List<PropertyItem> propertyList;

  @SerializedName(value = "cfg_id_list")
  private List<CfgIdInfo> cfgIdList;

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

  public List<PropertyItem> getPropertyList() {
    return propertyList;
  }

  public void setPropertyList(List<PropertyItem> propertyList) {
    this.propertyList = propertyList;
  }

  public List<CfgIdInfo> getCfgIdList() {
    return cfgIdList;
  }

  public void setCfgIdList(List<CfgIdInfo> cfgIdList) {
    this.cfgIdList = cfgIdList;
  }

  public String getHostName() {
    return hostName;
  }

  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  public class PropertyItem {
    @SerializedName("key")
    private String key;

    @SerializedName("value")
    private String value;

    @SerializedName("value_type")
    private Integer valueType;

    public String getKey() {
      return key;
    }

    public String getValue() {
      return value;
    }

    public Integer getValueType() {
      return valueType;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public void setValueType(Integer valueType) {
      this.valueType = valueType;
    }
  }

  public class CfgIdInfo {
    @SerializedName("path_name")
    private String pathName;

    @SerializedName("target_type")
    private String targetType;

    @SerializedName("default_content")
    private String defaultContent;

    public String getPathName() {
      return pathName;
    }

    public void setPathName(String pathName) {
      this.pathName = pathName;
    }

    public String getTargetType() {
      return targetType;
    }

    public void setTargetType(String targetType) {
      this.targetType = targetType;
    }

    public String getDefaultContent() {
      return defaultContent;
    }

    public void setDefaultContent(String defaultContent) {
      this.defaultContent = defaultContent;
    }
  }
}

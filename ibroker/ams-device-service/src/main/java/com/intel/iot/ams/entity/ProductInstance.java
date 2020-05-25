/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.entity;


import java.util.Date;

public class ProductInstance {

  private Integer instanceId;

  private String instanceName;

  private String productName;

  private String version;

  private String cpu;

  private String platform;

  private String os;

  private String osMin;

  private String system;

  private String sysMin;

  private String bits;

  private String description;

  private String dependencyList;

  private String metadata;

  private Boolean aotEnable;

  private Boolean wasmEnable;

  private Integer wasmVersion;

  private Integer minWasmVersion;

  private Date uploadTime;

  public Date getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(Date uploadTime) {
    this.uploadTime = uploadTime;
  }


  public Integer getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(Integer instanceId) {
    this.instanceId = instanceId;
  }

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

  public String getCpu() {
    return cpu;
  }

  public void setCpu(String cpu) {
    this.cpu = cpu;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDependencyList() {
    return dependencyList;
  }

  public void setDependencyList(String dependencyList) {
    this.dependencyList = dependencyList;
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  public String getOsMin() {
    return osMin;
  }

  public void setOsMin(String osMin) {
    this.osMin = osMin;
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getSysMin() {
    return sysMin;
  }

  public void setSysMin(String sysMin) {
    this.sysMin = sysMin;
  }

  public String getBits() {
    return bits;
  }

  public void setBits(String bits) {
    this.bits = bits;
  }

  public String getInstanceName() {
    return instanceName;
  }

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }

  public Boolean getAotEnable() {
    return aotEnable;
  }

  public void setAotEnable(Boolean aotEnable) {
    this.aotEnable = aotEnable;
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

}

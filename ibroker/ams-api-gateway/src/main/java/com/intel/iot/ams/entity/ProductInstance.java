/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "ProductInstance")
public class ProductInstance {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Integer instanceId;

  @Column(nullable = false)
  private String instanceName;

  @Column(nullable = false)
  private String productName;

  @Column(nullable = false)
  private String version;

  @Column(nullable = false)
  private Date uploadTime;

  @Column private String cpu;

  @Column private String platform;

  @Column private String os;

  @Column private String osMin;

  @Column private String system;

  @Column private String sysMin;

  @Column private String bits;

  @Column private String description;

  @Column private String dependencyList;

  @Lob
  @Column(nullable = false)
  private String metadata;

  @Column private Boolean aotEnable;

  @Column private Boolean wasmEnable;

  @Column private Integer wasmVersion;

  @Column private Integer minWasmVersion;

  public Integer getMinWasmVersion() {
    return minWasmVersion;
  }

  public void setMinWasmVersion(Integer minWasmVersion) {
    this.minWasmVersion = minWasmVersion;
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

  public Date getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(Date uploadTime) {
    this.uploadTime = uploadTime;
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

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  public String getDependencyList() {
    return dependencyList;
  }

  public void setDependencyList(String dependencyList) {
    this.dependencyList = dependencyList;
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
}

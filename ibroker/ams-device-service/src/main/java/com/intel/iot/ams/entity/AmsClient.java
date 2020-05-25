/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.entity;

import java.util.Date;

public class AmsClient {

  private Integer id;

  private String clientUuid;

  private String templateName;

  private String deviceName;

  private String amsClientVersion;

  private String cpu;

  private String platform;

  private String os;

  private String osVer;

  private String system;

  private String sysVer;

  private String bits;

  private String serial;

  private String description;

  private Date provisionTime;

  private Date lastProductUpdateTime;

  private Date lastConfigUpdateTime;

  private Date lastConnectionTime;

  private String fwVersion;

  private String deviceType;

  private Boolean aotEnable;

  private Boolean wasmEnable;

  private Integer wasmVersion;

  private Boolean productLock;

  private String projectId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getClientUuid() {
    return clientUuid;
  }

  public void setClientUuid(String clientUuid) {
    this.clientUuid = clientUuid;
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

  public String getSerial() {
    return serial;
  }

  public void setSerial(String serial) {
    this.serial = serial;
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

  public Date getProvisionTime() {
    return provisionTime;
  }

  public void setProvisionTime(Date provisionTime) {
    this.provisionTime = provisionTime;
  }

  public Date getLastProductUpdateTime() {
    return lastProductUpdateTime;
  }

  public void setLastProductUpdateTime(Date lastProductUpdateTime) {
    this.lastProductUpdateTime = lastProductUpdateTime;
  }

  public Date getLastConfigUpdateTime() {
    return lastConfigUpdateTime;
  }

  public void setLastConfigUpdateTime(Date lastConfigUpdateTime) {
    this.lastConfigUpdateTime = lastConfigUpdateTime;
  }

  public Date getLastConnectionTime() {
    return lastConnectionTime;
  }

  public void setLastConnectionTime(Date lastConnectionTime) {
    this.lastConnectionTime = lastConnectionTime;
  }

  public String getOsVer() {
    return osVer;
  }

  public void setOsVer(String osVer) {
    this.osVer = osVer;
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getSysVer() {
    return sysVer;
  }

  public void setSysVer(String sysVer) {
    this.sysVer = sysVer;
  }

  public String getBits() {
    return bits;
  }

  public void setBits(String bits) {
    this.bits = bits;
  }

  public String getAmsClientVersion() {
    return amsClientVersion;
  }

  public void setAmsClientVersion(String amsClientVersion) {
    this.amsClientVersion = amsClientVersion;
  }

  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public String getFwVersion() {
    return fwVersion;
  }

  public void setFwVersion(String fwVersion) {
    this.fwVersion = fwVersion;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public Boolean getAotEnable() {
    return aotEnable;
  }

  public void setAotEnable(Boolean aotEnable) {
    this.aotEnable = aotEnable;
  }

  public Boolean getProductLock() {
    return productLock;
  }

  public void setProductLock(Boolean productLock) {
    this.productLock = productLock;
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

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

}

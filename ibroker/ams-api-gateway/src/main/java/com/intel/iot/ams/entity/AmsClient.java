/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "AmsClient")
public class AmsClient {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Integer id;

  @Column(name = "amsClientUuid", unique = true, nullable = false)
  private String clientUuid;

  @Column private String projectId;

  @Column private String templateName;

  @Column private String deviceName;

  @Column private String amsClientVersion;

  @Column(nullable = false)
  private String cpu;

  @Column private String platform;

  @Column(nullable = false)
  private String os;

  @Column private String osVer;

  @Column private String system;

  @Column private String sysVer;

  @Column(nullable = false)
  private String bits;

  @Column(unique = true, nullable = false)
  private String serial;

  @Column(length = 4096)
  private String description;

  @Column(nullable = false)
  private Date provisionTime;

  @Column private Date lastProductUpdateTime;

  @Column private Date lastConfigUpdateTime;

  @Column private Date lastConnectionTime;

  @Column private String fwVersion;

  @Column private String deviceType;

  @Column private Boolean aotEnable;

  @Column private Boolean wasmEnable;

  @Column private Integer wasmVersion;

  @Column private Boolean productLock;

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

  @Override
  public String toString() {
    return "AmsClient{"
        + "id="
        + id
        + ", clientUUID='"
        + clientUuid
        + '\''
        + ", projectId='"
        + projectId
        + '\''
        + ", templateName='"
        + templateName
        + '\''
        + ", deviceName='"
        + deviceName
        + '\''
        + ", amsClientVersion='"
        + amsClientVersion
        + '\''
        + ", cpu='"
        + cpu
        + '\''
        + ", platform='"
        + platform
        + '\''
        + ", os='"
        + os
        + '\''
        + ", osVer='"
        + osVer
        + '\''
        + ", system='"
        + system
        + '\''
        + ", sysVer='"
        + sysVer
        + '\''
        + ", bits='"
        + bits
        + '\''
        + ", serial='"
        + serial
        + '\''
        + ", description='"
        + description
        + '\''
        + ", provisionTime="
        + provisionTime
        + ", lastProductUpdateTime="
        + lastProductUpdateTime
        + ", lastConfigUpdateTime="
        + lastConfigUpdateTime
        + ", lastConnectionTime="
        + lastConnectionTime
        + ", fwVersion='"
        + fwVersion
        + '\''
        + ", deviceType='"
        + deviceType
        + '\''
        + ", aotEnable="
        + aotEnable
        + ", wasmEnable="
        + wasmEnable
        + ", wasmVersion="
        + wasmVersion
        + ", productLock="
        + productLock
        + '}';
  }
}

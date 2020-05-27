/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.requestbody;


import com.google.gson.annotations.SerializedName;

public class ClientProvisioningRequest {

  @SerializedName("di")
  private String Di;

  @SerializedName("hardware_serial")
  private String serial;

  @SerializedName("cpu")
  private String cpu;

  @SerializedName("platform")
  private String platform;

  @SerializedName("ams_version")
  private String amsVersion;

  @SerializedName("fw_version")
  private String fwVersion;

  @SerializedName("device_type")
  private String deviceType;

  @SerializedName("os")
  private String os;

  @SerializedName("os_ver")
  private String osVer;

  @SerializedName("system")
  private String system;

  @SerializedName("sys_ver")
  private String sysVer;

  @SerializedName("bits")
  private String bits;

  @SerializedName("description")
  private String description;

  @SerializedName("device_name")
  private String deviceName;

  @SerializedName("template")
  private String template;

  @SerializedName("aot_enable")
  private Boolean aotEnable;

  @SerializedName("wasm_enable")
  private Boolean wasmEnable;

  @SerializedName("wasmVersion")
  private Integer wasmVersion;

  public String getDi() {
    return Di;
  }

  public void setDi(String Di) {
    this.Di = Di;
  }

  public String getSerial() {
    return serial;
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

  public String getDescription() {
    return description;
  }

  public void setSerial(String serial) {
    this.serial = serial;
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

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
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

  public String getAmsVersion() {
    return amsVersion;
  }

  public void setAmsVersion(String amsVersion) {
    this.amsVersion = amsVersion;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public String getFwVersion() {
    return fwVersion;
  }

  public void setFwVersion(String fwVersion) {
    this.fwVersion = fwVersion;
  }

  public Boolean getAotEnable() {
    return aotEnable;
  }

  public void setAotEnable(Boolean aotEnable) {
    this.aotEnable = aotEnable;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
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

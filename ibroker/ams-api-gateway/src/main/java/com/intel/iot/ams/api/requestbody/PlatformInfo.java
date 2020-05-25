/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api.requestbody;

import com.google.gson.annotations.SerializedName;

public class PlatformInfo {

  @SerializedName(value = "cpu")
  private String cpu;

  @SerializedName(value = "os")
  private String os;

  @SerializedName(value = "os_min")
  private String osMin;

  @SerializedName(value = "system")
  private String system;

  @SerializedName(value = "sys_min")
  private String sysMin;

  @SerializedName(value = "bits")
  private String bits;

  public String getCpu() {
    return cpu;
  }

  public void setCpu(String cpu) {
    this.cpu = cpu;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
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
}

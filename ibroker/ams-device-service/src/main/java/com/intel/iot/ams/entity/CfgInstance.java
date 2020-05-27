/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.entity;

public class CfgInstance {

  private Integer id;

  private String cfgUuid;

  private String targetId;

  private Integer contentId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getCfgUuid() {
    return cfgUuid;
  }

  public void setCfgUuid(String cfgUuid) {
    this.cfgUuid = cfgUuid;
  }

  public String getTargetId() {
    return targetId;
  }

  public void setTargetId(String targetId) {
    this.targetId = targetId;
  }

  public Integer getContentId() {
    return contentId;
  }

  public void setContentId(Integer contentId) {
    this.contentId = contentId;
  }

}

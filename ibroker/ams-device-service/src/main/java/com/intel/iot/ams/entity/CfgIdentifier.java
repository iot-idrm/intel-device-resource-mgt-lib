/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.entity;

public class CfgIdentifier {

  private Integer id;

  private String cfgUuid;

  private int type;

  private String userName;

  private String pathName;

  private String targetType;

  private Integer defaultContentId;

  private String description;

  private String schema;

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

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

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

  public Integer getDefaultContentId() {
    return defaultContentId;
  }

  public void setDefaultContentId(Integer defaultContentId) {
    this.defaultContentId = defaultContentId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }
}

/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.entity;


import java.util.Date;

public class HistoricCfgInstance {

  private Integer id;

  private String cfgUuid;

  private String userName;

  private String pathName;

  private String targetType;

  private Integer instanceId;

  private String targetId;

  private Integer contentId;

  private int contentType;

  private String sharedName;

  private byte[] content;

  private String contentHash;

  private String formatId;

  private Date startTime;

  private Date endTime;

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

  public Integer getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(Integer instanceId) {
    this.instanceId = instanceId;
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

  public int getContentType() {
    return contentType;
  }

  public void setContentType(int contentType) {
    this.contentType = contentType;
  }

  public String getSharedName() {
    return sharedName;
  }

  public void setSharedName(String sharedName) {
    this.sharedName = sharedName;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public String getContentHash() {
    return contentHash;
  }

  public void setContentHash(String contentHash) {
    this.contentHash = contentHash;
  }

  public String getFormatId() {
    return formatId;
  }

  public void setFormatId(String formatId) {
    this.formatId = formatId;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

}

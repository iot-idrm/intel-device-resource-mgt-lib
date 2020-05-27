/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "HistoricCfgInstance")
public class HistoricCfgInstance {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Integer id;

  @Column(nullable = false)
  private String cfgUuid;

  @Column(nullable = false)
  private String userName;

  @Column(nullable = false)
  private String pathName;

  @Column(nullable = false)
  private String targetType;

  @Column(nullable = false)
  private Integer instanceId;

  @Column(nullable = false)
  private String targetId;

  @Column(nullable = false)
  private Integer contentId;

  @Column(nullable = false)
  private int contentType;

  @Column private String sharedName;

  @Lob
  @Column(nullable = false)
  private byte[] content;

  @Column(nullable = false)
  private String contentHash;

  @Column private String formatId;

  @Column private Date startTime;

  @Column private Date endTime;

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

  public Integer getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(Integer instanceId) {
    this.instanceId = instanceId;
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

  public String getTargetId() {
    return targetId;
  }

  public void setTargetId(String targetId) {
    this.targetId = targetId;
  }
}

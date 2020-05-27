/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import javax.persistence.*;

@Entity
@Table(name = "CfgContent")
public class CfgContent {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Integer id;

  @Column(nullable = false)
  private int contentType;

  @Column private String sharedName;

  @Lob
  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private String contentHash;

  @Column private String formatId;

  @Column private int expirationTime;

  @Column private String tag;

  @Column private String description;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
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

  public int getExpirationTime() {
    return expirationTime;
  }

  public void setExpirationTime(int expirationTime) {
    this.expirationTime = expirationTime;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}

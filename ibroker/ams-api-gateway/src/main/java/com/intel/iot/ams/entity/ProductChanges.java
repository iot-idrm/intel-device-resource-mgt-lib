/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.entity;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "ProductChanges")
public class ProductChanges {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(unique = true, nullable = false)
  private Integer id;

  @Column(name = "amsClientUuid", nullable = false)
  private String clientUuid;

  @Column private String downloadId;

  @Column private String productName;

  @Column private Date enableTime;

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

  public String getDownloadId() {
    return downloadId;
  }

  public void setDownloadId(String downloadId) {
    this.downloadId = downloadId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Date getEnableTime() {
    return enableTime;
  }

  public void setEnableTime(Date enableTime) {
    this.enableTime = enableTime;
  }
}

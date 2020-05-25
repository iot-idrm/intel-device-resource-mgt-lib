/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.McuDownloadImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface McuDownloadImageDao extends JpaRepository<McuDownloadImage, Integer> {

  public McuDownloadImage findByHashcode(String hash);

  public McuDownloadImage findByIdList(String idList);
}

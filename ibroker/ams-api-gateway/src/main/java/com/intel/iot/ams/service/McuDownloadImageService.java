/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.McuDownloadImage;
import com.intel.iot.ams.repository.McuDownloadImageDao;
import org.springframework.beans.factory.annotation.Autowired;

public class McuDownloadImageService extends BaseService<Integer, McuDownloadImage> {

  @Autowired private McuDownloadImageDao mcuImageDao;

  public McuDownloadImage findByHash(String hash) {
    return mcuImageDao.findByHashcode(hash);
  }

  public McuDownloadImage findByIdList(String idList) {
    return mcuImageDao.findByIdList(idList);
  }
}

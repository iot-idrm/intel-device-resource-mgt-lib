/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.McuDownloadImage;
import com.intel.iot.ams.repository.McuDownloadImageDao;
import com.intel.iot.ams.repository.hibernate.McuDownloadImageDaoImpl;

public class McuDownloadImageService extends BaseService<Integer, McuDownloadImage> {

  private McuDownloadImageDao mcuImageDao;

  public McuDownloadImageService() {
    mcuImageDao = new McuDownloadImageDaoImpl();
    super.setDao(mcuImageDao);
  }

  public McuDownloadImage findByHash(String hash) {
    return mcuImageDao.findByHash(hash);
  }

  public McuDownloadImage findByIdList(String idList) {
    return mcuImageDao.findByIdList(idList);
  }

}

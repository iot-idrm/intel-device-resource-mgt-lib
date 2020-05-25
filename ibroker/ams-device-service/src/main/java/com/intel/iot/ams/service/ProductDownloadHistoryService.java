/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.util.List;
import com.intel.iot.ams.entity.ProductDownloadHistory;
import com.intel.iot.ams.repository.ProductDownloadHistoryDao;
import com.intel.iot.ams.repository.hibernate.ProductDownloadHistoryDaoImpl;

public class ProductDownloadHistoryService extends BaseService<Integer, ProductDownloadHistory> {

  private ProductDownloadHistoryDao downloadHistoryDao;

  public ProductDownloadHistoryService() {
    downloadHistoryDao = new ProductDownloadHistoryDaoImpl();
    super.setDao(downloadHistoryDao);
  }

  public List<ProductDownloadHistory> findByClientUuid(String uuid) {
    return downloadHistoryDao.findByClientUuid(uuid);
  }

  public List<ProductDownloadHistory> findByClientUuidAndProductName(String uuid,
                                                                     String productName) {
    return downloadHistoryDao.findByClientUuidAndProductName(uuid, productName);
  }

}

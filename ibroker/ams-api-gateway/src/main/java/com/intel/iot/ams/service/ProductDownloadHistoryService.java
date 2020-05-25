/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ProductDownloadHistory;
import com.intel.iot.ams.repository.ProductDownloadHistoryDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductDownloadHistoryService extends BaseService<Integer, ProductDownloadHistory> {

  @Autowired private ProductDownloadHistoryDao downloadHistoryDao;

  public List<ProductDownloadHistory> findByClientUuid(String uuid) {
    return downloadHistoryDao.findByAmsClientUuid(uuid);
  }

  public List<ProductDownloadHistory> findByClientUuidAndProductName(
      String uuid, String productName) {
    return downloadHistoryDao.findByAmsClientUuidAndProductName(uuid, productName);
  }

  public void removeByClientUuid(String clientUuid) {
    downloadHistoryDao.removeByAmsClientUuid(clientUuid);
  }
}

/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ProductDownloadHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDownloadHistoryDao extends JpaRepository<ProductDownloadHistory, Integer> {

  public List<ProductDownloadHistory> findByAmsClientUuid(String uuid);

  public List<ProductDownloadHistory> findByAmsClientUuidAndProductName(
      String uuid, String productName);

  public void removeByAmsClientUuid(String clientUuid);
}

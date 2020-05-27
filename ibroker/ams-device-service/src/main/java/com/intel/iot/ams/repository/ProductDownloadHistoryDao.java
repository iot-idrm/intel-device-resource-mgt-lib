/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import java.util.List;
import com.intel.iot.ams.entity.ProductDownloadHistory;

public interface ProductDownloadHistoryDao extends Dao<Integer, ProductDownloadHistory> {

  public List<ProductDownloadHistory> findByClientUuid(String uuid);

  public List<ProductDownloadHistory> findByClientUuidAndProductName(String uuid,
                                                                     String productName);

}

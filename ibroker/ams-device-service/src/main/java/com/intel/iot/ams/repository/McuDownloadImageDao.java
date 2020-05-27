/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;


import com.intel.iot.ams.entity.McuDownloadImage;


public interface McuDownloadImageDao extends Dao<Integer, McuDownloadImage> {

  public McuDownloadImage findByHash(String hash);

  public McuDownloadImage findByIdList(String idList);
}

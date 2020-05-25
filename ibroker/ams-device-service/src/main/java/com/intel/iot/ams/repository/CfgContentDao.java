/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.CfgContent;

public interface CfgContentDao extends Dao<Integer, CfgContent> {

  public CfgContent findBySharedName(String name);

  public CfgContent findByHash(String hash);
}

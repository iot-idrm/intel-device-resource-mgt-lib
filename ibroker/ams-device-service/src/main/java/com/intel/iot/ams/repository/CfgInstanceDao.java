/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.CfgInstance;
import java.util.List;

public interface CfgInstanceDao extends Dao<Integer, CfgInstance> {

  public List<CfgInstance> findByCfgIdentifierUUID(String uuid);

  public CfgInstance findByCfgIdentifierUUIDAndTargetId(String uuid, String targetId);
}

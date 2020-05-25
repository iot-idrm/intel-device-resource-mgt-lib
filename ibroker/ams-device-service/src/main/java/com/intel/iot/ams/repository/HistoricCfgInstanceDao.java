/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.HistoricCfgInstance;
import java.util.List;

public interface HistoricCfgInstanceDao extends Dao<Integer, HistoricCfgInstance> {

  public List<HistoricCfgInstance> findByCfgIdentifierUUID(String uuid);

  public List<HistoricCfgInstance> findByCfgInstanceId(int id);
}

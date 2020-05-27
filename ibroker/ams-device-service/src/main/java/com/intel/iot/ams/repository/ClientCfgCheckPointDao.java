/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;


import java.util.List;
import com.intel.iot.ams.entity.ClientCfgCheckPoint;


public interface ClientCfgCheckPointDao extends Dao<Integer, ClientCfgCheckPoint> {

  public List<ClientCfgCheckPoint> findByClientId(Integer clientId);

  public void removeByClientId(Integer clientId);

}

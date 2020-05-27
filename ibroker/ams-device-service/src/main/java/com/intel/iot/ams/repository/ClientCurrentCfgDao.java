/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;


import java.util.List;
import com.intel.iot.ams.entity.ClientCurrentCfg;


public interface ClientCurrentCfgDao extends Dao<Integer, ClientCurrentCfg> {

  public List<ClientCurrentCfg> findByClientId(Integer clientId);

  public void removeByClientId(Integer clientId);

  public ClientCurrentCfg
      findByClientIdAndProductNameAndTargetTypeAndTargetIdAndPathName(Integer clientId, String pn,
                                                                      String tt, String ti,
                                                                      String path);

}

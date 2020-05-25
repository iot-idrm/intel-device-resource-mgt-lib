/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ClientCurrentCfg;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientCurrentCfgDao extends JpaRepository<ClientCurrentCfg, Integer> {

  public List<ClientCurrentCfg> findByClientId(int shortId);

  ClientCurrentCfg findByClientIdAndProductNameAndTargetTypeAndTargetIdAndPathName(
      Integer clientId, String pn, String tt, String ti, String path);
}

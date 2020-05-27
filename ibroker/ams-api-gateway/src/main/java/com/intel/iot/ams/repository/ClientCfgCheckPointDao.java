/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ClientCfgCheckPoint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientCfgCheckPointDao extends JpaRepository<ClientCfgCheckPoint, Integer> {

  public List<ClientCfgCheckPoint> findByClientId(Integer clientId);

  public void removeByClientId(Integer clientId);
}

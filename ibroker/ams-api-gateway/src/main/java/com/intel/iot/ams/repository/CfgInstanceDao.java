/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.CfgInstance;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgInstanceDao extends JpaRepository<CfgInstance, Integer> {

  public List<CfgInstance> findByCfgUuid(String uuid);

  public CfgInstance findByCfgUuidAndTargetId(String uuid, String targetId);
}

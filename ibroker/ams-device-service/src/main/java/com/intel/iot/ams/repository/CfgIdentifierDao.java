/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.CfgIdentifier;
import java.util.List;

public interface CfgIdentifierDao extends Dao<Integer, CfgIdentifier> {

  public CfgIdentifier findByUUID(String uuid);

  public List<CfgIdentifier> findByUserNameAndTargetType(String name, String targetType);
}

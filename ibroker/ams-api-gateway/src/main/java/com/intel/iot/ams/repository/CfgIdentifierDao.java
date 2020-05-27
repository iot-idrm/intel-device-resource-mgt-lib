/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.CfgIdentifier;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgIdentifierDao extends JpaRepository<CfgIdentifier, Integer> {

  public CfgIdentifier findByCfgUuid(String uuid);

  public CfgIdentifier findByUserNameAndPathNameAndTargetType(
      String userName, String pathName, String targetType);

  public List<CfgIdentifier> findByUserNameAndPathName(String userName, String pathName);

  public List<CfgIdentifier> findByUserNameAndTargetType(String userName, String targetType);

  public List<CfgIdentifier> findByTargetTypeAndPathName(String targetType, String pathName);

  public List<CfgIdentifier> findByUserName(String userName);

  public List<CfgIdentifier> findByPathName(String pathName);

  public List<CfgIdentifier> findByTargetType(String targetType);
}

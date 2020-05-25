/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.CfgIdentifier;
import com.intel.iot.ams.repository.CfgIdentifierDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CfgIdentifierService extends BaseService<Integer, CfgIdentifier> {

  @Autowired private CfgIdentifierDao cfgIdentifierDao;

  public CfgIdentifier findByUUID(String uuid) {

    return cfgIdentifierDao.findByCfgUuid(uuid);
  }

  public List<CfgIdentifier> findByUserNameAndPathName(String userName, String pathName) {

    return cfgIdentifierDao.findByUserNameAndPathName(userName, pathName);
  }

  public List<CfgIdentifier> findByUserNameAndTargetType(String userName, String targetType) {
    return cfgIdentifierDao.findByUserNameAndTargetType(userName, targetType);
  }

  public List<CfgIdentifier> findByTargetTypeAndPathName(String targetType, String pathName) {
    return cfgIdentifierDao.findByTargetTypeAndPathName(targetType, pathName);
  }

  public CfgIdentifier findByUserNameAndPathNameAndTargetType(
      String userName, String pathName, String targetType) {
    return cfgIdentifierDao.findByUserNameAndPathNameAndTargetType(userName, pathName, targetType);
  }

  public List<CfgIdentifier> findByUserName(String userName) {
    return cfgIdentifierDao.findByUserName(userName);
  }

  public List<CfgIdentifier> findByPathName(String pathName) {
    return cfgIdentifierDao.findByPathName(pathName);
  }

  public List<CfgIdentifier> findByTargetType(String targetType) {
    return cfgIdentifierDao.findByTargetType(targetType);
  }

  public void save(CfgIdentifier id) {
    cfgIdentifierDao.save(id);
  }

  public void update(CfgIdentifier cfgId) {
    cfgIdentifierDao.save(cfgId);
  }

  public List<CfgIdentifier> findAll() {
    return cfgIdentifierDao.findAll();
  }

  public void delete(CfgIdentifier cfgId) {
    cfgIdentifierDao.delete(cfgId);
  }
}

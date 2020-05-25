/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ClientDeviceMapping;
import com.intel.iot.ams.repository.ClientDeviceMappingDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClientDeviceMappingService extends BaseService<Integer, ClientDeviceMapping> {

  @Autowired private ClientDeviceMappingDao clientDeviceMappingDao;

  public ClientDeviceMapping findByAmsClientUuidAndProductName(
      String amsClientUuid, String productName) {
    return clientDeviceMappingDao.findByClientUuidAndProductName(amsClientUuid, productName);
  }

  public List<ClientDeviceMapping> findByProductName(String name) {
    return clientDeviceMappingDao.findByProductName(name);
  }

  public ClientDeviceMapping findByProductDeviceId(String deviceId) {
    return clientDeviceMappingDao.findByProductDeviceId(deviceId);
  }

  public void removeByProductName(String name) {
    clientDeviceMappingDao.removeByProductName(name);
  }

  public void removeByClientUuid(String clientUuid) {
    clientDeviceMappingDao.removeByClientUuid(clientUuid);
  }

  public List<ClientDeviceMapping> findByFilter(String name, Integer offset, Integer limit) {
    return clientDeviceMappingDao.findByProductName(name);
  }

  public ClientDeviceMapping findByAmsClientUuidAndProductName(
      String amsClientUuid, String productName, String projectId) {
    return clientDeviceMappingDao.findByClientUuidAndProductNameAndProjectId(
        amsClientUuid, productName, projectId);
  }

  public List<ClientDeviceMapping> findByProductName(String name, String projectId) {
    return clientDeviceMappingDao.findByProductNameAndProjectId(name, projectId);
  }
}

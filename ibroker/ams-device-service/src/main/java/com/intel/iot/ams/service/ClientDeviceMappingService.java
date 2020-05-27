/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ClientDeviceMapping;
import com.intel.iot.ams.repository.ClientDeviceMappingDao;
import com.intel.iot.ams.repository.hibernate.ClientDeviceMappingDaoImpl;

public class ClientDeviceMappingService extends BaseService<Integer, ClientDeviceMapping> {


  private ClientDeviceMappingDao clientDeviceMappingDao;

  public ClientDeviceMappingService() {
    clientDeviceMappingDao = new ClientDeviceMappingDaoImpl();
    super.setDao(clientDeviceMappingDao);
  }

  public ClientDeviceMapping findByProductDeviceId(String deviceId) {
    return clientDeviceMappingDao.findByProductDeviceId(deviceId);
  }


  public void removeByClientUuid(String clientUuid) {
    clientDeviceMappingDao.removeByClientUuid(clientUuid);
    return;
  }

}

/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.repository.AmsClientDao;
import com.intel.iot.ams.repository.hibernate.AmsClientDaoImpl;

public class AmsClientService extends BaseService<Integer, AmsClient> {


  private AmsClientDao amsClientDao;

  public AmsClientService() {
    amsClientDao = new AmsClientDaoImpl();
    super.setDao(amsClientDao);
  }

  public AmsClient findByClientUUID(String uuid) {

    return amsClientDao.findByClientUUID(uuid);
  }

  public void removeByClientUUID(String uuid) {
    amsClientDao.removeByClientUUID(uuid);
    return;
  }

  public AmsClient findByHardwareSerial(String serial) {

    return amsClientDao.findByHardwareSerial(serial);
  }

}

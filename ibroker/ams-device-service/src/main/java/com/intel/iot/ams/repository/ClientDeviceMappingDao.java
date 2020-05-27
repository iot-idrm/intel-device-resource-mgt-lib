/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ClientDeviceMapping;


public interface ClientDeviceMappingDao extends Dao<Integer, ClientDeviceMapping> {


  public ClientDeviceMapping findByProductDeviceId(String deviceId);

  public void removeByClientUuid(String clientUuid);

}

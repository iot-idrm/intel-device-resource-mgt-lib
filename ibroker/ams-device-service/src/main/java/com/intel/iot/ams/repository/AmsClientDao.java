/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;


import com.intel.iot.ams.entity.AmsClient;


public interface AmsClientDao extends Dao<Integer, AmsClient> {

  public AmsClient findByClientUUID(String uuid);

  public void removeByClientUUID(String uuid);

  public AmsClient findByHardwareSerial(String serial);
}

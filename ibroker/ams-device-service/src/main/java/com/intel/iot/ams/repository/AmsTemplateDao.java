/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;


import com.intel.iot.ams.entity.AmsTemplate;


public interface AmsTemplateDao extends Dao<Integer, AmsTemplate> {

  public AmsTemplate findByName(String name);

  public void removeByName(String name);
}

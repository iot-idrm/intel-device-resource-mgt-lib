/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.AmsTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmsTemplateDao extends JpaRepository<AmsTemplate, Integer> {

  public AmsTemplate findByName(String name);

  public void removeByName(String name);
}

/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.AmsTask;
import java.util.List;

public interface AmsTaskDao extends Dao<Integer, AmsTask> {

  public List<AmsTask> findByTaskPriority(int priority);

  public AmsTask getTopTask();
}

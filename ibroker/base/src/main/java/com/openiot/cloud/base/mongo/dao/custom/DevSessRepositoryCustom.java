/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.openiot.cloud.base.mongo.dao.custom;

import com.openiot.cloud.base.mongo.model.DevSession;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface DevSessRepositoryCustom {
  List<DevSession> filter(
      String id, String devId, Boolean ended, long from, long to, Pageable pageable);
}

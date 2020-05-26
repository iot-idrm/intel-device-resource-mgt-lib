/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.openiot.cloud.base.mongo.dao.custom;

import com.openiot.cloud.base.mongo.model.Folder;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface FolderRepositoryCustom {

  List<Folder> filter(
      String id,
      String name,
      String gtId,
      String gtName,
      String entType,
      String entId,
      Pageable pageable);
}

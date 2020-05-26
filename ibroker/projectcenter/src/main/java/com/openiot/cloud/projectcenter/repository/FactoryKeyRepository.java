/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.openiot.cloud.projectcenter.repository;

import com.openiot.cloud.projectcenter.repository.document.FactoryKey;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FactoryKeyRepository extends MongoRepository<FactoryKey, String> {
  List<FactoryKey> findByKeyNameAndKeyType(String keyName, String keyType);
}

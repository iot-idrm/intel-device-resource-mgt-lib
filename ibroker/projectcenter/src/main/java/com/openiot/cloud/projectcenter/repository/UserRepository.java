/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.openiot.cloud.projectcenter.repository;

import com.openiot.cloud.projectcenter.repository.document.User;
import java.util.List;
import java.util.Set;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
  List<User> findByName(String name);

  List<User> findByNameIn(Set<String> name);

  List<User> deleteByName(String name);
}

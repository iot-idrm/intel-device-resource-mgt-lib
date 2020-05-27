/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import java.io.Serializable;
import java.util.List;

public interface Dao<K, E> {

  Serializable persist(E entity);

  void update(E entity);

  void remove(E entity);

  E findById(K id);

  void removeById(K id);

  List<E> findAll();

  void saveOrUpdate(E entity);

}

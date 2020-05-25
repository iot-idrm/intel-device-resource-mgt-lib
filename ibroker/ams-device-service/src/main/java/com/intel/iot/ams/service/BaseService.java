/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.io.Serializable;
import java.util.List;
import com.intel.iot.ams.repository.Dao;

public class BaseService<K, E> {
  protected Dao<K, E> dao;

  public Dao<K, E> getDao() {
    return dao;
  }

  public void setDao(Dao<K, E> dao) {
    this.dao = dao;
  }

  public Serializable save(E entity) {
    return dao.persist(entity);
  }

  public void update(E entity) {
    dao.update(entity);
  }

  public void saveOrUpdate(E entity) {
    dao.saveOrUpdate(entity);
  }

  public void delete(E entity) {
    dao.remove(entity);
  }

  public E findById(K id) {
    return dao.findById(id);
  }

  public List<E> findAll() {
    return dao.findAll();
  }

  public void removeById(K id) {
    dao.removeById(id);
  }

}

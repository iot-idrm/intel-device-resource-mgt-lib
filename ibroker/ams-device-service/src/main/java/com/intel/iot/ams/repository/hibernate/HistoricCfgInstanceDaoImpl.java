/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository.hibernate;

import com.intel.iot.ams.entity.HistoricCfgInstance;
import com.intel.iot.ams.repository.HistoricCfgInstanceDao;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class HistoricCfgInstanceDaoImpl extends HibernateDaoImpl<HistoricCfgInstance>
    implements HistoricCfgInstanceDao {
  @Override
  public List<HistoricCfgInstance> findByCfgIdentifierUUID(String uuid) {
    String hql = "from HistoricCfgInstance where cfgUuid = :cfgUuid";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<HistoricCfgInstance> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("cfgUuid", uuid);
      list = query.list();
      query = null;
      t.commit();
    } catch (HibernateException e) {
      e.printStackTrace();
      if (t != null) {
        t.rollback();
      }
      throw e;
    } finally {
      s.close();
    }

    if (list.size() == 0) {
      return null;
    }
    return list;

  }

  @Override
  public List<HistoricCfgInstance> findByCfgInstanceId(int id) {

    String hql = "from HistoricCfgInstance where instanceId = :instanceId";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<HistoricCfgInstance> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("instanceId", id);
      list = query.list();
      query = null;
      t.commit();
    } catch (HibernateException e) {
      e.printStackTrace();
      if (t != null) {
        t.rollback();
      }
      throw e;
    } finally {
      s.close();
    }

    if (list.size() == 0) {
      return null;
    }
    return list;
  }
}

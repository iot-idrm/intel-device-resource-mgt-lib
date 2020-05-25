/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository.hibernate;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.intel.iot.ams.entity.CfgInstance;
import com.intel.iot.ams.repository.CfgInstanceDao;

public class CfgInstanceDaoImpl extends HibernateDaoImpl<CfgInstance> implements CfgInstanceDao {

  @Override
  public List<CfgInstance> findByCfgIdentifierUUID(String uuid) {
    String hql = "from CfgInstance where cfgUuid = :cfgUuid";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<CfgInstance> list = null;
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
  public CfgInstance findByCfgIdentifierUUIDAndTargetId(String uuid, String targetId) {

    String hql = "from CfgInstance where cfgUuid = :cfgUuid and targetId = :targetId";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<CfgInstance> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("cfgUuid", uuid);
      query.setParameter("targetId", targetId);
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
    return list.get(0);
  }
}

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
import com.intel.iot.ams.entity.CfgIdentifier;
import com.intel.iot.ams.repository.CfgIdentifierDao;

public class CfgIdentifierDaoImpl extends HibernateDaoImpl<CfgIdentifier>
    implements CfgIdentifierDao {

  @Override
  public CfgIdentifier findByUUID(String uuid) {
    String hql = "from CfgIdentifier where cfgUuid = :cfgUuid";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<CfgIdentifier> list = null;
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
    return list.get(0);
  }

  @Override
  public List<CfgIdentifier> findByUserNameAndTargetType(String name, String targetType) {
    String hql = "from CfgIdentifier where userName = :userName and targetType = :targetType";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<CfgIdentifier> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("userName", name);
      query.setParameter("targetType", targetType);
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

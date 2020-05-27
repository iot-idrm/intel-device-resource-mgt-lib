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
import com.intel.iot.ams.entity.ClientCfgCheckPoint;
import com.intel.iot.ams.repository.ClientCfgCheckPointDao;

public class ClientCfgCheckPointDaoImpl extends HibernateDaoImpl<ClientCfgCheckPoint>
    implements ClientCfgCheckPointDao {

  @Override
  public List<ClientCfgCheckPoint> findByClientId(Integer clientId) {
    String hql = "from ClientCfgCheckPoint where clientId = :clientId";

    Session s = null;
    Transaction t = null;
    Query query = null;

    List<ClientCfgCheckPoint> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("clientId", clientId);
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
  public void removeByClientId(Integer clientId) {
    String hql = "delete from ClientCfgCheckPoint where clientId = :clientId";
    Session s = null;
    Transaction t = null;
    Query delete = null;

    try {
      s = this.getSession();
      t = s.beginTransaction();
      delete = s.createQuery(hql);
      delete.setInteger("clientId", clientId);
      delete.executeUpdate();
      t.commit();
    } catch (Exception e) {
      e.printStackTrace();
      if (t != null) {
        t.rollback();
      }
      throw e;
    } finally {
      s.close();
    }

    return;

  }


}

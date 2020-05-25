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
import com.intel.iot.ams.entity.ClientCurrentCfg;
import com.intel.iot.ams.repository.ClientCurrentCfgDao;

public class ClientCurrentCfgDaoImpl extends HibernateDaoImpl<ClientCurrentCfg>
    implements ClientCurrentCfgDao {

  @Override
  public List<ClientCurrentCfg> findByClientId(Integer clientId) {

    String hql = "from ClientCurrentCfg where clientId = :clientId";

    Session s = null;
    Transaction t = null;
    Query query = null;

    List<ClientCurrentCfg> list = null;
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
    String hql = "delete from ClientCurrentCfg where clientId = :clientId";
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

  @Override
  public ClientCurrentCfg
      findByClientIdAndProductNameAndTargetTypeAndTargetIdAndPathName(Integer clientId, String pn,
                                                                      String tt, String ti,
                                                                      String path) {

    StringBuffer temp = new StringBuffer();
    String hql = null;
    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ClientCurrentCfg> list = null;

    temp.append("from ClientCurrentCfg where clientId = :clientId and productName = :pn and targetType = :tt and pathName = :path");
    if (ti == null) {
      temp.append(" and targetId is null");
    } else {
      temp.append(" and targetId = :ti");
    }

    hql = temp.toString();
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("clientId", clientId);
      query.setParameter("pn", pn);
      query.setParameter("tt", tt);
      query.setParameter("path", path);
      if (ti != null) {
        query.setParameter("ti", ti);
      }
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

/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository.hibernate;

import com.intel.iot.ams.entity.CfgContent;
import com.intel.iot.ams.repository.CfgContentDao;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CfgContentDaoImpl extends HibernateDaoImpl<CfgContent> implements CfgContentDao {

  @Override
  public CfgContent findBySharedName(String name) {
    String hql = "from CfgContent where sharedName = :sharedName";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<CfgContent> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("sharedName", name);
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
  public CfgContent findByHash(String hash) {
    String hql = "from CfgContent where contentHash = :hash";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<CfgContent> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("hash", hash);
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

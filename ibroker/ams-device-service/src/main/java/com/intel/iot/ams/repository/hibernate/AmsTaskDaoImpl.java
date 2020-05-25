/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.intel.iot.ams.entity.AmsTask;
import com.intel.iot.ams.repository.AmsTaskDao;
import java.util.List;

public class AmsTaskDaoImpl extends HibernateDaoImpl<AmsTask> implements AmsTaskDao {

  public List<AmsTask> findByTaskPriority(int priority) {

    String hql = "from AmsTask where taskPriority = :taskPriority";

    Session s = null;
    Transaction t = null;
    Query query = null;

    List<AmsTask> list = null;

    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("taskPriority", priority);
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

  public AmsTask getTopTask() {
    String hql = "from AmsTask order by id desc";

    Session s = null;
    Transaction t = null;
    Query query = null;

    List<AmsTask> list = null;

    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
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

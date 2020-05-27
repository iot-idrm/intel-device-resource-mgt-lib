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
import com.intel.iot.ams.entity.AmsTemplate;
import com.intel.iot.ams.repository.AmsTemplateDao;


public class AmsTemplateDaoImpl extends HibernateDaoImpl<AmsTemplate> implements AmsTemplateDao {


  @Override
  public AmsTemplate findByName(String name) {

    String hql = "from AmsTemplate where name = :name";

    Session s = null;
    Transaction t = null;
    Query query = null;

    List<AmsTemplate> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("name", name);
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
  public void removeByName(String name) {
    String hql = "delete from AmsTemplate where name = :name";
    Session s = null;
    Transaction t = null;
    Query delete = null;

    try {
      s = this.getSession();
      t = s.beginTransaction();
      delete = s.createQuery(hql);
      delete.setString("name", name);
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

/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.intel.iot.ams.entity.ProductProperty;
import com.intel.iot.ams.repository.ProductPropertyDao;
import java.util.List;

public class ProductPropertyDaoImpl extends HibernateDaoImpl<ProductProperty>
    implements ProductPropertyDao {

  @Override
  public List<ProductProperty> findByName(String name) {
    String hql = "from ProductProperty where productName = :productName";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ProductProperty> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("productName", name);
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
  public void removeByName(String name) {
    String hql = "delete from Product where productName = :productName";

    Session s = null;
    Transaction t = null;
    Query delete = null;

    try {
      s = getSession();
      t = s.beginTransaction();
      delete = s.createQuery(hql);
      delete.setString("productName", name);
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

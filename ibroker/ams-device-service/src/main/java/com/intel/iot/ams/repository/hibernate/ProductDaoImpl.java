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
import com.intel.iot.ams.entity.Product;
import com.intel.iot.ams.repository.ProductDao;

public class ProductDaoImpl extends HibernateDaoImpl<Product> implements ProductDao {

  @Override
  public Product findByUUID(String uuid) {
    String hql = "from Product where uuid = :uuid";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<Product> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("uuid", uuid);
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
  public void removeByUUID(String uuid) {
    String hql = "delete from Product where uuid = :uuid";

    Session s = null;
    Transaction t = null;
    Query delete = null;
    try {
      s = getSession();
      t = s.beginTransaction();
      delete = s.createQuery(hql);
      delete.setString("uuid", uuid);
      delete.executeUpdate();
      t.commit();
    } catch (Exception e) {
      t.rollback();
      throw e;
    } finally {
      s.close();
    }

    return;
  }

  @Override
  public Product findByName(String name) {
    String hql = "from Product where name = :name";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<Product> list = null;
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

}

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
import com.intel.iot.ams.entity.ProductInstalled;
import com.intel.iot.ams.repository.ProductInstalledDao;

public class ProductInstalledDaoImpl extends HibernateDaoImpl<ProductInstalled>
    implements ProductInstalledDao {

  @Override
  public List<ProductInstalled> findByClientUUID(String uuid) {
    String hql = "from ProductInstalled where amsClientUuid = :amsClientUuid";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ProductInstalled> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("amsClientUuid", uuid);
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
  public ProductInstalled findByClientUuidAndProductName(String clientUuid, String productName) {
    String hql =
        "from ProductInstalled where amsClientUuid = :amsClientUuid and productName = :productName";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ProductInstalled> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("amsClientUuid", clientUuid);
      query.setParameter("productName", productName);
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
  public void removeByClientUuid(String clientUuid) {

    String hql = "delete from ProductInstalled where amsClientUuid = :amsClientUuid";

    Session s = null;
    Transaction t = null;
    Query delete = null;
    try {
      s = getSession();
      t = s.beginTransaction();
      delete = s.createQuery(hql);
      delete.setString("amsClientUuid", clientUuid);
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

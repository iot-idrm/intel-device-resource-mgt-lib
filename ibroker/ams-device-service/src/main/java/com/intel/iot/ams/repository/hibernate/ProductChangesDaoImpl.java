/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository.hibernate;

import com.intel.iot.ams.entity.ProductChanges;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.Date;
import com.intel.iot.ams.repository.ProductChangesDao;

public class ProductChangesDaoImpl extends HibernateDaoImpl<ProductChanges>
    implements ProductChangesDao {

  @Override
  public List<ProductChanges> findByClientUUID(String uuid) {

    String hql = "from ProductChanges where amsClientUuid = :amsClientUuid";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ProductChanges> list = null;
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
  public void removeByClientUuid(String clientUuid) {
    String hql = "delete from ProductChanges where amsClientUuid = :amsClientUuid";

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

  @Override
  public void removeByClientUuidAndProductName(String clientUuid, String productName) {

    String hql =
        "delete from ProductChanges where amsClientUuid = :amsClientUuid and productName = :productName";

    Session s = null;
    Transaction t = null;
    Query delete = null;

    try {
      s = getSession();
      t = s.beginTransaction();
      delete = s.createQuery(hql);
      delete.setString("amsClientUuid", clientUuid);
      delete.setString("productName", productName);
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
  public Date getMaxEnableTime() {

    String hql = "from ProductChanges where enableTime is not null order by enableTime desc";
    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ProductChanges> list = null;
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

    if (list == null || list.size() == 0) {
      return null;
    }

    return list.get(0).getEnableTime();
  }

  @Override
  public int getEnableTimeCount(Date time) {
    String hql = "select count(*) from ProductChanges where enableTime=:enableTime";
    Session s = null;
    Transaction t = null;
    Query query = null;
    int count = 0;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("enableTime", time);
      Object result = query.uniqueResult();
      count = ((Long) result).intValue();
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
    return count;
  }

}

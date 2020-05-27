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
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.repository.AmsClientDao;

public class AmsClientDaoImpl extends HibernateDaoImpl<AmsClient> implements AmsClientDao {


  @Override
  public AmsClient findByClientUUID(String uuid) {
    String hql = "from AmsClient where amsClientUuid = :amsClientUuid";

    Session s = null;
    Transaction t = null;
    Query query = null;

    List<AmsClient> list = null;
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
    return list.get(0);
  }


  @Override
  public void removeByClientUUID(String uuid) {
    String hql = "delete from AmsClient where amsClientUuid = :amsClientUuid";
    Session s = null;
    Transaction t = null;
    Query delete = null;

    try {
      s = this.getSession();
      t = s.beginTransaction();
      delete = s.createQuery(hql);
      delete.setString("amsClientUuid", uuid);
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
  public AmsClient findByHardwareSerial(String serial) {
    String hql = "from AmsClient where serial = :serial";

    Session s = null;
    Transaction t = null;
    Query query = null;

    List<AmsClient> list = null;

    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("serial", serial);
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

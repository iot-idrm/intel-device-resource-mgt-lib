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
import com.intel.iot.ams.entity.ProductDownloadHistory;
import com.intel.iot.ams.repository.ProductDownloadHistoryDao;

public class ProductDownloadHistoryDaoImpl extends HibernateDaoImpl<ProductDownloadHistory>
    implements ProductDownloadHistoryDao {

  @Override
  public List<ProductDownloadHistory> findByClientUuid(String uuid) {
    String hql = "from ProductDownloadHistory where amsClientUuid = :uuid";

    Session s = null;
    Transaction t = null;
    Query query = null;

    List<ProductDownloadHistory> list = null;
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
    return list;
  }

  @Override
  public List<ProductDownloadHistory> findByClientUuidAndProductName(String uuid,
                                                                     String productName) {
    String hql =
        "from ProductDownloadHistory where amsClientUuid = :uuid and productName = :productName";

    Session s = null;
    Transaction t = null;
    Query query = null;

    List<ProductDownloadHistory> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("uuid", uuid);
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
    return list;
  }
}

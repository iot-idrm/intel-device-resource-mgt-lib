/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository.hibernate;

import com.intel.iot.ams.entity.ProductDeploy;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.intel.iot.ams.repository.ProductDeployDao;

public class ProductDeployDaoImpl extends HibernateDaoImpl<ProductDeploy>
    implements ProductDeployDao {

  @Override
  public List<ProductDeploy> findByClientUUID(String uuid) {

    String hql = "from ProductDeploy where amsClientUuid = :amsClientUuid";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ProductDeploy> list = null;
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
  public List<ProductDeploy> findByDeviceId(String deviceId) {

    String hql = "from ProductDeploy where productDeviceId = :productDeviceId";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ProductDeploy> list = null;
    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("productDeviceId", deviceId);
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
  public ProductDeploy findByClientUuidAndProductName(String clientUuid, String productName) {

    String hql =
        "from ProductDeploy where amsClientUuid = :amsClientUuid and productName = :productName";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ProductDeploy> list = null;
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
}

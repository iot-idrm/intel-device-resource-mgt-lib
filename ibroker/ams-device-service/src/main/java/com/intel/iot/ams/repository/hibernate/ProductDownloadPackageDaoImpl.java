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
import com.intel.iot.ams.entity.ProductDownloadPackage;
import com.intel.iot.ams.repository.ProductDownloadPackageDao;


public class ProductDownloadPackageDaoImpl extends HibernateDaoImpl<ProductDownloadPackage>
    implements ProductDownloadPackageDao {

  @Override
  public ProductDownloadPackage findByHashCode(String hash) {

    String hql = "from ProductDownloadPackage where hashcode = :hash";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ProductDownloadPackage> list = null;
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

  @Override
  public ProductDownloadPackage findByProductNameAndFromIdAndToId(String name, Integer fromId,
                                                                  Integer toId) {

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ProductDownloadPackage> list = null;
    String hql = null;

    try {
      s = this.getSession();
      t = s.beginTransaction();
      if (fromId == null) {
        hql =
            "from ProductDownloadPackage where productName = :productName and fromId is null and toId = :toId";
        query = s.createQuery(hql);
        query.setParameter("productName", name);
        query.setParameter("toId", toId);
      } else {
        hql =
            "from ProductDownloadPackage where productName = :productName and fromId = :fromId and toId = :toId";
        query = s.createQuery(hql);
        query.setParameter("productName", name);
        query.setParameter("fromId", fromId);
        query.setParameter("toId", toId);
      }
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

  public ProductDownloadPackage
      findByProductNameAndFromIdAndToIdAndIsAot(String name, Integer fromId, Integer toId,
                                                Boolean isAot) {

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ProductDownloadPackage> list = null;
    String hql = null;

    try {
      s = this.getSession();
      t = s.beginTransaction();
      if (isAot == null) {
        if (fromId == null) {
          hql =
              "from ProductDownloadPackage where productName = :productName and fromId is null and toId = :toId and isAot is null";
          query = s.createQuery(hql);
          query.setParameter("productName", name);
          query.setParameter("toId", toId);
        } else {
          hql =
              "from ProductDownloadPackage where productName = :productName and fromId = :fromId and toId = :toId and isAot is null";
          query = s.createQuery(hql);
          query.setParameter("productName", name);
          query.setParameter("fromId", fromId);
          query.setParameter("toId", toId);
        }
      } else {
        if (fromId == null) {
          hql =
              "from ProductDownloadPackage where productName = :productName and fromId is null and toId = :toId and isAot = :isAot";
          query = s.createQuery(hql);
          query.setParameter("productName", name);
          query.setParameter("toId", toId);
          query.setParameter("isAot", isAot);
        } else {
          hql =
              "from ProductDownloadPackage where productName = :productName and fromId = :fromId and toId = :toId and isAot = :isAot";
          query = s.createQuery(hql);
          query.setParameter("productName", name);
          query.setParameter("fromId", fromId);
          query.setParameter("toId", toId);
          query.setParameter("isAot", isAot);
        }
      }

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

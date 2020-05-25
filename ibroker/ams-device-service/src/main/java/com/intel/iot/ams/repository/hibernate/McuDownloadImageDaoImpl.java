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
import com.intel.iot.ams.entity.McuDownloadImage;
import com.intel.iot.ams.repository.McuDownloadImageDao;

public class McuDownloadImageDaoImpl extends HibernateDaoImpl<McuDownloadImage>
    implements McuDownloadImageDao {

  @Override
  public McuDownloadImage findByHash(String hash) {

    String hql = "from McuDownloadImage where hashcode = :hash";

    Session s = null;
    Transaction t = null;
    Query query = null;

    List<McuDownloadImage> list = null;

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
  public McuDownloadImage findByIdList(String idList) {

    String hql = "from McuDownloadImage where idList = :idList";

    Session s = null;
    Transaction t = null;
    Query query = null;

    List<McuDownloadImage> list = null;

    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("idList", idList);
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

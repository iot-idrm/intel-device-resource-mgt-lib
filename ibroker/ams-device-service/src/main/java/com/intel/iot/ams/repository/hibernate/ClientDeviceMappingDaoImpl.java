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
import com.intel.iot.ams.entity.ClientDeviceMapping;
import com.intel.iot.ams.repository.ClientDeviceMappingDao;

public class ClientDeviceMappingDaoImpl extends HibernateDaoImpl<ClientDeviceMapping>
    implements ClientDeviceMappingDao {


  public ClientDeviceMapping findByProductDeviceId(String deviceId) {

    String hql = "from ClientDeviceMapping where productDeviceId = :productDeviceId";

    Session s = null;
    Transaction t = null;
    Query query = null;
    List<ClientDeviceMapping> list = null;
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
    return list.get(0);
  }


  public void removeByClientUuid(String clientUuid) {
    String hql = "delete from ClientDeviceMapping where amsClientUuid = :clientUuid";

    Session s = null;
    Transaction t = null;
    Query delete = null;
    try {
      s = getSession();
      t = s.beginTransaction();
      delete = s.createQuery(hql);
      delete.setString("clientUuid", clientUuid);
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

}

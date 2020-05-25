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
import com.intel.iot.ams.entity.ProductInstance;
import com.intel.iot.ams.repository.ProductInstanceDao;



public class ProductInstanceDaoImpl extends HibernateDaoImpl<ProductInstance>
    implements ProductInstanceDao {

  @Override
  public ProductInstance findByNameAndVersionAndCpuAndPlatformAndOs(String name, String version,
                                                                    String cpu, String platform,
                                                                    String os) {
    StringBuffer temp = new StringBuffer();
    String hql = null;
    Query query = null;
    Session s = null;
    Transaction t = null;
    List<ProductInstance> list = null;

    temp.append("from ProductInstance where productName = :productName and version = :version");
    if (cpu == null || cpu.equals("")) {
      temp.append(" and cpu is null");
    } else {
      temp.append(" and cpu = :cpu");
    }
    if (platform == null || platform.equals("")) {
      temp.append(" and platform is null");

    } else {
      temp.append(" and platform = :platform");
    }
    if (os == null || os.equals("")) {
      temp.append(" and os is null");
    } else {
      temp.append(" and os = :os");
    }
    hql = temp.toString();

    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("productName", name);
      query.setParameter("version", version);
      if (cpu != null && !cpu.equals("")) {
        query.setParameter("cpu", cpu);
      }
      if (platform != null && !platform.equals("")) {
        query.setParameter("platform", platform);
      }
      if (os != null && !os.equals("")) {
        query.setParameter("os", os);
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

  @Override
  public List<ProductInstance>
      findByNameAndVersionAndCpuAndPlatformAndOsAndSystemAndBits(String name, String version,
                                                                 String cpu, String platform,
                                                                 String os, String system,
                                                                 String bits) {

    StringBuffer temp = new StringBuffer();
    String hql = null;
    Query query = null;
    Session s = null;
    Transaction t = null;
    List<ProductInstance> list = null;

    temp.append("from ProductInstance where productName = :productName and version = :version");

    if (cpu == null || cpu.equals("")) {
      temp.append(" and cpu is null");
    } else {
      temp.append(" and cpu = :cpu");
    }

    if (platform == null || platform.equals("")) {
      temp.append(" and platform is null");

    } else {
      temp.append(" and platform = :platform");
    }

    if (os == null || os.equals("")) {
      temp.append(" and os is null");
    } else {
      temp.append(" and os = :os");
    }

    if (system == null || system.equals("")) {
      temp.append(" and system is null");
    } else {
      temp.append(" and system = :system");
    }

    if (bits == null || bits.equals("")) {
      temp.append(" and bits is null");
    } else {
      temp.append(" and bits = :bits");
    }

    hql = temp.toString();

    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("productName", name);
      query.setParameter("version", version);

      if (cpu != null && !cpu.equals("")) {
        query.setParameter("cpu", cpu);
      }
      if (platform != null && !platform.equals("")) {
        query.setParameter("platform", platform);
      }
      if (os != null && !os.equals("")) {
        query.setParameter("os", os);
      }

      if (system != null && !system.equals("")) {
        query.setParameter("system", system);
      }

      if (bits != null && !bits.equals("")) {
        query.setParameter("bits", bits);
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
    return list;
  }


  public List<ProductInstance>
      findByNameAndVersionAndCpuAndPlatformAndOsAndBits(String name, String version, String cpu,
                                                        String platform, String os, String bits) {
    StringBuffer temp = new StringBuffer();
    String hql = null;
    Query query = null;
    Session s = null;
    Transaction t = null;
    List<ProductInstance> list = null;

    temp.append("from ProductInstance where productName = :productName and version = :version");

    if (cpu == null || cpu.equals("")) {
      temp.append(" and cpu is null");
    } else {
      temp.append(" and cpu = :cpu");
    }

    if (platform == null || platform.equals("")) {
      temp.append(" and platform is null");

    } else {
      temp.append(" and platform = :platform");
    }

    if (os == null || os.equals("")) {
      temp.append(" and os is null");
    } else {
      temp.append(" and os = :os");
    }

    if (bits == null || bits.equals("")) {
      temp.append(" and bits is null");
    } else {
      temp.append(" and bits = :bits");
    }

    hql = temp.toString();

    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("productName", name);
      query.setParameter("version", version);

      if (cpu != null && !cpu.equals("")) {
        query.setParameter("cpu", cpu);
      }
      if (platform != null && !platform.equals("")) {
        query.setParameter("platform", platform);
      }
      if (os != null && !os.equals("")) {
        query.setParameter("os", os);
      }

      if (bits != null && !bits.equals("")) {
        query.setParameter("bits", bits);
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
    return list;
  }

  public List<ProductInstance> findByNameAndVersionAndCpu(String name, String version, String cpu) {
    StringBuffer temp = new StringBuffer();
    String hql = null;
    Query query = null;
    Session s = null;
    Transaction t = null;
    List<ProductInstance> list = null;

    temp.append("from ProductInstance where productName = :productName and version = :version");

    if (cpu == null || cpu.equals("")) {
      temp.append(" and cpu is null");
    } else {
      temp.append(" and cpu = :cpu");
    }


    hql = temp.toString();

    try {
      s = this.getSession();
      t = s.beginTransaction();
      query = s.createQuery(hql);
      query.setParameter("productName", name);
      query.setParameter("version", version);

      if (cpu != null && !cpu.equals("")) {
        query.setParameter("cpu", cpu);
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
    return list;
  }


}

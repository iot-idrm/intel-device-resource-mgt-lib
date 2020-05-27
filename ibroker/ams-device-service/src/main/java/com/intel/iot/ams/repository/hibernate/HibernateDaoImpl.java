/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import com.intel.iot.ams.repository.Dao;


public abstract class HibernateDaoImpl<E> implements Dao<Integer, E> {

  protected SessionFactory sessionFactory;

  protected Class<E> entityClass;

  Configuration cfg = null;

  public HibernateDaoImpl() {
    ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
    this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[0];
    try {
      cfg = new Configuration().configure("resources/hibernate.cfg.xml");
    } catch (HibernateException he) {
      cfg = new Configuration().configure("hibernate.cfg.xml");
    }

    this.sessionFactory =
        cfg.buildSessionFactory(new StandardServiceRegistryBuilder().applySettings(cfg.getProperties())
                                                                    .build());
  }

  protected Session getSession() {
    // return sessionFactory.getCurrentSession();
    return sessionFactory.openSession();
  }

  @Override
  public Serializable persist(E entity) {
    Session s = null;
    Transaction t = null;
    Serializable ret = null;
    try {
      s = getSession();
      t = s.beginTransaction();
      ret = s.save(entity);
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
    return ret;
  }

  @Override
  public void update(E entity) {
    Session s = null;
    Transaction t = null;
    try {
      s = getSession();
      t = s.beginTransaction();
      s.update(entity);
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
  }

  @Override
  public void saveOrUpdate(E entity) {
    Session s = null;
    Transaction t = null;
    try {
      s = getSession();
      t = s.beginTransaction();
      s.saveOrUpdate(entity);
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
  }

  @Override
  public void remove(E entity) {
    Session s = null;
    Transaction t = null;
    try {
      s = getSession();
      t = s.beginTransaction();
      s.delete(entity);
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
  }

  @Override
  public E findById(Integer id) {
    Session s = null;
    Transaction t = null;
    E ret = null;
    try {
      s = getSession();
      t = s.beginTransaction();
      ret = (E) s.get(entityClass, id);
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
    return ret;
  }

  @Override
  public List<E> findAll() {
    Session s = null;
    Transaction t = null;
    List<E> ret = null;
    try {
      s = getSession();
      t = s.beginTransaction();
      ret = (List<E>) s.createCriteria(entityClass).list();
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

    return ret;
  }

  @Override
  public void removeById(Integer id) {
    Session s = null;
    Transaction t = null;
    try {
      s = getSession();
      t = s.beginTransaction();
      remove((E) s.get(entityClass, id));
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
  }

}

/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.CfgContent;
import com.intel.iot.ams.repository.CfgContentDao;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.query.internal.QueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CfgContentService extends BaseService<Integer, CfgContent> {

  private static final Logger logger = LoggerFactory.getLogger(CfgContentService.class);

  @Autowired private CfgContentDao cfgContentDao;

  @Autowired private EntityManager entityManager;

  public CfgContent findBySharedName(String name) {
    return cfgContentDao.findBySharedName(name);
  }

  public CfgContent findByHash(String hash) {
    return cfgContentDao.findByContentHash(hash);
  }

  public List<CfgContent> findByNameLike(String nameLike) {
    return cfgContentDao.findBySharedNameLike("%" + nameLike + "%");
  }

  public List<CfgContent> findByTag(String tag) {
    return cfgContentDao.findByTag(tag);
  }

  public List<CfgContent> findByNameOrTag(String nameLike, String tag) {
    return cfgContentDao.findBySharedNameOrTag(nameLike, tag);
  }

  public List<CfgContent> findByNameLikeAndTag(String nameLike, String tag) {
    return cfgContentDao.findBySharedNameLikeAndTag("%" + nameLike + "%", tag);
  }

  public void save(CfgContent content) {
    cfgContentDao.save(content);
  }

  public CfgContent findById(Integer contentId) {
    return cfgContentDao.findById(contentId).orElse(null);
  }

  public void delete(CfgContent old) {
    cfgContentDao.delete(old);
  }

  public void update(CfgContent content) {
    cfgContentDao.save(content);
  }

  public List<CfgContent> findAll() {
    return cfgContentDao.findAll();
  }

  public List<CfgContent> findByType(Integer contentType) {
    return cfgContentDao.findByContentType(contentType);
  }

  public List<CfgContent> fuzzySearch(String queryStr, Integer offset, Integer limit) {
    logger.info("fuzzy search by queryStr={}", queryStr);

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CfgContent> criteriaQuery = criteriaBuilder.createQuery(CfgContent.class);

    // SELECT client FROM CfgContent
    Root<CfgContent> CfgContentRoot = criteriaQuery.from(CfgContent.class);
    criteriaQuery.select(CfgContentRoot);

    if (queryStr != "") {
      // WHERE
      Predicate predicate = criteriaBuilder.disjunction();
      predicate =
          criteriaBuilder.or(
              predicate,
              criteriaBuilder.like(CfgContentRoot.get("sharedName"), "%" + queryStr + "%"));
      predicate =
          criteriaBuilder.or(
              predicate, criteriaBuilder.like(CfgContentRoot.get("tag"), "%" + queryStr + "%"));

      predicate =
          criteriaBuilder.or(
              predicate,
              criteriaBuilder.like(CfgContentRoot.get("description"), "%" + queryStr + "%"));

      // contentType is Integer, should verify if the type of the query_str is
      // Integer.
      Pattern pattern = Pattern.compile("[0-9]*");
      if (pattern.matcher(queryStr).matches()) {
        predicate =
            criteriaBuilder.or(
                predicate,
                criteriaBuilder.equal(
                    CfgContentRoot.get("contentType"), Integer.valueOf(queryStr)));
      }
      criteriaQuery.where(predicate);
    }

    TypedQuery<CfgContent> query = entityManager.createQuery(criteriaQuery);
    logger.info("Query String: " + query.unwrap(QueryImpl.class).getQueryString());
    return query.setFirstResult(offset).setMaxResults(limit).getResultList();
  }
}

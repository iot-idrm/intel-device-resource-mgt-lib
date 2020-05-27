/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.repository.AmsClientDao;
import java.util.List;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AmsClientService extends BaseService<Integer, AmsClient> {
  private static final Logger logger = LoggerFactory.getLogger(AmsClientService.class);
  @Autowired private AmsClientDao amsClientDao;

  @Autowired private EntityManager entityManager;

  public AmsClient findByClientUUID(String uuid) {

    return amsClientDao.findByClientUuid(uuid);
  }

  public void removeByClientUUID(String uuid) {
    amsClientDao.removeByClientUuid(uuid);
    return;
  }

  public List<AmsClient> findByTemplateName(String name) {
    return amsClientDao.findByTemplateName(name);
  }

  public List<AmsClient> findByFilter(Integer offset, Integer limit) {
    return amsClientDao.findAll(PageRequest.of(offset / limit, limit)).getContent();
  }

  // TODO: need a test
  public List<AmsClient> fuzzySearch(String queryStr, Integer offset, Integer limit) {
    return entityManager
        .createNativeQuery(queryStr, AmsClient.class)
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
  }

  public AmsClient findByClientUUID(String uuid, String projectId) {
    logger.debug("uuid={}, projectId={}", uuid, projectId);
    return amsClientDao.findByClientUuidAndProjectId(uuid, projectId);
  }

  public void removeByClientUUID(String uuid, String projectId) {
    amsClientDao.removeByClientUuidAndProjectId(uuid, projectId);
  }

  public List<AmsClient> findByTemplateName(String name, String projectId) {
    return amsClientDao.findByTemplateNameAndProjectId(name, projectId);
  }

  public List<AmsClient> findByFilter(Integer offset, Integer limit, String projectId) {
    return amsClientDao.findByProjectId(projectId, PageRequest.of(offset / limit, limit));
  }

  // TODO: need a test
  public List<AmsClient> fuzzySearch(
      String queryStr, Integer offset, Integer limit, String projectId) {
    logger.info("fuzzy search by queryStr={} and projectId={}", queryStr, projectId);

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<AmsClient> criteriaQuery = criteriaBuilder.createQuery(AmsClient.class);

    // SELECT client FROM AmsClient
    Root<AmsClient> amsClientRoot = criteriaQuery.from(AmsClient.class);
    criteriaQuery.select(amsClientRoot);

    // WHERE
    Predicate predicate = criteriaBuilder.disjunction();
    predicate =
        criteriaBuilder.or(
            predicate, criteriaBuilder.like(amsClientRoot.get("clientUuid"), "%" + queryStr + "%"));
    predicate =
        criteriaBuilder.or(
            predicate, criteriaBuilder.like(amsClientRoot.get("deviceName"), "%" + queryStr + "%"));
    predicate =
        criteriaBuilder.or(
            predicate,
            criteriaBuilder.like(amsClientRoot.get("templateName"), "%" + queryStr + "%"));
    predicate =
        criteriaBuilder.or(
            predicate, criteriaBuilder.like(amsClientRoot.get("serial"), "%" + queryStr + "%"));
    // the projectId could be null, "or some thing else"
    if (projectId != null) {
      predicate =
          criteriaBuilder.and(
              predicate, criteriaBuilder.equal(amsClientRoot.get("projectId"), projectId));
    } else {
      predicate =
          criteriaBuilder.and(predicate, criteriaBuilder.isNull(amsClientRoot.get("projectId")));
    }

    criteriaQuery.where(predicate);

    TypedQuery<AmsClient> query = entityManager.createQuery(criteriaQuery);
    logger.info("Query String: " + query.unwrap(QueryImpl.class).getQueryString());
    return query.setFirstResult(offset).setMaxResults(limit).getResultList();
  }

  public List<AmsClient> findAll() {
    return amsClientDao.findAll();
  }

  public void update(AmsClient client) {
    amsClientDao.save(client);
  }

  public void save(AmsClient client) {
    amsClientDao.save(client);
  }

  public void delete(AmsClient client) {
    amsClientDao.delete(client);
  }
}

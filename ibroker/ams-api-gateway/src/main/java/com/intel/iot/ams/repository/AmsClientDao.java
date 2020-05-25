/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.AmsClient;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmsClientDao extends JpaRepository<AmsClient, Integer> {

  public AmsClient findByClientUuid(String uuid);

  public void removeByClientUuid(String uuid);

  public List<AmsClient> findByTemplateName(String name);

  // Add Project info for query
  public AmsClient findByClientUuidAndProjectId(String uuid, String projectId);

  public void removeByClientUuidAndProjectId(String uuid, String projectId);

  public List<AmsClient> findByTemplateNameAndProjectId(String name, String projectId);

  public List<AmsClient> findByProjectId(String projectId, Pageable pageable);
}

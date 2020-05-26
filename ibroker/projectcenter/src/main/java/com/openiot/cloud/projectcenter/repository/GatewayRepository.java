/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.openiot.cloud.projectcenter.repository;

import com.openiot.cloud.projectcenter.repository.document.Gateway;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GatewayRepository extends MongoRepository<Gateway, String> {
  public List<Gateway> findByHwSn(String hwSn);

  public List<Gateway> findByNewHwSn(String newHwSn);

  public List<Gateway> findByIAgentId(String iAgentId);

  public List<Gateway> findByIAgentIdIn(String[] iAgentIds);

  public List<Gateway> findByIAgentIdAndProjectId(String iAgentId, String projectId);

  public List<Gateway> findByProjectId(String projectId);

  public List<Gateway> findByProjectIdExists(boolean exists);
}

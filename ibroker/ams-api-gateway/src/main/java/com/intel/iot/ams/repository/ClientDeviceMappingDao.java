/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ClientDeviceMapping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDeviceMappingDao extends JpaRepository<ClientDeviceMapping, Integer> {

  public ClientDeviceMapping findByClientUuidAndProductName(
      String amsClientUuid, String productName);

  public List<ClientDeviceMapping> findByProductName(String name);

  public ClientDeviceMapping findByProductDeviceId(String deviceId);

  public void removeByProductName(String name);

  public void removeByClientUuid(String clientUuid);

  public ClientDeviceMapping findByClientUuidAndProductNameAndProjectId(
      String amsClientUuid, String productName, String projectId);

  public List<ClientDeviceMapping> findByProductNameAndProjectId(String name, String projectId);
}

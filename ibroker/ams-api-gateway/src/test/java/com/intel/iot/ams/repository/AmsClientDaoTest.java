/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.intel.iot.ams.entity.AmsClient;
import java.sql.Date;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AmsClientDaoTest {
  @Autowired private AmsClientDao amsClientDao;

  private List<AmsClient> amsClientList = new LinkedList<>();

  @Before
  public void setUp() throws Exception {
    amsClientDao.deleteAll(amsClientList);

    AmsClient amsClient1 = new AmsClient();
    amsClient1.setClientUuid("17");
    amsClient1.setProjectId("blueberry");
    amsClient1.setCpu("x86-64");
    amsClient1.setOs("ubuntu 16.04");
    amsClient1.setBits("64");
    amsClient1.setSerial("17");
    amsClient1.setProvisionTime(Date.from(Instant.now(Clock.systemUTC())));
    amsClient1 = amsClientDao.save(amsClient1);
    amsClientList.add(amsClient1);

    AmsClient amsClient2 = new AmsClient();
    amsClient2.setClientUuid("23");
    amsClient2.setProjectId(null);
    amsClient2.setCpu("x86-64");
    amsClient2.setOs("ubuntu 16.04");
    amsClient2.setBits("64");
    amsClient2.setSerial("23");
    amsClient2.setProvisionTime(Date.from(Instant.now(Clock.systemUTC())));
    amsClient2 = amsClientDao.save(amsClient2);
    amsClientList.add(amsClient2);
  }

  @After
  public void tearDown() throws Exception {
    amsClientDao.deleteAll(amsClientList);
  }

  @Test
  public void testFindByProject() throws Exception {
    assertThat(
            amsClientDao.findByProjectId(
                amsClientList.get(0).getProjectId(), PageRequest.of(0, 10)))
        .asList()
        .isNotEmpty()
        .hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("clientUuid", amsClientList.get(0).getClientUuid());

    // query with "projectId = null"
    assertThat(
            amsClientDao.findByProjectId(
                amsClientList.get(1).getProjectId(), PageRequest.of(0, 10)))
        .asList()
        .isNotEmpty()
        .hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("clientUuid", amsClientList.get(1).getClientUuid());
  }

  @Test
  public void testFindByClientUuid() throws Exception {
    assertThat(
            amsClientDao.findByClientUuidAndProjectId(
                amsClientList.get(0).getClientUuid(), amsClientList.get(0).getProjectId()))
        .isNotNull()
        .hasFieldOrPropertyWithValue("serial", amsClientList.get(0).getSerial());
  }
}

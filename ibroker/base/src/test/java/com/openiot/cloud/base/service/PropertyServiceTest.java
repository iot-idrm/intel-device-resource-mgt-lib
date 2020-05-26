/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.openiot.cloud.base.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.openiot.cloud.base.Application;
import com.openiot.cloud.base.mongo.model.ResProperty;
import com.openiot.cloud.base.mongo.model.help.ConfigurationEntity;
import com.openiot.cloud.base.service.model.Property;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {Application.class},
    properties = {"mongo.db = test_openiot"})
public class PropertyServiceTest {
  @Test
  public void testBasic() throws Exception {
    ResProperty resProperty = new ResProperty();
    resProperty.setName("apple");
    resProperty.setImplemented(true);
    resProperty.setResTypes(Collections.singletonList("honeydew"));
    resProperty.addUserCfgsItem(new ConfigurationEntity("a float", "3.14"));

    Property property = new Property();
    BeanUtils.copyProperties(resProperty, property);
    assertThat(property)
        .hasFieldOrPropertyWithValue("name", resProperty.getName())
        .hasFieldOrPropertyWithValue("implemented", resProperty.getImplemented())
        .hasFieldOrPropertyWithValue("userCfgs", resProperty.getUserCfgs());
  }
}

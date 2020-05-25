/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.help;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 此类能解决在实体关联属性配置延迟加载后，在页面上取数据导致500错误 作用：json与bean之间的转换
 *
 * @author xinweizx
 */
public class HibernateObjectMapper extends ObjectMapper {
  /** */
  private static final long serialVersionUID = 1L;

  public HibernateObjectMapper() {
    disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
  }
}

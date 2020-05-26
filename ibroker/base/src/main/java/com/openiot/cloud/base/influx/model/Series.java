/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.openiot.cloud.base.influx.model;

import java.time.Instant;
import java.util.Map;
import lombok.Data;

@Data
public class Series {
  private Instant timestamp;
  private Map<String, Object> fields;
}

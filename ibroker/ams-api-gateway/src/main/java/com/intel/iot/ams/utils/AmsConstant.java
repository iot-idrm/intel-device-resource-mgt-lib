/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AmsConstant {

  /** Linux AMS repo root directories */
  @Value("${ams.repoPath}")
  public String repoPath;

  /** Windows AMS repo root directories */
  // public static final String repoPath = "C:\\code\\ams\\";
  @Value("${ams.tempPath}")
  public String tempPath;
}

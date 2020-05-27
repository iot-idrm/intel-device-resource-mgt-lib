/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.utils;

public class AotToolUtils {

  private static String[] platforms = {"JLF1", "JLF2"};

  public static boolean isPlatformSupported(String platform) {

    for (String p : platforms) {
      if (platform.toUpperCase().equals(p)) {
        return true;
      }
    }

    return false;
  }
}

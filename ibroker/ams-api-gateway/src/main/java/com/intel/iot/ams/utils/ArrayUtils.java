/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.utils;

public class ArrayUtils {

  public static int bytesToInt(byte[] value, int offset) {
    int num = 0;
    for (int i = offset + 3; i >= offset; i--) {
      num = (num << 8) | (value[i] & 0xff);
    }
    return num;
  }

  public static short bytesToShort(byte[] value, short offset) {
    short num = 0;
    for (int i = offset + 1; i >= offset; i--) {
      num = (short) ((num << 8) | (value[i] & 0xff));
    }
    return num;
  }
}

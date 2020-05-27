/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {
  public static String getStackTrace(Throwable exception) {
    exception.printStackTrace();
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    exception.printStackTrace(pw);
    return sw.getBuffer().toString();
  }
}

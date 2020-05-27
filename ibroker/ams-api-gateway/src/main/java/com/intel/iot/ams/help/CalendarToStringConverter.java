/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.help;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.springframework.core.convert.converter.Converter;

public class CalendarToStringConverter implements Converter<Calendar, String> {

  @Override
  public String convert(Calendar calendar) {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    return sdf.format(calendar.getTime());
  }
}

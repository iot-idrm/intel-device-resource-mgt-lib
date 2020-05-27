/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.help;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;
import org.springframework.core.convert.converter.Converter;

public class StringToCalendarConverter implements Converter<String, Calendar> {
  @Override
  public Calendar convert(String source) {
    if (source.trim().length() == 0 || source == null) {
      return null;
    }
    SimpleDateFormat dateFormat;
    Pattern pattern1 = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}");
    Pattern pattern2 = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
    Pattern pattern3 = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}");
    if (pattern1.matcher(source).matches()) {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    } else if (pattern2.matcher(source).matches()) {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    } else if (pattern3.matcher(source).matches()) {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    } else {
      dateFormat = new SimpleDateFormat("HH:mm");
    }
    dateFormat.setLenient(false);
    try {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(dateFormat.parse(source));
      return calendar;
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
}

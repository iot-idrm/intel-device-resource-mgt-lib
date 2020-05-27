/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.security;

import com.openiot.cloud.base.common.model.TokenContent;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class AuthUtils {

  public static String getProjectIdFromContext() {
    try {
      return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
          .map(Authentication::getDetails)
          .map(obj -> (TokenContent) obj)
          .map(TokenContent::getProject)
          .orElse(null);
    } catch (Exception e) {
      log.error("meet an exception while getting project info", e);
      return null;
    }
  }

  public static TokenContent getTokenContextFromContext() {
    try {
      return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
          .map(Authentication::getDetails)
          .map(obj -> (TokenContent) obj)
          .orElse(null);
    } catch (Exception e) {
      log.error("meet an exception while getting role info", e);
      return null;
    }
  }
}

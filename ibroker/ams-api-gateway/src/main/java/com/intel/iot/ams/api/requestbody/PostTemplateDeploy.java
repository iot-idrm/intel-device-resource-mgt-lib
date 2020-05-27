/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api.requestbody;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PostTemplateDeploy {
  @JsonProperty(value = "template_name")
  private String templateName;

  @JsonProperty(value = "client_list")
  private List<String> clientList;

  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public List<String> getClientList() {
    return clientList;
  }

  public void setClientList(List<String> clientList) {
    this.clientList = clientList;
  }
}

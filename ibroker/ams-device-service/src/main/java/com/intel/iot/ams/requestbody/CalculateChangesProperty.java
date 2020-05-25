/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.requestbody;

import com.google.gson.annotations.SerializedName;

public class CalculateChangesProperty {

  @SerializedName("client_uuid")
  private String clientUuid;

  public String getClientUuid() {
    return clientUuid;
  }

  public void setClientUuid(String clientUuid) {
    this.clientUuid = clientUuid;
  }

}

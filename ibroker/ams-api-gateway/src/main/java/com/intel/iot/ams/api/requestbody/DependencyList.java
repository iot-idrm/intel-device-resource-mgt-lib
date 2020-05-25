/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api.requestbody;

import com.google.gson.annotations.SerializedName;
import com.intel.iot.ams.api.requestbody.ManifestInfo.DependencyInfo;
import java.util.List;

public class DependencyList {

  @SerializedName("dependencies")
  private List<DependencyInfo> dependencyList;

  public List<DependencyInfo> getDependencyList() {
    return dependencyList;
  }

  public void setDependencyList(List<DependencyInfo> dependencyList) {
    this.dependencyList = dependencyList;
  }
}

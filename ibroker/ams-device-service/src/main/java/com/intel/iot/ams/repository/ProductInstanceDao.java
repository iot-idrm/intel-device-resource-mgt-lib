/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;


import java.util.List;
import com.intel.iot.ams.entity.ProductInstance;

public interface ProductInstanceDao extends Dao<Integer, ProductInstance> {

  public ProductInstance findByNameAndVersionAndCpuAndPlatformAndOs(String name, String version,
                                                                    String cpu, String platform,
                                                                    String os);

  public List<ProductInstance>
      findByNameAndVersionAndCpuAndPlatformAndOsAndSystemAndBits(String name, String version,
                                                                 String cpu, String platform,
                                                                 String os, String system,
                                                                 String bits);

  public List<ProductInstance>
      findByNameAndVersionAndCpuAndPlatformAndOsAndBits(String name, String version, String cpu,
                                                        String platform, String os, String bits);

  public List<ProductInstance> findByNameAndVersionAndCpu(String name, String version, String cpu);


}

/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.util.List;
import com.intel.iot.ams.entity.ProductInstance;
import com.intel.iot.ams.repository.ProductInstanceDao;
import com.intel.iot.ams.repository.hibernate.ProductInstanceDaoImpl;


public class ProductInstanceService extends BaseService<Integer, ProductInstance> {

  private ProductInstanceDao productInstanceDao;

  public ProductInstanceService() {
    productInstanceDao = new ProductInstanceDaoImpl();
    super.setDao(productInstanceDao);
  }

  public ProductInstance findByNameAndVersionAndCpuAndPlatformAndOs(String name, String version,
                                                                    String cpu, String platform,
                                                                    String os) {
    return productInstanceDao.findByNameAndVersionAndCpuAndPlatformAndOs(name,
                                                                         version,
                                                                         cpu,
                                                                         platform,
                                                                         os);
  }

  public List<ProductInstance>
      findByNameAndVersionAndCpuAndPlatformAndOsAndSystemAndBits(String name, String version,
                                                                 String cpu, String platform,
                                                                 String os, String system,
                                                                 String bits) {
    return productInstanceDao.findByNameAndVersionAndCpuAndPlatformAndOsAndSystemAndBits(name,
                                                                                         version,
                                                                                         cpu,
                                                                                         platform,
                                                                                         os,
                                                                                         system,
                                                                                         bits);
  }

  public List<ProductInstance>
      findByNameAndVersionAndCpuAndPlatformAndOsAndBits(String name, String version, String cpu,
                                                        String platform, String os, String bits) {
    return productInstanceDao.findByNameAndVersionAndCpuAndPlatformAndOsAndBits(name,
                                                                                version,
                                                                                cpu,
                                                                                platform,
                                                                                os,
                                                                                bits);
  }

  public List<ProductInstance> findByNameAndVersionAndCpu(String name, String version, String cpu) {
    return productInstanceDao.findByNameAndVersionAndCpu(name, version, cpu);
  }

}

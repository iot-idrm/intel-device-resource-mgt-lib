/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;


import com.intel.iot.ams.entity.Product;

public interface ProductDao extends Dao<Integer, Product> {

  public Product findByUUID(String uuid);

  public void removeByUUID(String uuid);

  public Product findByName(String name);
}

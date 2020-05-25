/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ProductProperty;
import java.util.List;

public interface ProductPropertyDao extends Dao<Integer, ProductProperty> {

  public List<ProductProperty> findByName(String name);

  public void removeByName(String name);

}

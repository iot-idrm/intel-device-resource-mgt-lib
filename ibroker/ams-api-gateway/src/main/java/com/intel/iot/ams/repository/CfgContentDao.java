/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.CfgContent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgContentDao extends JpaRepository<CfgContent, Integer> {

  public CfgContent findBySharedName(String name);

  public CfgContent findByContentHash(String hash);

  public List<CfgContent> findByTag(String tag);

  public List<CfgContent> findBySharedNameLike(String nameLike);

  public List<CfgContent> findBySharedNameOrTag(String nameLike, String tag);

  public List<CfgContent> findBySharedNameLikeAndTag(String nameLike, String tag);

  public List<CfgContent> findByContentType(Integer contentType);
}

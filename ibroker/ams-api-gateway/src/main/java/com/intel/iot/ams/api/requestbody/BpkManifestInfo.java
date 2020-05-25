/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api.requestbody;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BpkManifestInfo {

  @SerializedName(value = "app_uuid")
  private String appUuid;

  @SerializedName(value = "app_name")
  private String appName;

  @SerializedName(value = "heap_size")
  private Integer heapSize;

  @SerializedName(value = "stack_size")
  private Integer stackSize;

  @SerializedName(value = "profile")
  private String profile;

  @SerializedName(value = "app_type")
  private String appType;

  @SerializedName(value = "version")
  private String version;

  @SerializedName(value = "api_level")
  private ApiLevel apiLevel;

  @SerializedName(value = "vendor")
  private String vendor;

  @SerializedName(value = "description")
  private String description;

  @SerializedName(value = "flash_quota")
  private Integer flashQuota;

  @SerializedName(value = "api_permission_groups")
  private List<String> permissionGroups;

  @SerializedName(value = "event")
  private AppEvent event;

  @SerializedName(value = "time_out")
  private Integer timeOut;

  @SerializedName(value = "product_dependency")
  private List<ProductDep> productDeps;

  @SerializedName(value = "lib_dependency")
  private List<LibDep> libDeps;

  public String getAppUuid() {
    return appUuid;
  }

  public void setAppUuid(String appUuid) {
    this.appUuid = appUuid;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public Integer getHeapSize() {
    return heapSize;
  }

  public void setHeapSize(Integer heapSize) {
    this.heapSize = heapSize;
  }

  public Integer getStackSize() {
    return stackSize;
  }

  public void setStackSize(Integer stackSize) {
    this.stackSize = stackSize;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public String getAppType() {
    return appType;
  }

  public void setAppType(String appType) {
    this.appType = appType;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public ApiLevel getApiLevel() {
    return apiLevel;
  }

  public void setApiLevel(ApiLevel apiLevel) {
    this.apiLevel = apiLevel;
  }

  public String getVendor() {
    return vendor;
  }

  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getFlashQuota() {
    return flashQuota;
  }

  public void setFlashQuota(Integer flashQuota) {
    this.flashQuota = flashQuota;
  }

  public List<String> getPermissionGroups() {
    return permissionGroups;
  }

  public void setPermissionGroups(List<String> permissionGroups) {
    this.permissionGroups = permissionGroups;
  }

  public AppEvent getEvent() {
    return event;
  }

  public void setEvent(AppEvent event) {
    this.event = event;
  }

  public Integer getTimeOut() {
    return timeOut;
  }

  public void setTimeOut(Integer timeOut) {
    this.timeOut = timeOut;
  }

  public List<ProductDep> getProductDeps() {
    return productDeps;
  }

  public void setProductDeps(List<ProductDep> productDeps) {
    this.productDeps = productDeps;
  }

  public List<LibDep> getLibDeps() {
    return libDeps;
  }

  public void setLibDeps(List<LibDep> libDeps) {
    this.libDeps = libDeps;
  }

  public class ApiLevel {

    @SerializedName(value = "tested")
    private List<String> tested;

    @SerializedName(value = "max")
    private String max;

    @SerializedName(value = "min")
    private String min;

    public List<String> getTested() {
      return tested;
    }

    public void setTested(List<String> tested) {
      this.tested = tested;
    }

    public String getMax() {
      return max;
    }

    public void setMax(String max) {
      this.max = max;
    }

    public String getMin() {
      return min;
    }

    public void setMin(String min) {
      this.min = min;
    }
  }

  public class AppEvent {

    @SerializedName(value = "register")
    private List<Integer> register;

    @SerializedName(value = "post")
    private List<Integer> post;

    public List<Integer> getRegister() {
      return register;
    }

    public void setRegister(List<Integer> register) {
      this.register = register;
    }

    public List<Integer> getPost() {
      return post;
    }

    public void setPost(List<Integer> post) {
      this.post = post;
    }
  }

  public class ProductDep {

    @SerializedName(value = "product_name")
    private String productName;

    @SerializedName(value = "min_version")
    private String minVersion;

    public String getProductName() {
      return productName;
    }

    public void setProductName(String productName) {
      this.productName = productName;
    }

    public String getMinVersion() {
      return minVersion;
    }

    public void setMinVersion(String minVersion) {
      this.minVersion = minVersion;
    }
  }

  public class LibDep {

    @SerializedName(value = "lib_name")
    private String libName;

    @SerializedName(value = "tested_version")
    private List<String> testedVersion;

    @SerializedName(value = "min_version")
    private List<String> minVersion;

    @SerializedName(value = "max_version")
    private List<String> maxVersion;

    public String getLibName() {
      return libName;
    }

    public void setLibName(String libName) {
      this.libName = libName;
    }

    public List<String> getTestedVersion() {
      return testedVersion;
    }

    public void setTestedVersion(List<String> testedVersion) {
      this.testedVersion = testedVersion;
    }

    public List<String> getMinVersion() {
      return minVersion;
    }

    public void setMinVersion(List<String> minVersion) {
      this.minVersion = minVersion;
    }

    public List<String> getMaxVersion() {
      return maxVersion;
    }

    public void setMaxVersion(List<String> maxVersion) {
      this.maxVersion = maxVersion;
    }
  }
}

/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Date;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class ApiJwtUser implements UserDetails {

  private static final long serialVersionUID = -5958184196594399476L;

  private String name;
  private String password;
  private String email;
  private final Date lastPasswordResetDate;
  private String pid;

  public ApiJwtUser(String name, String password, String email, String pid, Long lastPwdResetTime) {
    this.name = name;
    this.password = password;
    this.email = email;
    this.pid = pid;
    this.lastPasswordResetDate = new Date(lastPwdResetTime);
  }

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // TODO Auto-generated method stub
    return null;
  }

  @JsonIgnore
  @Override
  public String getPassword() {
    // TODO Auto-generated method stub
    return password;
  }

  @JsonIgnore
  @Override
  public String getUsername() {
    // TODO Auto-generated method stub
    return name;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonExpired() {
    // TODO Auto-generated method stub
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonLocked() {
    // TODO Auto-generated method stub
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isCredentialsNonExpired() {
    // TODO Auto-generated method stub
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isEnabled() {
    // TODO Auto-generated method stub
    return true;
  }

  public Date getLastPasswordResetDate() {
    // TODO Auto-generated method stub
    return lastPasswordResetDate;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }
}

/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.security;

import com.openiot.cloud.base.help.BaseUtil;
import com.openiot.cloud.base.help.ConstDef;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

// refer to
// https://github.com/szerhusenBC/jwt-spring-security-demo/blob/master/src/main/java/org/zerhusen/security/JwtTokenUtil.java

@Component
public class ApiJwtTokenUtil implements Serializable {
  Logger logger = LoggerFactory.getLogger(ApiJwtTokenUtil.class);
  static final String CLAIM_KEY_USERNAME = "sub";
  static final String CLAIM_KEY_CREATED = "iat";
  private static final long serialVersionUID = -3301605591108950415L;
  // @SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "It's okay
  // here")

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private Long expiration;

  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public Date getIssuedAtDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getIssuedAt);
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public String getPidFromToken(String token) {
    final Claims claims = getAllClaimsFromToken(token);
    logger.info("claims: " + claims);
    Object pid = claims.get(ConstDef.KEY_PID);
    return pid == null ? null : (String) pid;
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    if (expiration != null) {
      return expiration.before(BaseUtil.getNow());
    }
    return false;
  }

  private Boolean isCreatedAfterLastPasswordReset(Date created, Date lastPasswordReset) {
    return (lastPasswordReset != null && !created.before(lastPasswordReset));
  }

  private Boolean ignoreTokenExpiration(String token) {
    // here you specify tokens, for that the expiration is ignored
    return false;
  }

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    String pid = ((ApiJwtUser) userDetails).getPid();
    if (pid != null) {
      claims.put(ConstDef.KEY_PID, pid);
    }
    return doGenerateToken(claims, userDetails.getUsername());
  }

  public String generateToken(String pid, String username) {
    Map<String, Object> claims = new HashMap<>();
    if (pid != null) {
      claims.put(ConstDef.KEY_PID, pid);
    }
    return doGenerateToken(claims, username);
  }

  private String doGenerateToken(Map<String, Object> claims, String subject) {
    final Date createdDate = BaseUtil.getNow();
    final Date expirationDate = calculateExpirationDate(createdDate);

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(createdDate)
        .setExpiration(expirationDate)
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
    final Date created = getIssuedAtDateFromToken(token);
    return isCreatedAfterLastPasswordReset(created, lastPasswordReset)
        && (!isTokenExpired(token) || ignoreTokenExpiration(token));
  }

  public String refreshToken(String token) {
    final Date createdDate = BaseUtil.getNow();
    final Date expirationDate = calculateExpirationDate(createdDate);

    final Claims claims = getAllClaimsFromToken(token);
    claims.setIssuedAt(createdDate);
    claims.setExpiration(expirationDate);

    return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    ApiJwtUser user = (ApiJwtUser) userDetails;
    final String username = getUsernameFromToken(token);
    final Date created = getIssuedAtDateFromToken(token);
    // final Date expiration = getExpirationDateFromToken(token);
    Boolean isvalid =
        (username.equals(user.getUsername())
            && !isTokenExpired(token)
            && isCreatedAfterLastPasswordReset(created, user.getLastPasswordResetDate()));
    if (!isvalid)
      logger.info(
          "invalid: create time-"
              + created
              + " lastPasswordReset-"
              + user.getLastPasswordResetDate());
    return isvalid;
  }

  public Boolean validateToken(String token) {
    if (isTokenExpired(token)) {
      logger.info("invalid token!");
      return false;
    }
    return true;
  }

  private Date calculateExpirationDate(Date createdDate) {
    return new Date(createdDate.getTime() + expiration * 1000);
  }

  public String refreshToken(String token, Map<String, Object> newClaims) {
    final Date createdDate = BaseUtil.getNow();
    final Date expirationDate = calculateExpirationDate(createdDate);

    final Claims claims = getAllClaimsFromToken(token);
    claims.setIssuedAt(createdDate);
    claims.setExpiration(expirationDate);

    return Jwts.builder()
        .setClaims(claims)
        .addClaims(newClaims)
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }
}

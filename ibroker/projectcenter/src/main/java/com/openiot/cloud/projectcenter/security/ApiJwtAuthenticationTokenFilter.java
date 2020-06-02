/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.openiot.cloud.projectcenter.security;

import com.openiot.cloud.base.common.model.TokenContent;
import com.openiot.cloud.projectcenter.repository.ProjectRepository;
import com.openiot.cloud.projectcenter.repository.UserRepository;
import com.openiot.cloud.projectcenter.service.AuthenticationService;
import com.openiot.cloud.projectcenter.utils.ApiJwtTokenUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiJwtAuthenticationTokenFilter extends OncePerRequestFilter {
  private static final Logger logger =
      LoggerFactory.getLogger(ApiJwtAuthenticationTokenFilter.class);
  @Autowired private ApiJwtTokenUtil jwtTokenUtil;
  @Autowired private AuthenticationService authenticationService;

  @Autowired private ProjectRepository projectRepository;
  @Autowired private UserRepository userRepository;

  @Value("${jwt.header}")
  private String tokenHeader;

  @Value("${jwt.tokenHead}")
  private String tokenHead;

  private static final List<AntPathRequestMatcher> passJWTAuthenticationList =
      Stream.of(
              new AntPathRequestMatcher("/api/user", HttpMethod.POST.name()),
              new AntPathRequestMatcher("/api/user", HttpMethod.GET.name()),
              new AntPathRequestMatcher("/api/user/login"),
              new AntPathRequestMatcher("/api/user/validation"))
          .collect(Collectors.toList());

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (passJWTAuthenticationList.stream().anyMatch(matcher -> matcher.matches(request))) {
      filterChain.doFilter(request, response);
      return;
    }

    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      logger.info(
          "authFilter: authetication is not null in SecurityContext: "
              + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
      filterChain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader(this.tokenHeader);
    if (authHeader != null && authHeader.startsWith(tokenHead)) {
      final String token = authHeader.substring(tokenHead.length()); // The part after "Bearer "

      if (!token.isEmpty()) {
        TokenContent tokenContent = authenticationService.validateToken(token);
        logger.info("the token validation result {}", tokenContent);

        if (tokenContent == null) {
          SecurityContextHolder.clearContext();
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "invalid token");
          return;
        } else {
          if (tokenContent.getUser() == null) {
            logger.info("authFilter: username is null in token: {}", token);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "missing the user name");
            return;
          }

          List<SimpleGrantedAuthority> grantedAuthorityList = new ArrayList<>();
          if (tokenContent.getRole() != null) {
            grantedAuthorityList.add(new SimpleGrantedAuthority(tokenContent.getRole().getValue()));
          }

          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  tokenContent.getUser(), null, grantedAuthorityList);

          authentication.setDetails(tokenContent);

          logger.info("Set SecurityContext with {}", authentication);
          SecurityContextHolder.getContext().setAuthentication(authentication);

          filterChain.doFilter(request, response);
        }
      } else {
        logger.info("an empty token in {}", request);
        SecurityContextHolder.clearContext();
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "an empty token");
        return;
      }
    } else {
      SecurityContextHolder.clearContext();
      response.sendError(
          HttpServletResponse.SC_UNAUTHORIZED, "need an item for authentication in header");
      return;
    }
  }
}

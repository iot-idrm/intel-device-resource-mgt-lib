/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.api;


import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.entity.CfgContent;
import com.intel.iot.ams.service.ServiceBundle;

public class CfgDownloadResource extends CoapResource {

  private static final Logger logger = Logger.getLogger(CfgDownloadResource.class);

  public CfgDownloadResource() {
    super("d");
  }

  @Override
  public void handleGET(CoapExchange exchange) {
    Response resp = null;

    String contentId = null;
    String shortId = null;

    int queryCount = exchange.getRequestOptions().getURIQueryCount();
    List<String> queryStrList = exchange.getRequestOptions().getUriQuery();

    if (queryCount != 2) {
      logger.error("Invalid query count!");
      exchange.respond(ResponseCode.BAD_REQUEST);
      return;
    }

    for (int i = 0; i < queryCount; i++) {
      if (!queryStrList.get(i).startsWith("id=") && !queryStrList.get(i).startsWith("cid=")) {
        logger.error("Invalid query string, query parameters does not include contentId & client shortId!");
        exchange.respond(ResponseCode.BAD_REQUEST);
        return;
      }
    }

    for (int i = 0; i < queryCount; i++) {
      if (queryStrList.get(i).startsWith("id=")) {
        contentId = queryStrList.get(i).substring((queryStrList.get(i).indexOf('=') + 1),
                                                  queryStrList.get(i).length());
      }

      if (queryStrList.get(i).startsWith("cid=")) {
        shortId = queryStrList.get(i).substring((queryStrList.get(i).indexOf('=') + 1),
                                                queryStrList.get(i).length());
      }
    }

    if (!contentId.matches("[0-9]+")) {
      resp = new Response(ResponseCode.BAD_REQUEST);
      logger.warn("Configuration content Id must be integer!");
      resp.setPayload("Configuration content Id must be integer!");
      exchange.respond(resp);
      return;
    }

    if (!shortId.matches("[0-9]+")) {
      resp = new Response(ResponseCode.BAD_REQUEST);
      logger.warn("AMS client short Id must be integer!");
      resp.setPayload("AMS client short Id must be integer!");
      exchange.respond(resp);
      return;
    }

    AmsClient client =
        ServiceBundle.getInstance().getClientSrv().findById(Integer.valueOf(shortId));
    if (client == null) {
      resp = new Response(ResponseCode.NOT_FOUND);
      logger.error("Cannot find AMS client with short ID: " + shortId + ".");
      resp.setPayload("Cannot find AMS client with short ID: " + shortId + ".");
      exchange.respond(resp);
      return;
    }

    CfgContent content =
        ServiceBundle.getInstance().getCfgContentSrv().findById(Integer.valueOf(contentId));
    if (content == null) {
      resp = new Response(ResponseCode.NOT_FOUND);
      logger.error("Cannot find content with contentId ID: " + contentId + ".");
    } else {
      resp = new Response(ResponseCode.CONTENT);
      resp.setPayload(content.getContent());
    }


    if (resp.getCode().equals(ResponseCode.CONTENT)) {
      client.setLastConfigUpdateTime(new Date());
      ServiceBundle.getInstance().getClientSrv().update(client);
    }


    exchange.respond(resp);
  }
}

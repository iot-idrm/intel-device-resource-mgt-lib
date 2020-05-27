/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.api;

import java.util.List;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.entity.AmsTemplate;
import com.intel.iot.ams.service.ServiceBundle;
import org.apache.log4j.Logger;


public class TemplateResource extends CoapResource {

  private static final Logger logger = Logger.getLogger(TemplateResource.class);

  public TemplateResource() {
    super("t");
  }

  @Override
  public void handlePOST(CoapExchange exchange) {

    Response resp = null;
    int queryCount = exchange.getRequestOptions().getURIQueryCount();

    String tn = exchange.getQueryParameter("tn");
    String cid = exchange.getQueryParameter("cid");

    if (queryCount != 2 || tn == null || cid == null) {
      logger.warn("Client shortId & template name must be provided!");
      exchange.respond(ResponseCode.BAD_REQUEST);
      return;
    }

    AmsClient client = ServiceBundle.getInstance().getClientSrv().findById(Integer.valueOf(cid));
    if (client == null) {
      logger.error("No such client whose shortId is : "+cid);
      exchange.respond(ResponseCode.NOT_FOUND);
      return;
    }

    AmsTemplate t = ServiceBundle.getInstance().getTemplateSrv().findByName(tn);
    if (t == null) {
      logger.error("No such template whose name is : "+tn);
      exchange.respond(ResponseCode.NOT_FOUND);
      return;
    }

    client.setTemplateName(tn);
    ServiceBundle.getInstance().getClientSrv().update(client);

    resp = new Response(ResponseCode.CONTENT);
    exchange.respond(resp);
    return;
  }


  @Override
  public void handleGET(CoapExchange exchange) {
    Response resp = null;

    int queryCount = exchange.getRequestOptions().getURIQueryCount();

    if (queryCount != 0 && queryCount != 1) {
      logger.warn("Query count must be 0 or 1!");
      exchange.respond(ResponseCode.BAD_REQUEST);
      return;
    }

    if (queryCount == 1 && exchange.getQueryParameter("cid") == null) {
      logger.warn("If query count equals 1, it must be client shortId!");
      exchange.respond(ResponseCode.BAD_REQUEST);
      return;
    }

    if (queryCount == 0) {

      List<AmsTemplate> templates = ServiceBundle.getInstance().getTemplateSrv().findAll();
      if (templates == null || templates.size() <= 0) {
        logger.error("There are no template!");
        exchange.respond(ResponseCode.NOT_FOUND);
        return;
      } else {
        JsonArray jResult = new JsonArray();

        for (AmsTemplate t : templates) {
          JsonObject jElement = new JsonObject();
          jElement.addProperty("tn", t.getName());
          jElement.addProperty("desc", t.getDescription());
          jResult.add(jElement);
        }

        resp = new Response(ResponseCode.CONTENT);
        resp.setPayload(jResult.toString());
      }
    }

    if (queryCount == 1) {

      String cid = exchange.getQueryParameter("cid");
      if (!cid.matches("[0-9]+")) {
        resp = new Response(ResponseCode.BAD_REQUEST);
        logger.warn("AMS client short ID must be integer!");
        resp.setPayload("AMS client short ID must be integer!");
        exchange.respond(resp);
        return;
      }

      AmsClient client = ServiceBundle.getInstance().getClientSrv().findById(Integer.valueOf(cid));
      if (client == null || client.getTemplateName() == null) {
        logger.error("No such client or client has no template!");
        exchange.respond(ResponseCode.NOT_FOUND);
        return;
      } else {

        String tn = client.getTemplateName();
        AmsTemplate t = ServiceBundle.getInstance().getTemplateSrv().findByName(tn);
        if (t == null) {
          logger.error("Template " + tn + " can not be found!");
          exchange.respond(ResponseCode.NOT_FOUND);
          return;
        }

        JsonObject jResult = new JsonObject();
        jResult.addProperty("tn", tn);
        jResult.addProperty("desc", t.getDescription());
        logger.info("Template name : "+tn+"\n"+"Description : "+t.getDescription());

        resp = new Response(ResponseCode.CONTENT);
        resp.setPayload(jResult.toString());
      }
    }

    exchange.respond(resp);
  }
}

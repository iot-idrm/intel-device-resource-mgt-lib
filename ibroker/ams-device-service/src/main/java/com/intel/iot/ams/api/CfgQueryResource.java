/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.api;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.entity.AmsTemplate;
import com.intel.iot.ams.entity.CfgContent;
import com.intel.iot.ams.entity.CfgIdentifier;
import com.intel.iot.ams.entity.CfgInstance;
import com.intel.iot.ams.entity.Product;
import com.intel.iot.ams.entity.ProductDeploy;
import com.intel.iot.ams.requestbody.TemplateItem;
import com.intel.iot.ams.requestbody.TemplateItem.TemplateConfigItem;
import com.intel.iot.ams.service.ServiceBundle;

public class CfgQueryResource extends CoapResource {

  private static final Logger logger = Logger.getLogger(CfgQueryResource.class);

  public CfgQueryResource() {
    super("q");
  }

  private List<TemplateItem> parseTemplateContent(String content) {

    List<TemplateItem> list;
    Gson gson = new Gson();
    try {
      list = gson.fromJson(content, new TypeToken<List<TemplateItem>>() {}.getType());
    } catch (JsonSyntaxException jse) {
      return null;
    }

    return list;
  }

  @Override
  public void handleGET(CoapExchange exchange) {
    Response resp = null;

    String productName = null;
    String targetType = null;
    String targetId = null;
    String shortId = null;

    int queryCount = exchange.getRequestOptions().getURIQueryCount();
    List<String> queryStrList = exchange.getRequestOptions().getUriQuery();

    if (queryCount != 3 && queryCount != 4) {
      logger.error("Incorrect query count, must be 3 or 4!");
      exchange.respond(ResponseCode.BAD_REQUEST);
      return;
    }

    if (queryCount == 3) {
      for (int i = 0; i < queryCount; i++) {
        if (!queryStrList.get(i).startsWith("pn=") && !queryStrList.get(i).startsWith("tt=")
            && !queryStrList.get(i).startsWith("cid=")) {
          logger.warn("Query string must be product name, target type , client shortId if query count equals 3!");
          exchange.respond(ResponseCode.BAD_REQUEST);
          return;
        }
      }

      for (int i = 0; i < queryCount; i++) {
        if (queryStrList.get(i).startsWith("pn=")) {
          productName = queryStrList.get(i).substring((queryStrList.get(i).indexOf('=') + 1),
                                                      queryStrList.get(i).length());
        }
        if (queryStrList.get(i).startsWith("tt=")) {
          targetType = queryStrList.get(i).substring((queryStrList.get(i).indexOf('=') + 1),
                                                     queryStrList.get(i).length());
        }
        if (queryStrList.get(i).startsWith("cid=")) {
          shortId = queryStrList.get(i).substring((queryStrList.get(i).indexOf('=') + 1),
                                                  queryStrList.get(i).length());
        }
      }
    }

    if (queryCount == 4) {
      for (int i = 0; i < queryCount; i++) {
        if (!queryStrList.get(i).startsWith("pn=") && !queryStrList.get(i).startsWith("tt=")
            && !queryStrList.get(i).startsWith("tid=") && !queryStrList.get(i).startsWith("cid=")) {
          logger.warn("Query string must be product name, target type, target id, client shortId if query count equals 4!");
          exchange.respond(ResponseCode.BAD_REQUEST);
          return;
        }
      }

      for (int i = 0; i < queryCount; i++) {
        if (queryStrList.get(i).startsWith("pn=")) {
          productName = queryStrList.get(i).substring((queryStrList.get(i).indexOf('=') + 1),
                                                      queryStrList.get(i).length());
        }
        if (queryStrList.get(i).startsWith("tt=")) {
          targetType = queryStrList.get(i).substring((queryStrList.get(i).indexOf('=') + 1),
                                                     queryStrList.get(i).length());
        }

        if (queryStrList.get(i).startsWith("tid=")) {
          targetId = queryStrList.get(i).substring((queryStrList.get(i).indexOf('=') + 1),
                                                   queryStrList.get(i).length());
        }
        if (queryStrList.get(i).startsWith("cid=")) {
          shortId = queryStrList.get(i).substring((queryStrList.get(i).indexOf('=') + 1),
                                                  queryStrList.get(i).length());
        }
      }
    }

    if (!shortId.matches("[0-9]+")) {
      resp = new Response(ResponseCode.BAD_REQUEST);
      logger.warn("AMS client short ID must be integer!");
      resp.setPayload("AMS client short ID must be integer!");
      exchange.respond(resp);
      return;
    }


    AmsClient client =
        ServiceBundle.getInstance().getClientSrv().findById(Integer.valueOf(shortId));
    if (client == null) {
      resp = new Response(ResponseCode.NOT_FOUND);
      logger.error("Cannot find the AMS client with short ID: " + shortId);
      resp.setPayload("Cannot find the AMS client with short ID: " + shortId);
      exchange.respond(resp);
      return;
    }

    Product p = ServiceBundle.getInstance().getProductSrv().findByName(productName);
    if (p == null) {
      resp = new Response(ResponseCode.NOT_FOUND);
      logger.error("Cannot find product: " + productName);
      resp.setPayload("Cannot find product: " + productName);
      exchange.respond(resp);
      return;
    }

    List<CfgIdentifier> idList =
        ServiceBundle.getInstance().getCfgIdSrv().findByUserNameAndTargetType(p.getName(),
                                                                              targetType);
    if (idList == null) {
      logger.error("Cannot find Config identifier by product name & targetType: " + p.getName()+", "+targetType);
      exchange.respond(ResponseCode.NOT_FOUND);
      return;
    }

    JsonArray jResult = new JsonArray();
    AmsTemplate template = null;
    List<ProductDeploy> deploys = null;
    if (client.getTemplateName() != null && !client.getTemplateName().equals("")) {
      template = ServiceBundle.getInstance().getTemplateSrv().findByName(client.getTemplateName());
    }
    deploys =
        ServiceBundle.getInstance().getProductDeploySrv().findByClientUUID(client.getClientUuid());
    // If no product and template deployed, use the global default template
    if (template == null && deploys == null) {
      template = ServiceBundle.getInstance().getTemplateSrv().findByName("_global_");
    }

    List<TemplateItem> tempList = null;
    if (template != null) {
      tempList = parseTemplateContent(template.getContent());
    }

    for (CfgIdentifier id : idList) {
      CfgContent content = null;

      /** Step 1: Find exact instance content */
      if (targetId != null && !targetId.equals("")) {
        CfgInstance instance =
            ServiceBundle.getInstance()
                         .getCfgInstSrv()
                         .findByCfgIdentifierUUIDAndTargetId(id.getCfgUuid(), targetId);
        if (instance != null && instance.getContentId() != null) {
          content =
              ServiceBundle.getInstance().getCfgContentSrv().findById(instance.getContentId());
        }
      }

      /** Step 2: Find template content */
      if (content == null && tempList != null) {
        for (TemplateItem item : tempList) {
          boolean isFoundProduct = false;
          if (item.getProductName().equals(productName)) {
            isFoundProduct = true;
            for (TemplateConfigItem cfgItem : item.getCfgs()) {
              String pathName = cfgItem.getPathName();
              pathName.trim().replaceAll("\\\\", "/");
              if (pathName.startsWith("./")) {
                pathName = pathName.substring(1);
              } else {
                if (!pathName.startsWith("/")) {
                  pathName = "/" + pathName;
                }
              }

              String cfgType = cfgItem.getCfgType();

              if (pathName.equals(id.getPathName()) && cfgType.equals(id.getTargetType())) {
                content = ServiceBundle.getInstance()
                                       .getCfgContentSrv()
                                       .findBySharedName(cfgItem.getContentName());
                break;
              }
            }
          }
          if (isFoundProduct) {
            break;
          }
        }
      }

      /** Step 3: Find default content */
      if (content == null && id.getDefaultContentId() != null) {
        content = ServiceBundle.getInstance().getCfgContentSrv().findById(id.getDefaultContentId());
      }
      if (content != null) {
        JsonObject jElement = new JsonObject();
        jElement.addProperty("pn", id.getPathName());
        jElement.addProperty("id", content.getId().toString());
        jElement.addProperty("h", content.getContentHash());
        jResult.add(jElement);
      }
    }

    resp = new Response(ResponseCode.CONTENT);
    resp.setPayload(jResult.toString());
    exchange.respond(resp);
  }
}

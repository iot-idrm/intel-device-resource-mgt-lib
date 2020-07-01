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
import com.intel.iot.ams.entity.ClientCfgCheckPoint;
import com.intel.iot.ams.entity.ClientCurrentCfg;
import com.intel.iot.ams.requestbody.PostAllCfgInfo;
import com.intel.iot.ams.requestbody.TemplateItem;
import com.intel.iot.ams.requestbody.PostAllCfgInfo.CurrentCfg;
import com.intel.iot.ams.requestbody.TemplateItem.TemplateConfigItem;
import com.intel.iot.ams.service.ServiceBundle;

public class ClientAllCfgResource extends CoapResource {

  private static final Logger logger = Logger.getLogger(ClientAllCfgResource.class);

  public ClientAllCfgResource() {
    super("a");
  }

  @Override
  public void handleGET(CoapExchange exchange) {

    Response resp = null;
    int queryCount = exchange.getRequestOptions().getURIQueryCount();
    if (queryCount != 1 || exchange.getQueryParameter("cid") == null) {
      logger.warn("Client shortId must be provided!");
      exchange.respond(ResponseCode.BAD_REQUEST);
      return;
    }

    String cid = exchange.getQueryParameter("cid");
    if (!cid.matches("[0-9]+")) {
      logger.warn("Client shortId must be integer!");
      exchange.respond(ResponseCode.BAD_REQUEST);
      return;
    }

    Integer shortId = Integer.valueOf(cid);
    AmsClient client =
        ServiceBundle.getInstance().getClientSrv().findById(Integer.valueOf(shortId));
    if (client == null) {
      resp = new Response(ResponseCode.NOT_FOUND);
      logger.error("Cannot find client with short ID: " + shortId);
      resp.setPayload("Cannot find client with short ID: " + shortId);
      exchange.respond(resp);
      return;
    }

    List<ClientCfgCheckPoint> cpList =
        ServiceBundle.getInstance().getClientCheckPointSrv().findByClientId(shortId);
    if (cpList == null) {
      resp = new Response(ResponseCode.NOT_FOUND);
      logger.error("Cannot find client checkpoint with short ID: " + shortId);
      resp.setPayload("Cannot find client checkpoint with short ID: " + shortId);
      exchange.respond(resp);
      return;
    }

    if (client.getProductLock() != null && client.getProductLock()){
      logger.info("==> Client:"+client.getClientUuid()+" has been locked, can not update config");
      resp = new Response(ResponseCode.BAD_REQUEST);
      resp.setPayload("client:" + shortId + " has been locked, can not update config");
      exchange.respond(resp);
      return;
    }


    //get template_item template list in customized template content
    AmsTemplate template =
        ServiceBundle.getInstance().getTemplateSrv().findByName(client.getTemplateName());
    List<TemplateItem> tempList = null;
    if (template != null) {
      tempList = parseTemplateContent(template.getContent());
    }

    //get template_item list in project template content
    AmsTemplate proj_template =
        ServiceBundle.getInstance().getTemplateSrv().findByName("projectId_"+ client.getProjectId()+ "");
    List<TemplateItem> proj_itemList = null;
    if (proj_template != null) {
      proj_itemList = parseTemplateContent(proj_template.getContent());
    }

    //get template_item list in global template content
    AmsTemplate global_template =
        ServiceBundle.getInstance().getTemplateSrv().findByName("_global_");
    List<TemplateItem> global_itemList = null;
    if (global_template != null) {
      global_itemList = parseTemplateContent(global_template.getContent());
    }

    JsonArray jResult = new JsonArray();

    for (ClientCfgCheckPoint cp : cpList) {
      List<CfgIdentifier> idList =
          ServiceBundle.getInstance().getCfgIdSrv().findByUserNameAndTargetType(cp.getProductName(),
                                                                                cp.getTargetType());
      if (idList == null) {
        continue;
      }
      for (CfgIdentifier id : idList) {
        CfgContent content = null;

        /** Step 1: Find exact instance content */
        if (cp.getTargetId() != null && !cp.getTargetId().equals("")) {
          CfgInstance instance =
              ServiceBundle.getInstance()
                           .getCfgInstSrv()
                           .findByCfgIdentifierUUIDAndTargetId(id.getCfgUuid(), cp.getTargetId());
          if (instance != null && instance.getContentId() != null) {
            content =
                ServiceBundle.getInstance().getCfgContentSrv().findById(instance.getContentId());
          }
        }

        /** Step 2.1: Find customized template content */
        if (content == null && tempList != null) {
          for (TemplateItem item : tempList) {
            boolean isFoundProduct = false;
            if (item.getProductName() != null
                && item.getProductName().equals(cp.getProductName())) {
              isFoundProduct = true;
              if (item.getCfgs() != null) {
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
            }
            if (isFoundProduct) {
              break;
            }
          }
        }

        /** Step 2.2: Find project default template content */
        if (content == null && proj_itemList != null){
          for (TemplateItem item : proj_itemList) {
            boolean isFoundProduct = false;
            if (item.getProductName() != null
                && item.getProductName().equals(cp.getProductName())) {
              isFoundProduct = true;

              if (item.getCfgs() != null) {
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
            }
            if (isFoundProduct) {
              break;
            }
          }
        }
        
        /** Step 2.3: Find global template content */
        if (content == null && global_itemList != null){
          for (TemplateItem item : global_itemList) {
            boolean isFoundProduct = false;
            if (item.getProductName() != null
                && item.getProductName().equals(cp.getProductName())) {
              isFoundProduct = true;

              if (item.getCfgs() != null) {
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
            }
            if (isFoundProduct) {
              break;
            }
          }
        }

        /** Step 3: Find default content */
        if (content == null && id.getDefaultContentId() != null) {
          content =
              ServiceBundle.getInstance().getCfgContentSrv().findById(id.getDefaultContentId());
        }

        /**
         * Step 4: Add cfg to response only if the deployed cfg is different with the client current
         * cfg
         */
        if (content != null) {
          ClientCurrentCfg currentCfg =
              ServiceBundle.getInstance()
                           .getCurrentCfgSrv()
                           .findByClientIdAndProductNameAndTargetTypeAndTargetIdAndPathName(cp.getClientId(),
                                                                                            cp.getProductName(),
                                                                                            cp.getTargetType(),
                                                                                            cp.getTargetId(),
                                                                                            id.getPathName());

          if (currentCfg == null || !currentCfg.getHash().equals(content.getContentHash())) {
            JsonObject jElement = new JsonObject();
            jElement.addProperty("pn", id.getUserName());
            jElement.addProperty("tt", cp.getTargetType());
            if (cp.getTargetId() != null) {
              jElement.addProperty("tid", cp.getTargetId());
            }
            jElement.addProperty("id", content.getId().toString());
            jElement.addProperty("path", id.getPathName());


            jElement.addProperty("h", content.getContentHash());
            jResult.add(jElement);
          }
        }
      }
    }

    resp = new Response(ResponseCode.CONTENT);
    resp.setPayload(jResult.toString());
    exchange.respond(resp);
  }

  @Override
  public void handlePOST(CoapExchange exchange) {

    Response resp = null;
    int queryCount = exchange.getRequestOptions().getURIQueryCount();
    if (queryCount != 1 || exchange.getQueryParameter("cid") == null) {
      logger.warn("Client shortId must be provided!");
      exchange.respond(ResponseCode.BAD_REQUEST);
      return;
    }

    String cid = exchange.getQueryParameter("cid");
    if (!cid.matches("[0-9]+")) {
      logger.warn("Client shortId must be integer!");
      exchange.respond(ResponseCode.BAD_REQUEST);
      return;
    }

    Integer shortId = Integer.valueOf(cid);
    AmsClient client =
        ServiceBundle.getInstance().getClientSrv().findById(Integer.valueOf(shortId));
    if (client == null) {
      resp = new Response(ResponseCode.NOT_FOUND);
      logger.error("Cannot find client with short ID: " + shortId);
      resp.setPayload("Cannot find client with short ID: " + shortId);
      exchange.respond(resp);
      return;
    }

    byte[] reqPayload = exchange.getRequestPayload();
    List<PostAllCfgInfo> cfgs = parsePayload(reqPayload);
    if (cfgs == null) {
      resp = new Response(ResponseCode.BAD_REQUEST);
      logger.error("Post payload is not correct!");
      resp.setPayload("Post payload is not correct!");
      exchange.respond(resp);
      return;
    }

    // Remove old checkpoint and current cfg info
    ServiceBundle.getInstance().getClientCheckPointSrv().removeByClientId(shortId);
    ServiceBundle.getInstance().getCurrentCfgSrv().removeByClientId(shortId);

    for (PostAllCfgInfo info : cfgs) {
      ClientCfgCheckPoint cp = new ClientCfgCheckPoint();
      cp.setClientId(shortId);
      cp.setProductName(info.getProductName());
      cp.setTargetType(info.getTargetType());
      if (info.getTargetId() != null) {
        cp.setTargetId(info.getTargetId());
      }
      ServiceBundle.getInstance().getClientCheckPointSrv().save(cp);

      if (info.getCfgs() != null && info.getCfgs().size() > 0) {
        for (CurrentCfg c : info.getCfgs()) {
          ClientCurrentCfg currentCfg = new ClientCurrentCfg();
          currentCfg.setClientId(shortId);
          currentCfg.setProductName(info.getProductName());
          currentCfg.setTargetType(info.getTargetType());
          if (info.getTargetId() != null) {
            currentCfg.setTargetId(info.getTargetId());
          }
          currentCfg.setPathName(c.getPath());
          currentCfg.setHash(c.getHash());

          ServiceBundle.getInstance().getCurrentCfgSrv().save(currentCfg);
        }
      }
    }

    resp = new Response(ResponseCode.CONTENT);
    exchange.respond(resp);
    return;
  }

  private List<PostAllCfgInfo> parsePayload(byte[] payload) {

    List<PostAllCfgInfo> ret;
    Gson gson = new Gson();
    try {
      ret = gson.fromJson(new String(payload), new TypeToken<List<PostAllCfgInfo>>() {}.getType());
    } catch (JsonSyntaxException jse) {
      return null;
    }

    return ret;
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
}

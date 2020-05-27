/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.api;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import java.util.Date;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.entity.Product;
import com.intel.iot.ams.entity.ProductChanges;
import com.intel.iot.ams.entity.ProductDownloadPackage;
import com.intel.iot.ams.entity.ProductInstance;
import com.intel.iot.ams.service.ServiceBundle;


public class ProductChangeResource extends CoapResource {
  private static final Logger logger = Logger.getLogger(ProductChangeResource.class);

  public ProductChangeResource() {
    super("c");
  }

  @Override
  public void handleGET(CoapExchange exchange) {

    Response resp = null;

    String queryStr = exchange.getRequestOptions().getUriQueryString();
    int queryCount = exchange.getRequestOptions().getURIQueryCount();

    if (queryCount != 1 || !queryStr.startsWith("id=")) {
      resp = new Response(ResponseCode.BAD_REQUEST);
      resp.setPayload("Product change can only be queried by id!");
      logger.warn("Product change can only be queried by id!");
    } else {
      String sId = queryStr.substring((queryStr.indexOf('=') + 1), queryStr.length());
      if (!sId.matches("[0-9]+")) {
        resp = new Response(ResponseCode.BAD_REQUEST);
        resp.setPayload("Short Id must be integer!");
        logger.warn("Short Id must be integer!");

        exchange.respond(resp);
        return;
      }

      AmsClient client = ServiceBundle.getInstance().getClientSrv().findById(Integer.valueOf(sId));
      if (client == null) {
        resp = new Response(ResponseCode.NOT_FOUND);
        resp.setPayload("No such client!");
        logger.error("No such client!");
      } else {
        List<ProductChanges> changeList = ServiceBundle.getInstance()
                                                       .getProductChangeSrv()
                                                       .findByClientUUID(client.getClientUuid());
        if (changeList == null) {
          resp = new Response(ResponseCode.NOT_FOUND);
          resp.setPayload("No changes of the client!");
          logger.error("No changes of the client!");

        } else {
          JsonObject jResult = new JsonObject();
          JsonArray jDelArr = new JsonArray();
          JsonArray jInstallArr = new JsonArray();
          jResult.add("del_list", jDelArr);
          jResult.add("install_list", jInstallArr);

          for (ProductChanges change : changeList) {
            String downId = change.getDownloadId();
            if (downId != null) {
              // check the enable time, if current time is not reach the enable time, ignore this
              // product change
              if (change.getEnableTime() != null
                  && (new Date().getTime() < change.getEnableTime().getTime())) {
                continue;
              }
              Product p =
                  ServiceBundle.getInstance().getProductSrv().findByName(change.getProductName());
              if (p != null && downId.matches("[0-9]+")) {
                ProductDownloadPackage pkg = ServiceBundle.getInstance()
                                                          .getProductPkgSrv()
                                                          .findById(Integer.valueOf(downId));
                if (pkg != null) {
                  ProductInstance toInstance =
                      ServiceBundle.getInstance().getProductInstanceSrv().findById(pkg.getToId());
                  JsonObject jInstall = new JsonObject();
                  jInstall.addProperty("product_name", change.getProductName());
                  jInstall.addProperty("fmt", pkg.getFormat());
                  jInstall.addProperty("download_id", downId);
                  jInstall.addProperty("category", pkg.getCategory());
                  jInstall.addProperty("version", toInstance.getVersion());
                  jInstall.addProperty("hash", pkg.getHashcode());
                  jInstallArr.add(jInstall);
                }
              }
            } else {
              JsonObject jDel = new JsonObject();
              jDel.addProperty("product_name", change.getProductName());
              jDelArr.add(jDel);
            }
          }
          resp = new Response(ResponseCode.CONTENT);
          resp.setPayload(jResult.toString());
          logger.info("Resp: " + jResult.toString());
        }

        client.setLastConnectionTime(new Date());
        ServiceBundle.getInstance().getClientSrv().update(client);
      }
    }
    exchange.respond(resp);
  }
}

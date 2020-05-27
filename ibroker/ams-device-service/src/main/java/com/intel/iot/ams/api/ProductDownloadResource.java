/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.api;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.entity.Product;
import com.intel.iot.ams.entity.ProductDownloadHistory;
import com.intel.iot.ams.entity.ProductDownloadPackage;
import com.intel.iot.ams.service.ServiceBundle;
import com.intel.iot.ams.utils.AmsConstant;


public class ProductDownloadResource extends CoapResource {

  private static final Logger logger = Logger.getLogger(ProductDownloadResource.class);

  public ProductDownloadResource() {
    super("d");
  }

  @Override
  public void handleGET(CoapExchange exchange) {
    Response resp = null;

    String downloadId = null;
    String shortId = null;


    int queryCount = exchange.getRequestOptions().getURIQueryCount();
    List<String> queryStrList = exchange.getRequestOptions().getUriQuery();

    if (queryCount != 2) {
      logger.error("Query count must be 2 but provide:"+queryStrList);
      exchange.respond(ResponseCode.BAD_REQUEST);
      return;
    }

    for (int i = 0; i < queryCount; i++) {
      if (!queryStrList.get(i).startsWith("id=") && !queryStrList.get(i).startsWith("cid=")) {
        logger.warn("Query parameters must include downloadId & shortId!");
        exchange.respond(ResponseCode.BAD_REQUEST);
        return;
      }
    }

    for (int i = 0; i < queryCount; i++) {
      if (queryStrList.get(i).startsWith("id=")) {
        downloadId = queryStrList.get(i).substring((queryStrList.get(i).indexOf('=') + 1),
                                                   queryStrList.get(i).length());
      }

      if (queryStrList.get(i).startsWith("cid=")) {
        shortId = queryStrList.get(i).substring((queryStrList.get(i).indexOf('=') + 1),
                                                queryStrList.get(i).length());
      }
    }

    if (!downloadId.matches("[0-9]+")) {
      resp = new Response(ResponseCode.BAD_REQUEST);
      resp.setPayload("Product package download Id must be integer!");
      logger.warn("Product package download Id must be integer!");
      exchange.respond(resp);
      return;
    }

    if (!shortId.matches("[0-9]+")) {
      resp = new Response(ResponseCode.BAD_REQUEST);
      resp.setPayload("AMS client short Id must be integer!");
      logger.warn("AMS client short Id must be integer!");
      exchange.respond(resp);
      return;
    }

    AmsClient client =
        ServiceBundle.getInstance().getClientSrv().findById(Integer.valueOf(shortId));
    if (client == null) {
      resp = new Response(ResponseCode.NOT_FOUND);
      resp.setPayload("Cannot find AMS client with short ID: " + shortId + ".");
      logger.error("Cannot find AMS client with short ID: " + shortId + ".");
    }

    Product p = null;
    ProductDownloadPackage pkg =
        ServiceBundle.getInstance().getProductPkgSrv().findById(Integer.valueOf(downloadId));
    if (pkg == null) {
      resp = new Response(ResponseCode.NOT_FOUND);
      logger.error("Download package not found with downloadId: " + downloadId);
    } else {
      p = ServiceBundle.getInstance().getProductSrv().findByName(pkg.getProductName());
      if (p == null) {
        resp = new Response(ResponseCode.NOT_FOUND);
        resp.setPayload("Product is not existed in AMS repo.");
        logger.error("Product is not existed in AMS repo: " + pkg.getProductName());
      }

      String pkgPath = AmsConstant.downloadPath + pkg.getHashcode() + "." + pkg.getFormat();
      File pkgFile = new File(pkgPath);
      if (pkgFile.exists() != true) {
        resp = new Response(ResponseCode.NOT_FOUND);
        logger.error("Package file can not be found!");
      }
      byte[] buffer = null;
      try {
        buffer = FileUtils.readFileToByteArray(pkgFile);
      } catch (IOException ioe) {
        buffer = null;
      }
      if (buffer == null) {
        resp = new Response(ResponseCode.INTERNAL_SERVER_ERROR);
        resp.setPayload("Read download package error!");
        logger.error("Read download package error!");

      } else {
        resp = new Response(ResponseCode.CONTENT);
        resp.setPayload(buffer);
      }
    }

    if (resp.getCode().equals(ResponseCode.CONTENT)) {
      pkg.setLastUsedTime(new Date());
      ServiceBundle.getInstance().getProductPkgSrv().update(pkg);

      client.setLastProductUpdateTime(new Date());
      ServiceBundle.getInstance().getClientSrv().update(client);

      ProductDownloadHistory history = new ProductDownloadHistory();
      history.setAmsClientUuid(client.getClientUuid());
      history.setProductName(p.getName());
      history.setCategory(pkg.getCategory());
      history.setFromId(pkg.getFromId());
      history.setToId(pkg.getToId());
      history.setDownloadTime(new Date());

      ServiceBundle.getInstance().getProductHistorySrv().save(history);
    }

    exchange.respond(resp);
  }
}

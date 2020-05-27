/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.entity.AmsTask;
import com.intel.iot.ams.entity.ProductInstalled;
import com.intel.iot.ams.requestbody.InstalledProductRequest;
import com.intel.iot.ams.requestbody.InstalledProductRequest.ProductInfo;
import com.intel.iot.ams.service.ServiceBundle;
import com.intel.iot.ams.task.AmsTaskType;
import org.apache.log4j.Logger;



public class ProductInstalledResource extends CoapResource {

  private static final Logger logger=Logger.getLogger(ProductInstalledResource.class);

  public ProductInstalledResource() {
    super("installed");
  }

  private InstalledProductRequest parsePayload(byte[] payload) {

    InstalledProductRequest request;
    Gson gson = new Gson();
    try {
      request = gson.fromJson(new String(payload), InstalledProductRequest.class);
    } catch (JsonSyntaxException jse) {
      return null;
    }

    return request;
  }

  private List<ProductInstalled> parseRequest(InstalledProductRequest request, String uuid) {
    if (request == null) {
      return null;
    }

    List<ProductInstalled> infoList = new ArrayList<ProductInstalled>();
    if (request.getProductList() != null) {
      for (ProductInfo info : request.getProductList()) {
        ProductInstalled install = new ProductInstalled();
        install.setClientUuid(uuid);
        install.setProductName(info.getProductName());
        install.setVersion(info.getVersion());
        if (info.getAot() != null) {
          install.setIsAot(info.getAot());
        }
        infoList.add(install);
      }
    }
    return infoList;
  }

  @Override
  public void handlePOST(CoapExchange exchange) {
    byte[] reqPayload = exchange.getRequestPayload();
    Response resp = null;

    InstalledProductRequest request = parsePayload(reqPayload);
    if (request == null) {
      resp = new Response(ResponseCode.BAD_REQUEST);
      resp.setPayload("Post payload is not correct!");
      logger.error("Post payload is not correct!");
      exchange.respond(resp);
      return;
    }

    String sId = request.getShortId();
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
      resp.setPayload("Cannot find client with short ID: " + sId);
      logger.error("Cannot find client with short ID: " + sId);
      exchange.respond(resp);
      return;
    }

    /** Update ams client version, fw version, client wasm support info */
    boolean clientUpdate = false;

    /**
     * Because AmsClientVersion/FwVersion.. may be NULL, so should judge the value first
     * Or there will report NULLPOINTEREXCEPTION when using .equals
     * */
    if (isEmpty(client.getAmsClientVersion())||
            (!client.getAmsClientVersion().equals(request.getAmsVersion()))) {
      client.setAmsClientVersion(request.getAmsVersion());
      clientUpdate = true;
    }

    if (isEmpty(client.getFwVersion())||
            (!client.getFwVersion().equals(request.getFwVersion()))) {
      client.setFwVersion(request.getFwVersion());
      clientUpdate = true;
    }

    if (request.getWasm() != null) {
      client.setAotEnable(request.getWasm().getAot());
      client.setWasmEnable(request.getWasm().getBytecode());
      if (request.getWasm().getBytecode() == true) {
        client.setWasmVersion(request.getWasm().getApiVersion());
      }
      clientUpdate = true;
    }

    if (clientUpdate) {
      ServiceBundle.getInstance().getClientSrv().update(client);
    }

    List<ProductInstalled> install_list = parseRequest(request, client.getClientUuid());

    /** Update product installed data */
    if (install_list != null) {
      ServiceBundle.getInstance()
                   .getProductInstalledSrv()
                   .removeByClientUuid(client.getClientUuid());
      for (ProductInstalled install : install_list) {
        ServiceBundle.getInstance().getProductInstalledSrv().save(install);
      }
    }

    /** Delete all old product changes of this client */
    ServiceBundle.getInstance().getProductChangeSrv().removeByClientUuid(client.getClientUuid());

    /** Create a AmsTask to calculate product changes of this client */
    AmsTask task = new AmsTask();
    task.setTaskType(AmsTaskType.CALCULATE_PRODUCT_CHANGES);
    task.setTaskCreateTime(new Date());
    JsonObject jTaskProperty = new JsonObject();
    jTaskProperty.addProperty("client_uuid", client.getClientUuid());
    task.setTaskProperties(jTaskProperty.toString());
    ServiceBundle.getInstance().getTaskSrv().save(task);

    resp = new Response(ResponseCode.CONTENT);
    exchange.respond(resp);
    return;
  }


  /**
   *  A tool to judge whether the Object is Empty
   * */
  public static boolean isEmpty(Object obj)
  {
    if (obj == null)
    {
      return true;
    }
    if ((obj instanceof List))
    {
      return ((List) obj).size() == 0;
    }
    if ((obj instanceof String))
    {
      return ((String) obj).trim().equals("");
    }
    return false;
  }

}

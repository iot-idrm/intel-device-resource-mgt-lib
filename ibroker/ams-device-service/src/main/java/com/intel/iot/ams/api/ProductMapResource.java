/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.api;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.entity.AmsTask;
import com.intel.iot.ams.entity.ClientDeviceMapping;
import com.intel.iot.ams.entity.Product;
import com.intel.iot.ams.entity.ProductDeploy;
import com.intel.iot.ams.entity.ProductInstance;
import com.intel.iot.ams.requestbody.ClientDeviceMappingRequest;
import com.intel.iot.ams.service.ServiceBundle;
import com.intel.iot.ams.task.AmsTaskType;
import com.intel.iot.ams.utils.AotToolUtils;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

public class ProductMapResource extends CoapResource {

  private static final Logger logger=Logger.getLogger(ProductMapResource.class);

  public ProductMapResource() {
    super("prod_map");
  }

  private ClientDeviceMappingRequest parsePayload(byte[] payload) {

    String temp = new String(payload);
    ClientDeviceMappingRequest request;
    Gson gson = new Gson();
    try {
      request = gson.fromJson(temp, ClientDeviceMappingRequest.class);
    } catch (JsonSyntaxException jse) {
      return null;
    }

    return request;
  }

  @Override
  public void handlePOST(CoapExchange exchange) {

    byte[] reqPayload = exchange.getRequestPayload();
    Response resp = null;

    ClientDeviceMappingRequest request = parsePayload(reqPayload);
    if (request == null) {
      resp = new Response(ResponseCode.BAD_REQUEST);
      logger.error("Post payload is not correct!");
      resp.setPayload("Post payload is not correct!");
      exchange.respond(resp);
      return;
    }

    String sId = request.getShortId();
    if (!sId.matches("[0-9]+")) {
      resp = new Response(ResponseCode.BAD_REQUEST);
      logger.warn("Short Id must be integer!");
      resp.setPayload("Short Id must be integer!");
      exchange.respond(resp);
      return;
    }

    Product p = ServiceBundle.getInstance().getProductSrv().findByName(request.getProductName());
    if (p == null) {
      resp = new Response(ResponseCode.NOT_FOUND);
      resp.setPayload("No such product: " + request.getProductName());
      logger.error("No such product: " + request.getProductName());
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

    ClientDeviceMapping mapping =
        ServiceBundle.getInstance().getMapSrv().findByProductDeviceId(request.getProductDeviceId());
    if (mapping != null) {
      if (!mapping.getProductName().equals(p.getName())) {
        resp = new Response(ResponseCode.BAD_REQUEST);
        resp.setPayload("Product Device Id: " + request.getProductDeviceId()
            + " has already been used by another product");
        logger.error("Product Device Id: " + request.getProductDeviceId()
                + " has already been used by another product");
        exchange.respond(resp);
        return;
      }

      if (mapping.getClientUuid().equals(client.getClientUuid())) {
        resp = new Response(ResponseCode.CONTENT);
        resp.setPayload("Product Device Id: " + request.getProductDeviceId() + " of Product: "
            + request.getProductName() + " has already been mapped to client UUID: "
            + client.getClientUuid());
      } else {
        resp = new Response(ResponseCode.BAD_REQUEST);
        resp.setPayload("Product Device Id: " + request.getProductDeviceId() + " of Product: "
            + request.getProductName() + " has already been mapped to another client UUID: "
            + mapping.getClientUuid());

        logger.error("Product Device Id: " + request.getProductDeviceId() + " of Product: "
                + request.getProductName() + " has already been mapped to another client UUID: "
                + mapping.getClientUuid());
      }
      exchange.respond(resp);
      return;
    }

    /** Delete the old record */
    ServiceBundle.getInstance().getMapSrv().removeByClientUuid(client.getClientUuid());

    /** Create new mapping record */
    mapping = new ClientDeviceMapping();
    mapping.setClientUuid(client.getClientUuid());
    mapping.setProductDeviceId(request.getProductDeviceId());
    mapping.setProductName(p.getName());
    ServiceBundle.getInstance().getMapSrv().save(mapping);

    /** Update the product settings */
    List<ProductDeploy> settingList = ServiceBundle.getInstance()
                                                   .getProductDeploySrv()
                                                   .findByDeviceId(request.getProductDeviceId());
    if (settingList != null) {
      for (ProductDeploy setting : settingList) {
        if (setting.getClientUuid() == null) {
          /** If the product is fw_app_wasm, then it should do the AOT related check */
          // If the deploy is illegal, it will delete this deploy record
          boolean isLegal = true;
          if (setting.getIsAot() == true) {
            Product deployedProduct =
                ServiceBundle.getInstance().getProductSrv().findByName(setting.getProductName());
            if (deployedProduct.getCategory() == 5) {
              ProductInstance instance =
                  ServiceBundle.getInstance()
                               .getProductInstanceSrv()
                               .findByNameAndVersionAndCpuAndPlatformAndOs(setting.getProductName(),
                                                                           setting.getVersion(),
                                                                           null,
                                                                           null,
                                                                           null);
              if (instance != null) {
                if (instance.getAotEnable() == null || instance.getAotEnable() != true) {
                  isLegal = false;
                }
                if (client.getAotEnable() != true) {
                  isLegal = false;
                }
                if (!AotToolUtils.isPlatformSupported(client.getCpu())) {
                  isLegal = false;
                }
              }
            }
          }
          if (isLegal == true) {
            setting.setClientUuid(client.getClientUuid());
            ServiceBundle.getInstance().getProductDeploySrv().update(setting);
          } else {
            ServiceBundle.getInstance().getProductDeploySrv().delete(setting);
          }
        }
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
    resp.setPayload("Success!");
    exchange.respond(resp);
    return;
  }

}

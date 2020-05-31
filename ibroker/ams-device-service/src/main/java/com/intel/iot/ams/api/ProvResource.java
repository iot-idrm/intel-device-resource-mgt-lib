/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.api;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.intel.iot.ams.service.ServiceBundle;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.requestbody.ClientProvisioningRequest;
import org.apache.log4j.Logger;


public class ProvResource extends CoapResource {

  private static final Logger logger = Logger.getLogger(ProvResource.class);

  public ProvResource() {
    super("prov");
  }

  private ClientProvisioningRequest parsePayload(byte[] payload) {

    ClientProvisioningRequest request;
    Gson gson = new Gson();
    try {
      request = gson.fromJson(new String(payload), ClientProvisioningRequest.class);
    } catch (JsonSyntaxException jse) {
      return null;
    }

    return request;
  }


  public boolean checkVerFormat(String version) {
    Pattern pattern = Pattern.compile("^\\d+\\.\\d+(\\.\\d+)?$");
    Matcher isVer = pattern.matcher(version);
    if (!isVer.matches()) {
      return false;
    }
    return true;
  }

  @Override
  public void handlePOST(CoapExchange exchange) {

    byte[] reqPayload = exchange.getRequestPayload();
    Response resp = null;

    ClientProvisioningRequest request = parsePayload(reqPayload);
    if (request == null || request.getDi() == null || request.getSerial() == null) {
      resp = new Response(ResponseCode.BAD_REQUEST);
      resp.setPayload("Request Di or serial number cannot be null!");
      logger.warn(" Request Di or serial number cannot be null! ");
      exchange.respond(resp);
      return;
    }
    logger.info("  Di:  "+request.getDi());

    String clientUuid = null;
    AmsClient client =
        ServiceBundle.getInstance().getClientSrv().findByClientUUID(request.getDi());
    if (client == null) {
      clientUuid = request.getDi();
      client = new AmsClient();
      client.setClientUuid(request.getDi());
      client.setCpu(request.getCpu());
      client.setAmsClientVersion(request.getAmsVersion());
      client.setBits(request.getBits());
      if (request.getTemplate() != null) {
        client.setTemplateName(request.getTemplate());
      }
      if (request.getPlatform() != null) {
        client.setPlatform(request.getPlatform());
      }
      client.setOs(request.getOs());
      if (request.getOsVer() != null && checkVerFormat(request.getOsVer())) {
        client.setOsVer(request.getOsVer());
      }
      client.setSystem(request.getSystem());
      if (request.getSysVer() != null && checkVerFormat(request.getSysVer())) {
        client.setSysVer(request.getSysVer());
      }
      client.setSystem(request.getSystem());
      if (request.getDescription() != null) {
        client.setDescription(request.getDescription());
      }
      if (request.getDeviceName() != null) {
        client.setDeviceName(request.getDeviceName());
      }
      client.setSerial(request.getSerial());
      client.setProvisionTime(new Date());
      if (request.getFwVersion() != null) {
        client.setFwVersion(request.getFwVersion());
      }
      if (request.getAotEnable() != null) {
        client.setAotEnable(request.getAotEnable());
      }
      if (request.getWasmEnable() != null) {
        client.setWasmEnable(request.getWasmEnable());
        client.setWasmVersion(request.getWasmVersion());
      }
      if (request.getDeviceType() != null) {
        client.setDeviceType(request.getDeviceType());
      }
      ServiceBundle.getInstance().getClientSrv().save(client);
    } else {
      clientUuid = client.getClientUuid();
      if (request.getDescription() != null) {
        client.setDescription(request.getDescription());
      }
      if (request.getDeviceName() != null) {
        client.setDeviceName(request.getDeviceName());
      }
      if (request.getFwVersion() != null) {
        client.setFwVersion(request.getFwVersion());
      }
      if (request.getAotEnable() != null) {
        client.setAotEnable(request.getAotEnable());
      }
      if (request.getWasmEnable() != null) {
        client.setWasmEnable(request.getWasmEnable());
        client.setWasmVersion(request.getWasmVersion());
      }

      ServiceBundle.getInstance().getClientSrv().update(client);
    }

    JsonObject jResult = new JsonObject();
    jResult.addProperty("short_id", client.getId().toString());
    jResult.addProperty("uuid", clientUuid);

    resp = new Response(ResponseCode.CONTENT);
    resp.setPayload(jResult.toString());

    exchange.respond(resp);
  }

}

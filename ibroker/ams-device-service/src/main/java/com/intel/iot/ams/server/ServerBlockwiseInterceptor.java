/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.server;

import org.eclipse.californium.core.coap.BlockOption;
import org.eclipse.californium.core.coap.EmptyMessage;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.interceptors.MessageInterceptor;
import org.apache.log4j.Logger;

public class ServerBlockwiseInterceptor implements MessageInterceptor {

  private static Logger logger = Logger.getLogger(ServerBlockwiseInterceptor.class);

  private StringBuilder buffer = new StringBuilder();
  // public ReceiveRequestHandler handler;

  @Override
  public void sendRequest(Request request) {
    buffer.append("\nERROR: Server sent " + request + "\n");
  }

  @Override
  public void sendResponse(Response response) {
    StringBuffer buf = new StringBuffer();
    buf.append(String.format("\nResponse to [%s:%d]: <-----   %s [MID=%d], %s%s%s%s",
                             response.getDestination(),
                             response.getDestinationPort(),
                             response.getType(),
                             response.getMID(),
                             response.getCode(),
                             blockOptionString(1, response.getOptions().getBlock1()),
                             blockOptionString(2, response.getOptions().getBlock2()),
                             observeOptionString(response.getOptions())));
    logger.info(buf.toString());

    // System.out.println("<---" + response.getDestination()+ ": " +
    // response.getDestinationPort());
  }

  @Override
  public void sendEmptyMessage(EmptyMessage message) {
    StringBuffer buf = new StringBuffer();
    buf.append(String.format("\n<-----   %s [MID=%d], 0", message.getType(), message.getMID()));
    logger.info(buf.toString());
  }

  @Override
  public void receiveRequest(Request request) {
    StringBuffer buf = new StringBuffer();
    buf.append(String.format("\nRequest from [%s:%d]: %s [MID=%d], %s, /%s?%s%s%s%s    ----->",
                             request.getSource(),
                             request.getSourcePort(),
                             request.getType(),
                             request.getMID(),
                             request.getCode(),
                             request.getOptions().getUriPathString(),
                             request.getOptions().getUriQueryString(),
                             blockOptionString(1, request.getOptions().getBlock1()),
                             blockOptionString(2, request.getOptions().getBlock2()),
                             observeOptionString(request.getOptions())));
    logger.info(buf.toString());

    // System.out.println(request.getSource()+ ": " +
    // request.getSourcePort() + "-->");
    // if (null != handler) handler.receiveRequest(request);
  }

  @Override
  public void receiveResponse(Response response) {
    buffer.append("ERROR: Server received " + response);
  }

  @Override
  public void receiveEmptyMessage(EmptyMessage message) {
    buffer.append(String.format("\n%-19s                       ----->",
                                String.format("%s [MID=%d], 0",
                                              message.getType(),
                                              message.getMID())));
  }

  public void log(String str) {
    buffer.append(str);
  }

  private String blockOptionString(int nbr, BlockOption option) {
    if (option == null)
      return "";
    return String.format(", %d:%d/%d/%d",
                         nbr,
                         option.getNum(),
                         option.isM() ? 1 : 0,
                         option.getSize());
  }

  private String observeOptionString(OptionSet options) {
    if (options == null)
      return "";
    if (!options.hasObserve())
      return "";
    return ", observe(" + options.getObserve() + ")";
  }

  public String toString() {
    return buffer.append("\n").substring(1);
  }

  public void clear() {
    buffer = new StringBuilder();
  }

}

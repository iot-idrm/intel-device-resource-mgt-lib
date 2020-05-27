/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.server;

import com.intel.iot.ams.task.AmsTaskHandler;

public class Main {

  public static void main(String[] args) {
    boolean logOn = true;

    if (args.length > 0 && args[0].trim().toLowerCase().equals("log_on")) {
      logOn = true;
    }

    AmsCoapTcpServer tcpServer = new AmsCoapTcpServer(logOn);
    tcpServer.start();
    System.out.println("\n\n====================AMS CoAP TCP server start================\n\n");

    AmsCoapUdpServer udpServer = new AmsCoapUdpServer(logOn);
    udpServer.start();
    System.out.println("\n\n====================AMS CoAP UDP server start================\n\n");

    if (logOn) {
      System.out.println("\n\n====================AMS server Log is ON================\n\n");
    } else {
      System.out.println("\n\n====================AMS server Log is OFF================\n\n");
    }
    /**
     * will constantly query task per 5s
     **/
    Runnable taskHandler = new AmsTaskHandler();
    Thread task = new Thread(taskHandler);
    task.start();

    System.out.println("\n\n====================AMS Task handler thread start================\n\n");

  }

}

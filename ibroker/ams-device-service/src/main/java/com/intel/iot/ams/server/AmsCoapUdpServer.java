/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.server;

import java.net.InetSocketAddress;

import com.intel.iot.ams.api.*;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import com.intel.iot.ams.service.ServiceBundle;

public class AmsCoapUdpServer extends CoapServer {

  public AmsCoapUdpServer(boolean logOn) {

    /* Initialize all the database service */
    ServiceBundle.getInstance();

    /* Build the resource tree */
    AmsRootResource ams = new AmsRootResource();
    APIVersionResource version = new APIVersionResource();
    ProvResource prov = new ProvResource();
    ProductResource product = new ProductResource();
    ConfigResource config = new ConfigResource();
    ProductMapResource map = new ProductMapResource();
    ProductInstalledResource install = new ProductInstalledResource();
    ProductChangeResource change = new ProductChangeResource();
    ProductDownloadResource download = new ProductDownloadResource();
    CfgQueryResource query = new CfgQueryResource();
    CfgDownloadResource cfgDown = new CfgDownloadResource();
    ClientAllCfgResource cfgAll = new ClientAllCfgResource();
    TemplateResource tmplt=new TemplateResource();


    ams.add(version);
    version.add(prov); /** API: /ams/v1/prov   */
    version.add(map); /** API: /ams/v1/prod_map   */
    version.add(product);
    version.add(config);
    version.add(tmplt);/** API: /ams/v1/t **/

    product.add(install); /** API: /ams/v1/p/installed   */
    product.add(change); /** API: /ams/v1/p/c   */
    product.add(download); /** API: /ams/v1/p/d   */
    config.add(query); /** API: /ams/v1/c/q   */
    config.add(cfgDown); /** API: /ams/v1/c/d   */
    config.add(cfgAll); /** API: /ams/v1/c/a */

    this.add(ams);

    this.addEndpoints(logOn);
  }

  /**
   * Add individual endpoints listening on default CoAP port on all IPv4 addresses of all network interfaces.
   */
  private void addEndpoints(boolean logOn) {
    NetworkConfig netCfg =
        new NetworkConfig().setInt(NetworkConfig.Keys.MAX_MESSAGE_SIZE, 1024)
                           .setInt(NetworkConfig.Keys.PREFERRED_BLOCK_SIZE, 1024)
                           .setInt(NetworkConfig.Keys.TCP_CONNECT_TIMEOUT, 1000 * 60 * 1)
                           .setInt(NetworkConfig.Keys.BLOCKWISE_STATUS_LIFETIME, 1000 * 60 * 5)
                           .setInt(NetworkConfig.Keys.MARK_AND_SWEEP_INTERVAL, 1000 * 60 * 1)
                           .setLong(NetworkConfig.Keys.EXCHANGE_LIFETIME, 1000 * 247);

    InetSocketAddress bindToAddress = new InetSocketAddress("0.0.0.0", 5555);
    CoapEndpoint endpoint = new CoapEndpoint(bindToAddress, netCfg);
    if (logOn) {
      endpoint.addInterceptor(new ServerBlockwiseInterceptor());
    }
    addEndpoint(endpoint);
  }

}

/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;


public class ServiceBundle {

  private AmsClientService clientSrv;
  private AmsTaskService taskSrv;
  private CfgContentService cfgContentSrv;
  private CfgIdentifierService cfgIdSrv;
  private CfgInstanceService cfgInstSrv;
  private ClientDeviceMappingService mapSrv;
  private HistoricCfgInstanceService historicCfgSrv;
  private ProductChangesService productChangeSrv;
  private ProductDownloadPackageService productPkgSrv;
  private ProductInstalledService productInstalledSrv;
  private ProductInstanceService productInstanceSrv;
  private ProductPropertyService productPropertySrv;
  private ProductService productSrv;
  private ProductDeployService productDeploySrv;
  private ProductDownloadHistoryService productHistorySrv;
  private AmsTemplateService templateSrv;
  private ClientCurrentCfgService currentCfgSrv;
  private ClientcfgCheckPointService clientCheckPointSrv;


  private ServiceBundle() {
    clientSrv = new AmsClientService();
    taskSrv = new AmsTaskService();
    cfgContentSrv = new CfgContentService();
    cfgIdSrv = new CfgIdentifierService();
    cfgInstSrv = new CfgInstanceService();
    mapSrv = new ClientDeviceMappingService();
    historicCfgSrv = new HistoricCfgInstanceService();
    productChangeSrv = new ProductChangesService();
    productPkgSrv = new ProductDownloadPackageService();
    productInstalledSrv = new ProductInstalledService();
    productInstanceSrv = new ProductInstanceService();
    productPropertySrv = new ProductPropertyService();
    productSrv = new ProductService();
    productDeploySrv = new ProductDeployService();
    productHistorySrv = new ProductDownloadHistoryService();
    templateSrv = new AmsTemplateService();
    currentCfgSrv = new ClientCurrentCfgService();
    clientCheckPointSrv = new ClientcfgCheckPointService();
  }

  public static ServiceBundle getInstance() {
    return ServiceBundleHolder.instance;
  }

  private static class ServiceBundleHolder {
    private final static ServiceBundle instance = new ServiceBundle();
  }

  public AmsClientService getClientSrv() {
    return clientSrv;
  }

  public AmsTaskService getTaskSrv() {
    return taskSrv;
  }

  public CfgContentService getCfgContentSrv() {
    return cfgContentSrv;
  }

  public CfgIdentifierService getCfgIdSrv() {
    return cfgIdSrv;
  }

  public CfgInstanceService getCfgInstSrv() {
    return cfgInstSrv;
  }

  public ClientDeviceMappingService getMapSrv() {
    return mapSrv;
  }

  public HistoricCfgInstanceService getHistoricCfgSrv() {
    return historicCfgSrv;
  }

  public ProductChangesService getProductChangeSrv() {
    return productChangeSrv;
  }

  public ProductDownloadPackageService getProductPkgSrv() {
    return productPkgSrv;
  }

  public ProductInstalledService getProductInstalledSrv() {
    return productInstalledSrv;
  }

  public ProductInstanceService getProductInstanceSrv() {
    return productInstanceSrv;
  }

  public ProductPropertyService getProductPropertySrv() {
    return productPropertySrv;
  }

  public ProductService getProductSrv() {
    return productSrv;
  }

  public ProductDeployService getProductDeploySrv() {
    return productDeploySrv;
  }

  public ProductDownloadHistoryService getProductHistorySrv() {
    return productHistorySrv;
  }

  public AmsTemplateService getTemplateSrv() {
    return templateSrv;
  }

  public ClientCurrentCfgService getCurrentCfgSrv() {
    return currentCfgSrv;
  }

  public ClientcfgCheckPointService getClientCheckPointSrv() {
    return clientCheckPointSrv;
  }

}

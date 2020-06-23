/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intel.iot.ams.api.requestbody.AddProductForClientInfo;
import com.intel.iot.ams.entity.*;
import com.intel.iot.ams.service.*;
import com.intel.iot.ams.task.AmsTaskType;
import com.intel.iot.ams.utils.AotToolUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RESTAPIs implementation of APIs document Chapter 2.3 Client product deploy management
 *
 * @author Zhang, Yi Y <yi.y.zhang@intel.com>
 */
@RestController
@RequestMapping("/ams_user_cloud")
public class ClientProductDeployAPIs {

  @Autowired private ProductDeployService deploySrv;

  @Autowired private ProductInstalledService installSrv;

  @Autowired private ClientDeviceMappingService mappingSrv;

  @Autowired private ProductService pSrv;

  @Autowired private ProductInstanceService pInstanceSrv;

  @Autowired private AmsClientService clientSrv;

  @Autowired private ProductDependencyService pdSrv;

  @Autowired private AmsTaskService taskSrv;

  @Autowired private ProductDownloadHistoryService historySrv;

  @Autowired private ProductChangesService changeSrv;

  @Autowired private LogService logSrv;

  // ----------------------------------------------------------------
  //
  // RESTful APIs
  //
  // ----------------------------------------------------------------

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.3.1 Query client product deploy
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Get client product deploy
   *
   * <p>RESTful API: GET /ams/v1/product/deploy
   *
   * @return an instance of ResponseEntity<String> with response code and client product deploy
   *     information in JSON string
   */
  @RequestMapping(
      value = "/ams/v1/product/deploy",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getClientProductDeploy(
      @RequestParam(value = "client_uuid", required = false) String clientUuid,
      @RequestParam(value = "product_device_id", required = false) String productDeviceId,
      @RequestParam(value = "product_name", required = false) String productName,
      @RequestParam(value = "offset", required = false) Integer offset,
      @RequestParam(value = "limit", required = false) Integer limit) {

    if (clientUuid == null && productDeviceId == null && productName == null) {
      JsonArray jReturn = new JsonArray();
      List<ProductDeploy> deployList = deploySrv.findAll(offset, limit);
      if (deployList != null) {
        for (ProductDeploy deploy : deployList) {
          JsonObject jDeploy = new JsonObject();
          if (deploy.getClientUuid() != null) {
            jDeploy.addProperty("client_uuid", deploy.getClientUuid());
          }
          if (deploy.getProductDeviceId() != null) {
            jDeploy.addProperty("product_device_id", deploy.getClientUuid());
          }
          jDeploy.addProperty("product_name", deploy.getProductName());
          jDeploy.addProperty("version", deploy.getVersion());
          if (deploy.getIsAot() != null) {
            jDeploy.addProperty("is_aot", deploy.getIsAot());
          }
          jReturn.add(jDeploy);
        }
      }
      return new ResponseEntity<String>(jReturn.toString(), HttpStatus.OK);
    }

    JsonObject jResult = new JsonObject();
    if (clientUuid != null) {
      if (productDeviceId != null) {
        return new ResponseEntity<String>(
            "Query parameter \"client_uuid\" cannot be used with \"product_device_id\"",
            HttpStatus.BAD_REQUEST);
      }

      AmsClient client = clientSrv.findByClientUUID(clientUuid);
      if (client == null) {
        return new ResponseEntity<String>(
            "client: " + clientUuid + " does not existed!", HttpStatus.NOT_FOUND);
      }

      jResult.addProperty("client_uuid", clientUuid);
      jResult.addProperty("device_name", client.getDeviceName());
      JsonArray jDeploys = new JsonArray();
      jResult.add("product_deploy", jDeploys);
      JsonArray jInstalls = new JsonArray();
      jResult.add("product_installed", jInstalls);

      if (productName == null) {
        List<ProductDeploy> clientDeployList = deploySrv.findByClientUUID(clientUuid);
        if (clientDeployList != null) {
          for (ProductDeploy deploy : clientDeployList) {
            JsonObject jDeploy = new JsonObject();
            Product p = pSrv.findByName(deploy.getProductName());
            if (p != null) {
              jDeploy.addProperty("product_name", deploy.getProductName());
              jDeploy.addProperty("category", p.getCategory());
              jDeploy.addProperty("version", deploy.getVersion());
              if (deploy.getIsAot() != null) {
                jDeploy.addProperty("is_aot", deploy.getIsAot());
              }
              jDeploys.add(jDeploy);
            }
          }
        }

        /** Add ams_client installed info */
        JsonObject jClient = new JsonObject();
        jClient.addProperty("product_name", "ams_client");
        jClient.addProperty("category", 1);
        jClient.addProperty("version", client.getAmsClientVersion());
        jInstalls.add(jClient);

        List<ProductInstalled> clientInstallList = installSrv.findByClientUUID(clientUuid);
        if (clientInstallList != null) {
          for (ProductInstalled install : clientInstallList) {
            JsonObject jInstall = new JsonObject();
            Product p = pSrv.findByName(install.getProductName());
            if (p != null) {
              jInstall.addProperty("product_name", install.getProductName());
              jInstall.addProperty("category", p.getCategory());
              jInstall.addProperty("version", install.getVersion());
              if (install.getIsAot() != null) {
                jInstall.addProperty("is_aot", install.getIsAot());
              }
              jInstalls.add(jInstall);
            }
          }
        }
      } else {
        ProductDeploy deploy = deploySrv.findByClientUuidAndProductName(clientUuid, productName);
        if (deploy != null) {
          JsonObject jDeploy = new JsonObject();
          Product p = pSrv.findByName(deploy.getProductName());
          if (p != null) {
            jDeploy.addProperty("product_name", deploy.getProductName());
            jDeploy.addProperty("category", p.getCategory());
            jDeploy.addProperty("version", deploy.getVersion());
            if (deploy.getIsAot() != null) {
              jDeploy.addProperty("is_aot", deploy.getIsAot());
            }
            jDeploys.add(jDeploy);
          }
        }

        if (productName.equals("ams_client")) {
          JsonObject jClient = new JsonObject();
          jClient.addProperty("product_name", "ams_client");
          jClient.addProperty("category", 1);
          jClient.addProperty("version", client.getAmsClientVersion());
          jInstalls.add(jClient);
        }
        /*
         * if the client has firmware , then the version of firmware is the fw_version
         * in AmsClient TABLE.
         *
         * added by Ning on 0415.
         */
        else if (client.getFwVersion() != "" | client.getFwVersion() != null) {
          JsonObject jClientFw = new JsonObject();
          jClientFw.addProperty("product_name", productName); // productName is like moto_watch
          jClientFw.addProperty("category", 2); // fw_product
          jClientFw.addProperty("version", client.getFwVersion());
          jInstalls.add(jClientFw);
        } else {
          ProductInstalled install =
              installSrv.findByClientUuidAndProductName(clientUuid, productName);
          if (install != null) {
            JsonObject jInstall = new JsonObject();
            Product p = pSrv.findByName(install.getProductName());
            if (p != null) {
              jInstall.addProperty("product_name", install.getProductName());
              jInstall.addProperty("category", p.getCategory());
              jInstall.addProperty("version", install.getVersion());
              if (install.getIsAot() != null) {
                jInstall.addProperty("is_aot", install.getIsAot());
              }
              jInstalls.add(jInstall);
            }
          }
        }
      }
    }

    if (productDeviceId != null) {
      if (clientUuid != null || productName != null) {
        return new ResponseEntity<String>(
            "Query parameter \"product_device_id\" cannot be used with other parameters.",
            HttpStatus.BAD_REQUEST);
      }

      jResult.addProperty("product_device_id", productDeviceId);
      AmsClient client = null;
      List<ProductDeploy> deviceDeployList = deploySrv.findByDeviceId(productDeviceId);
      List<ProductInstalled> clientInstallList = null;
      ClientDeviceMapping mapping = mappingSrv.findByProductDeviceId(productDeviceId);
      if (mapping != null && mapping.getClientUuid() != null) {
        client = clientSrv.findByClientUUID(mapping.getClientUuid());
        if (client == null) {
          return new ResponseEntity<String>(
              "Cannot find client by product device id: " + productDeviceId + " !",
              HttpStatus.NOT_FOUND);
        }
        clientInstallList = installSrv.findByClientUUID(mapping.getClientUuid());
      }
      jResult.addProperty("device_name", client.getDeviceName());
      JsonArray jDeploys = new JsonArray();
      jResult.add("product_deploy", jDeploys);
      JsonArray jInstalls = new JsonArray();
      jResult.add("product_installed", jInstalls);
      if (deviceDeployList != null) {
        for (ProductDeploy deploy : deviceDeployList) {
          JsonObject jDeploy = new JsonObject();
          Product p = pSrv.findByName(deploy.getProductName());
          if (p != null) {
            jDeploy.addProperty("product_name", deploy.getProductName());
            jDeploy.addProperty("category", p.getCategory());
            jDeploy.addProperty("version", deploy.getVersion());
            if (deploy.getIsAot() != null) {
              jDeploy.addProperty("is_aot", deploy.getIsAot());
            }
            jDeploys.add(jDeploy);

            if (clientInstallList != null) {
              for (ProductInstalled install : clientInstallList) {
                if (install.getProductName().equals(deploy.getProductName())) {
                  JsonObject jInstall = new JsonObject();
                  jInstall.addProperty("product_name", install.getProductName());
                  jInstall.addProperty("category", p.getCategory());
                  jInstall.addProperty("version", install.getVersion());
                  if (install.getIsAot() != null) {
                    jInstall.addProperty("is_aot", install.getIsAot());
                  }
                  jInstalls.add(jInstall);
                  break;
                }
              }
            }
          }
        }
      }
    }

    if (productName != null && clientUuid == null && productDeviceId == null) {
      if (productName.equals("ams_client")) {
        JsonArray jInstalled = new JsonArray();
        jResult.add("installed_clients", jInstalled);
        JsonArray jPendingInstall = new JsonArray();
        jResult.add("pending_install_clients", jPendingInstall);
        JsonArray jPendingUpgrade = new JsonArray();
        jResult.add("pending_upgrade_clients", jPendingUpgrade);
        List<AmsClient> clientList = clientSrv.findAll();
        if (clientList != null) {
          for (AmsClient client : clientList) {
            JsonObject jClient = new JsonObject();
            jClient.addProperty("client_uuid", client.getClientUuid());
            jClient.addProperty("device_name", client.getDeviceName());
            jClient.addProperty("installed_version", client.getAmsClientVersion());
            jInstalled.add(jClient);
          }
        }

        List<ProductDeploy> deployList = deploySrv.findByProductName(productName);
        if (deployList != null) {
          for (ProductDeploy deploy : deployList) {
            if (clientList != null) {
              for (AmsClient client : clientList) {
                if (deploy.getClientUuid().equals(client.getClientUuid())) {
                  if (!deploy.getVersion().equals(client.getAmsClientVersion())) {
                    JsonObject jClient = new JsonObject();
                    jClient.addProperty("client_uuid", deploy.getClientUuid());
                    jClient.addProperty("device_name", client.getDeviceName());
                    jClient.addProperty("pending_version", deploy.getVersion());
                    if (deploy.getIsAot() != null) {
                      jClient.addProperty("is_aot", deploy.getIsAot());
                    }
                    jPendingUpgrade.add(jClient);
                  }
                }
              }
            }
          }
        }
      } else {
        Product p = pSrv.findByName(productName);
        if (p == null) {
          return new ResponseEntity<String>(
              "No such product: " + productName, HttpStatus.BAD_REQUEST);
        }

        List<ProductInstalled> installList = installSrv.findByProductName(productName);
        JsonArray jInstalled = new JsonArray();
        jResult.add("installed_clients", jInstalled);
        JsonArray jPendingInstall = new JsonArray();
        jResult.add("pending_install_clients", jPendingInstall);
        JsonArray jPendingUpgrade = new JsonArray();
        jResult.add("pending_upgrade_clients", jPendingUpgrade);
        if (installList != null) {
          for (ProductInstalled install : installList) {
            JsonObject jClient = new JsonObject();
            jClient.addProperty("client_uuid", install.getClientUuid());
            AmsClient client = clientSrv.findByClientUUID(install.getClientUuid());
            if (client != null) {
              jClient.addProperty("device_name", client.getDeviceName());
            }
            ClientDeviceMapping mapping =
                mappingSrv.findByAmsClientUuidAndProductName(install.getClientUuid(), p.getName());
            if (mapping != null && mapping.getProductDeviceId() != null) {
              jClient.addProperty("product_device_id", mapping.getProductDeviceId());
            }
            jClient.addProperty("installed_version", install.getVersion());
            if (install.getIsAot() != null) {
              jClient.addProperty("is_aot", install.getIsAot());
            }
            jInstalled.add(jClient);
          }
        }

        List<ProductDeploy> deployList = deploySrv.findByProductName(productName);
        if (deployList != null) {
          for (ProductDeploy deploy : deployList) {
            boolean isInstall = false;
            if (installList != null) {
              for (ProductInstalled install : installList) {
                if (deploy.getClientUuid().equals(install.getClientUuid())) {
                  isInstall = true;
                  if (!deploy.getVersion().equals(install.getVersion())) {
                    JsonObject jClient = new JsonObject();
                    jClient.addProperty("client_uuid", deploy.getClientUuid());
                    AmsClient client = clientSrv.findByClientUUID(deploy.getClientUuid());
                    if (client != null) {
                      jClient.addProperty("device_name", client.getDeviceName());
                    }
                    ClientDeviceMapping mapping =
                        mappingSrv.findByAmsClientUuidAndProductName(
                            deploy.getClientUuid(), p.getName());
                    if (mapping != null && mapping.getProductDeviceId() != null) {
                      jClient.addProperty("product_device_id", mapping.getProductDeviceId());
                    }
                    jClient.addProperty("pending_version", deploy.getVersion());
                    if (deploy.getIsAot() != null) {
                      jClient.addProperty("is_aot", deploy.getIsAot());
                    }
                    jPendingUpgrade.add(jClient);
                  }
                }
              }
            }
            if (!isInstall) {
              JsonObject jClient = new JsonObject();
              jClient.addProperty("client_uuid", deploy.getClientUuid());
              AmsClient client = clientSrv.findByClientUUID(deploy.getClientUuid());
              if (client != null) {
                jClient.addProperty("device_name", client.getDeviceName());
              }
              ClientDeviceMapping mapping =
                  mappingSrv.findByAmsClientUuidAndProductName(deploy.getClientUuid(), p.getName());
              if (mapping != null && mapping.getProductDeviceId() != null) {
                jClient.addProperty("product_device_id", mapping.getProductDeviceId());
              }
              jClient.addProperty("pending_version", deploy.getVersion());
              if (deploy.getIsAot() != null) {
                jClient.addProperty("is_aot", deploy.getIsAot());
              }
              jPendingInstall.add(jClient);
            }
          }
        }
      }
    }

    return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.3.2 Query product dependencies on client
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Get client product dependencies
   *
   * <p>RESTful API: GET /ams/v1/product/dependency
   *
   * @param versionwhose uuid: "+clientUuid+
   * @return an instance of ResponseEntity<String> with response code and client product deploy
   *     information in JSON string
   */
  @RequestMapping(
      value = "/ams/v1/product/dependency",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getClientProductDependency(
      @RequestParam(value = "client_uuid", required = false) String clientUuid,
      @RequestParam(value = "product_device_id", required = false) String productDeviceId,
      @RequestParam(value = "product_name", required = false) String productName,
      @RequestParam(value = "version", required = false) String version) {

    List<ProductDependency> pdList = null;
    List<ProductDeploy> deployList = null;

    JsonObject jResult = new JsonObject();
    JsonArray jAlready = new JsonArray();
    JsonArray jNotDeploy = new JsonArray();
    JsonArray jNotRepo = new JsonArray();

    jResult.add("arealdy_in_deploy", jAlready);
    jResult.add("not_in_deploy", jNotDeploy);
    jResult.add("not_in_repo", jNotRepo);

    if (clientUuid == null && productDeviceId == null) {
      return new ResponseEntity<String>(
          "Query parameter \"client_uuid\" or \"product_device_id\" is required.",
          HttpStatus.BAD_REQUEST);
    }

    if (productName == null) {
      return new ResponseEntity<String>(
          "Query parameter \"product_name\" is required.", HttpStatus.BAD_REQUEST);
    }

    Product p = pSrv.findByName(productName);
    if (p == null) {
      return new ResponseEntity<String>("No such product: " + productName, HttpStatus.BAD_REQUEST);
    }

    if (productDeviceId != null) {
      ClientDeviceMapping mapping = mappingSrv.findByProductDeviceId(productDeviceId);
      if (mapping == null) {
        return new ResponseEntity<String>(
            "Device Id: " + productDeviceId + "has not mapped with any client yet.",
            HttpStatus.BAD_REQUEST);
      }

      if (clientUuid != null) {
        if (!mapping.getClientUuid().equals(clientUuid)) {
          return new ResponseEntity<String>(
              "Device Id: "
                  + productDeviceId
                  + " and AMS client UUID "
                  + clientUuid
                  + " are not match.",
              HttpStatus.BAD_REQUEST);
        }
      }

      AmsClient client = clientSrv.findByClientUUID(mapping.getClientUuid());
      ProductInstance instance = null;
      instance =
          findInstanceForClient(
              p.getName(),
              version,
              p.getCategory(),
              client);
      if (instance == null) {
        return new ResponseEntity<String>(
            "Product: " + productName + " doesnot support device: " + productDeviceId,
            HttpStatus.BAD_REQUEST);
      }

      pdList = pdSrv.findByInstanceId(instance.getInstanceId());
      if (pdList == null) {
        return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
      }

      deployList = deploySrv.findByDeviceId(productDeviceId);
    }

    if (clientUuid != null) {

      AmsClient client = clientSrv.findByClientUUID(clientUuid);
      if (client == null) {
        return new ResponseEntity<String>(
            "Client: " + clientUuid + " doesnot exist!", HttpStatus.BAD_REQUEST);
      }
      ProductInstance instance = null;
      if (p.getCategory() == 4) {
        instance =
            pInstanceSrv.findByNameAndVersionAndCpuAndPlatformAndOs(
                p.getName(),
                version,
                null,
                null,
                null);
      } else {
        instance =
            pInstanceSrv.findByNameAndVersionAndCpuAndPlatformAndOs(
                p.getName(),
                version,
                client.getCpu(),
                null,
                client.getOs());
      }

      if (instance == null) {
        return new ResponseEntity<String>(
            "Product: " + productName + " doesnot support client: " + clientUuid,
            HttpStatus.BAD_REQUEST);
      }

      pdList = pdSrv.findByInstanceId(instance.getInstanceId());
      if (pdList == null) {
        return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
      }
      deployList = deploySrv.findByClientUUID(clientUuid);
    }

    /** Prepare for response payload */
    for (ProductDependency pd : pdList) {
      JsonObject jTemp = new JsonObject();
      Product dep = pSrv.findByName(pd.getDependencyName());
      if (dep == null) {
        jTemp.addProperty("product_name", pd.getDependencyName());
        if (pd.getMinVersion() != null) {
          jTemp.addProperty("min_version", pd.getMinVersion());
        }

        jNotRepo.add(jTemp);
      }
      if (deployList == null) {
        jTemp.addProperty("product_name", pd.getDependencyName());
        if (pd.getMinVersion() != null) {
          jTemp.addProperty("min_version", pd.getMinVersion());
        }

        jNotDeploy.add(jTemp);
      } else {
        boolean isDeploy = false;
        for (ProductDeploy deploy : deployList) {
          if (deploy.getProductName().equals(dep.getName())) {
            isDeploy = true;
            jTemp.addProperty("product_name", pd.getDependencyName());
            if (pd.getMinVersion() != null) {
              jTemp.addProperty("min_version", pd.getMinVersion());
            }

            jAlready.add(jTemp);
            break;
          }
        }
        if (isDeploy == false) {
          jTemp.addProperty("product_name", pd.getDependencyName());
          if (pd.getMinVersion() != null) {
            jTemp.addProperty("min_version", pd.getMinVersion());
          }

          jNotDeploy.add(jTemp);
        }
      }
    }

    return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.3.3 Query depended products in client deploy information
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Get products
   *
   * <p>RESTful API: GET /ams/v1/product/depended
   *
   * @return an instance of ResponseEntity<String> with response code and client product deploy
   *     information in JSON string
   */
  @RequestMapping(
      value = "/ams/v1/product/depended",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getDependedProductOnClient(
      @RequestParam(value = "client_uuid", required = false) String clientUuid,
      @RequestParam(value = "product_device_id", required = false) String productDeviceId,
      @RequestParam(value = "product_name", required = false) String productName) {

    JsonObject jResult = new JsonObject();
    JsonArray jDependedArray = new JsonArray();
    jResult.add("depended_list", jDependedArray);

    if (clientUuid == null && productDeviceId == null) {
      return new ResponseEntity<String>(
          "Query parameter \"client_uuid\" or \"product_device_id\" is required.",
          HttpStatus.BAD_REQUEST);
    }

    if (productName == null) {
      return new ResponseEntity<String>(
          "Query parameter \"product_name\" and \"version\" are required.", HttpStatus.BAD_REQUEST);
    }

    Product p = pSrv.findByName(productName);
    if (p == null) {
      return new ResponseEntity<String>("No such product: " + productName, HttpStatus.BAD_REQUEST);
    }

    List<ProductDependency> pdList = pdSrv.findByDependName(productName);
    if (pdList == null) {
      return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
    }

    List<ProductDeploy> deployList = null;
    AmsClient client = null;

    if (productDeviceId != null) {
      ClientDeviceMapping mapping = mappingSrv.findByProductDeviceId(productDeviceId);
      if (mapping == null) {
        return new ResponseEntity<String>(
            "Device Id: " + productDeviceId + "has not mapped with any client yet.",
            HttpStatus.BAD_REQUEST);
      }

      if (clientUuid != null) {
        if (!mapping.getClientUuid().equals(clientUuid)) {
          return new ResponseEntity<String>(
              "Device Id: "
                  + productDeviceId
                  + " and AMS client UUID "
                  + clientUuid
                  + " are not match.",
              HttpStatus.BAD_REQUEST);
        }
      }

      client = clientSrv.findByClientUUID(mapping.getClientUuid());
      deployList = deploySrv.findByDeviceId(productDeviceId);
    }

    if (clientUuid != null) {

      client = clientSrv.findByClientUUID(clientUuid);
      if (client == null) {
        return new ResponseEntity<String>(
            "Client: " + clientUuid + " doesnot exist!", HttpStatus.BAD_REQUEST);
      }
      deployList = deploySrv.findByClientUUID(clientUuid);
    }

    /** Prepare for response payload */
    if (deployList != null) {
      for (ProductDeploy deploy : deployList) {

        Product pTemp = pSrv.findByName(deploy.getProductName());
        if (pTemp == null) {
          continue;
        }
        ProductInstance instance = null;
        instance =
            findInstanceForClient(
                pTemp.getName(), deploy.getVersion(), pTemp.getCategory(), client);
        if (instance != null) {
          for (ProductDependency pd : pdList) {
            if (instance.getInstanceId().equals(pd.getInstanceId())) {
              Product depended = pSrv.findByName(instance.getProductName());
              if (depended != null) {
                JsonObject jDepended = new JsonObject();
                jDepended.addProperty("product_name", depended.getName());
                jDepended.addProperty("version", instance.getVersion());

                jDependedArray.add(jDepended);
              }
            }
          }
        }
      }
    }

    return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.3.4 Add product for client
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Add a product for a client
   *
   * <p>RESTful API: POST /ams/v1/product/deploy
   *
   * @param info the POST payload data
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/product/deploy",
      produces = "application/json",
      method = RequestMethod.POST)
  public ResponseEntity<String> addProductForClient(@RequestBody AddProductForClientInfo info) {

    if (info.getProductName() == null) {
      return new ResponseEntity<String>(
          "Must specify product_name for deleting/posting a product!", HttpStatus.BAD_REQUEST);
    }

    if (info.getClientUuid() != null && info.getDeviceId() != null) {
      return new ResponseEntity<String>(
          "\"client_uuid\" cannot be used together with \"product_device_id\".",
          HttpStatus.BAD_REQUEST);
    }

    Product p = pSrv.findByName(info.getProductName());
    if (p == null) {
      return new ResponseEntity<String>(
          "No such product: " + info.getProductName(), HttpStatus.BAD_REQUEST);
    }

    if (info.getClientUuid() != null) {
      return deployProductWithClientUuid(info, p);
    }

    if (info.getDeviceId() != null) {
      return deployProductWithDeviceId(info, p);
    }

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.3.5 Delete product for client
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Delete a product from a client
   *
   * <p>RESTful API: DELETE /ams/v1/product/deploy
   *
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/product/deploy",
      produces = "application/json",
      method = RequestMethod.DELETE)
  public ResponseEntity<String> delProductFromClient(
      @RequestParam(value = "client_uuid", required = false) String clientUuid,
      @RequestParam(value = "product_name", required = false) String productName,
      @RequestParam(value = "product_device_id", required = false) String productDeviceId) {
    if (productName == null) {
      return new ResponseEntity<String>(
          "Must specify product_name for deleting a product!", HttpStatus.BAD_REQUEST);
    }

    if (clientUuid == null && productDeviceId == null) {
      return new ResponseEntity<String>(
          "Query parameter \"client_uuid\" or \"product_device_id\" is required.",
          HttpStatus.BAD_REQUEST);
    }

    if (clientUuid != null && productDeviceId != null) {
      return new ResponseEntity<String>(
          "Query parameter \"client_uuid\" cannot be used together with and \"product_device_id\".",
          HttpStatus.BAD_REQUEST);
    }

    Product p = pSrv.findByName(productName);
    if (p == null) {
      return new ResponseEntity<String>("No such product: " + productName, HttpStatus.BAD_REQUEST);
    }

    if (clientUuid != null) {
      AmsClient client = clientSrv.findByClientUUID(clientUuid);
      if (client == null) {
        return new ResponseEntity<String>("No such client: " + clientUuid, HttpStatus.BAD_REQUEST);
      }

      if (client.getProductLock() != null && client.getProductLock() == true) {
        return new ResponseEntity<String>(
            "The client is lock for new product deployment!", HttpStatus.BAD_REQUEST);
      }

      deploySrv.removeByClientUuidAndProductName(clientUuid, p.getName());

      /** Log to MySQL */
      logSrv.LogToMysql(
          "Delete",
          "Software Deploy",
          "Delete deployed software(" + p.getName() + ") of client",
          clientUuid);

      /** Delete all old product changes of this client */
      changeSrv.removeByClientUuid(clientUuid);

      /** Create a AmsTask to calculate product changes of this client */
      AmsTask task = new AmsTask();
      task.setTaskType(AmsTaskType.CALCULATE_PRODUCT_CHANGES);
      task.setTaskCreateTime(new Date());
      JsonObject jTaskProperty = new JsonObject();
      jTaskProperty.addProperty("client_uuid", clientUuid);
      task.setTaskProperties(jTaskProperty.toString());
      taskSrv.save(task);
    }

    if (productDeviceId != null) {
      deploySrv.removeByDeviceIdAndProductName(productDeviceId, p.getName());

      ClientDeviceMapping mapping = mappingSrv.findByProductDeviceId(productDeviceId);
      if (mapping != null) {
        /** Delete all old product changes of this client */
        changeSrv.removeByClientUuid(mapping.getClientUuid());
        /** Create a AmsTask to calculate product changes of this client */
        AmsTask task = new AmsTask();
        task.setTaskType(AmsTaskType.CALCULATE_PRODUCT_CHANGES);
        task.setTaskCreateTime(new Date());
        JsonObject jTaskProperty = new JsonObject();
        jTaskProperty.addProperty("client_uuid", mapping.getClientUuid());
        task.setTaskProperties(jTaskProperty.toString());
        taskSrv.save(task);
      }
    }

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.3.6 Query product download history
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Get product download history
   *
   * <p>RESTful API: GET /ams/v1/product/history
   *
   * @return an instance of ResponseEntity<String> with response code and client product download
   *     history in JSON string
   */
  @RequestMapping(
      value = "/ams/v1/product/history",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getClientProductDownloadHistory(
      @RequestParam(value = "client_uuid", required = false) String clientUuid,
      @RequestParam(value = "product_name", required = false) String productName) {

    JsonArray jResult = new JsonArray();

    if (clientUuid == null || productName == null) {
      return new ResponseEntity<String>(
          "Query parameter \"client_uuid\" and \"productName\" is required.",
          HttpStatus.BAD_REQUEST);
    }

    List<ProductDownloadHistory> historyList =
        historySrv.findByClientUuidAndProductName(clientUuid, productName);

    if (historyList != null) {
      for (ProductDownloadHistory history : historyList) {
        JsonObject j = new JsonObject();
        j.addProperty("download_time", String.valueOf(history.getDownloadTime().getTime()));
        if (history.getFromId() != null) {
          ProductInstance fromInstance = pInstanceSrv.findById(history.getFromId());
          if (fromInstance == null) {
            continue;
          }
          j.addProperty("from_version", fromInstance.getVersion());
        }
        ProductInstance toInstance = pInstanceSrv.findById(history.getToId());
        if (toInstance == null) {
          continue;
        }
        j.addProperty("to_version", toInstance.getVersion());
        if (history.isAot() != null) {
          j.addProperty("is_aot", history.isAot().booleanValue());
        }
        jResult.add(j);
      }
    }

    return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
  }

  @RequestMapping(
      value = "/ams/v1/product/change",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getClientProductChange(
      @RequestParam(value = "client_uuid", required = false) String clientUuid,
      @RequestParam(value = "product_name", required = false) String productName) {

    JsonArray jResult = new JsonArray();
    List<ProductChanges> changeList = null;
    if (clientUuid == null && productName == null) {
      changeList = changeSrv.findAll();
    } else if (clientUuid != null && productName == null) {
      changeList = changeSrv.findByClientUUID(clientUuid);
    } else if (clientUuid != null && productName != null) {
      ProductChanges change = changeSrv.findByClientUuidAndProductName(clientUuid, productName);
      if (change != null) {
        changeList = new ArrayList<ProductChanges>();
        changeList.add(change);
      }
    } else {
      return new ResponseEntity<String>(
          "If query parameter \"product_name\" is not null, then \"client_uuid\" cannot be null as well.",
          HttpStatus.BAD_REQUEST);
    }

    if (changeList != null) {
      for (ProductChanges change : changeList) {
        AmsClient client = clientSrv.findByClientUUID(change.getClientUuid());
        if (client != null) {
          JsonObject j = new JsonObject();
          j.addProperty("client_uuid", change.getClientUuid());
          j.addProperty("device_name", client.getDeviceName());
          if (change.getDownloadId() != null) {
            j.addProperty("download_id", change.getDownloadId());
          }
          j.addProperty("product_name", change.getProductName());
          if (change.getEnableTime() != null) {
            j.addProperty("enable_time", change.getEnableTime().toString());
          }
          jResult.add(j);
        }
      }
    }

    return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
  }

  private ProductInstance findInstanceForClient(
      String productName, String version, int category, AmsClient client) {

    List<ProductInstance> tempList = null;
    if (productName == null || version == null) {
      return null;
    }
    if (category > 5 || category < 1) {
      return null;
    }
    if ((category != 4 || category != 5) && client == null) {
      return null;
    }

    /** the product is fw_product */
    if (category == 2) {
      ProductInstance instance =
          pInstanceSrv.findByNameAndVersionAndCpuAndPlatformAndOs(
              productName, version, client.getCpu(), null, null);
      return instance;
    }

    /** the product is imrt app */
    if (category == 4) {
      ProductInstance instance =
          pInstanceSrv.findByNameAndVersionAndCpuAndPlatformAndOs(
              productName, version, null, null, null);
      return instance;
    }

    /** the product is fw_app_wasm */
    if (category == 5) {
      ProductInstance instance =
          pInstanceSrv.findByNameAndVersionAndCpuAndPlatformAndOs(
              productName, version, null, null, null);
      return instance;
    }

    tempList =
        pInstanceSrv.findByNameAndVersionAndCpuAndPlatformAndOsAndSystemAndBits(
            productName,
            version,
            client.getCpu(),
            client.getPlatform(),
            client.getOs(),
            client.getSystem(),
            client.getBits());
    if (tempList != null) {
      for (ProductInstance temp : tempList) {
        if (checkVerComp(client.getOsVer(), temp.getOsMin()) == true) {
          if (checkVerComp(client.getSysVer(), temp.getSysMin()) == true) {
            return temp;
          }
        }
      }
    }

    if (client.getBits() != null && client.getBits().equals("64bit")) {
      tempList =
          pInstanceSrv.findByNameAndVersionAndCpuAndPlatformAndOsAndSystemAndBits(
              productName,
              version,
              client.getCpu(),
              client.getPlatform(),
              client.getOs(),
              client.getSystem(),
              "32bit");
      if (tempList != null) {
        for (ProductInstance temp : tempList) {
          if (checkVerComp(client.getOsVer(), temp.getOsMin()) == true) {
            if (checkVerComp(client.getSysVer(), temp.getSysMin()) == true) {
              return temp;
            }
          }
        }
      }
    }

    tempList =
        pInstanceSrv.findByNameAndVersionAndCpuAndPlatformAndOsAndBits(
            productName,
            version,
            client.getCpu(),
            client.getPlatform(),
            client.getOs(),
            client.getBits());
    if (tempList != null) {
      for (ProductInstance temp : tempList) {
        if (checkVerComp(client.getOsVer(), temp.getOsMin()) == true) {
          if (checkVerComp(client.getSysVer(), temp.getSysMin()) == true) {
            return temp;
          }
        }
      }
    }

    if (client.getBits() != null && client.getBits().equals("64bit")) {
      tempList =
          pInstanceSrv.findByNameAndVersionAndCpuAndPlatformAndOsAndBits(
              productName, version, client.getCpu(), client.getPlatform(), client.getOs(), "32bit");
      if (tempList != null) {
        for (ProductInstance temp : tempList) {
          if (checkVerComp(client.getOsVer(), temp.getOsMin()) == true) {
            if (checkVerComp(client.getSysVer(), temp.getSysMin()) == true) {
              return temp;
            }
          }
        }
      }
    }

    tempList = pInstanceSrv.findByNameAndVersionAndCpu(productName, version, "ANY");
    if (tempList != null) {
      return tempList.get(0);
    }

    tempList = pInstanceSrv.findByNameAndVersionAndCpu(productName, version, "Any");
    if (tempList != null) {
      return tempList.get(0);
    }

    tempList = pInstanceSrv.findByNameAndVersionAndCpu(productName, version, "any");
    if (tempList != null) {
      return tempList.get(0);
    }

    return null;
  }

  private boolean checkVerComp(String version, String minVersion) {

    if (version == null || minVersion == null) {
      return true;
    }

    if (version.equals(minVersion)) {
      return true;
    }

    String[] verArray = new String[3];
    String[] minVerArray = new String[3];

    String[] tempVerArray = version.split("\\.");
    String[] tempMinVerArray = minVersion.split("\\.");

    if (tempVerArray.length == 2) {
      System.arraycopy(tempVerArray, 0, verArray, 0, 2);
      verArray[2] = "0";
    } else {
      System.arraycopy(tempVerArray, 0, verArray, 0, 3);
    }
    if (tempMinVerArray.length == 2) {
      System.arraycopy(tempMinVerArray, 0, minVerArray, 0, 2);
      minVerArray[2] = "0";
    } else {
      System.arraycopy(tempMinVerArray, 0, minVerArray, 0, 3);
    }

    for (int i = 0; i < 3; i++) {
      if (Integer.parseInt(verArray[i]) > Integer.parseInt(minVerArray[i])) {
        return true;
      } else if (Integer.parseInt(verArray[i]) < Integer.parseInt(minVerArray[i])) {
        return false;
      }
    }

    return true;
  }

  private ResponseEntity<String> deployProductWithClientUuid(
      AddProductForClientInfo info, Product p) {
    AmsClient client = clientSrv.findByClientUUID(info.getClientUuid());
    if (client == null) {
      return new ResponseEntity<String>(
          "No such client: " + info.getClientUuid(), HttpStatus.BAD_REQUEST);
    }

    /** make sure AMS has the product instance of the adding product for the client */
    ProductInstance instance =
        findInstanceForClient(
            p.getName(),
            info.getVersion(),
            p.getCategory(),
            client);
    if (instance == null) {
      return new ResponseEntity<String>(
          "AMS has no suitable product version package for this client", HttpStatus.NOT_FOUND);
    }

    /** FW product name must be comply with client product name */
    if (p.getCategory() == 2 && !p.getName().equals(client.getDeviceType())) {
      return new ResponseEntity<String>(
          "Cannot deploy FW to different product!", HttpStatus.BAD_REQUEST);
    }

    /** If the product is fw_app_wasm and aot_enable is true, check if the aot can be done */
    if (p.getCategory() == 5) {
      if (info.getAotEnable() != null && info.getAotEnable() == true) {
        if (instance.getAotEnable() == null || instance.getAotEnable() != true) {
          return new ResponseEntity<String>(
              "Deployed product version has no AOT support!", HttpStatus.BAD_REQUEST);
        }
        if (client.getAotEnable() != true) {
          return new ResponseEntity<String>("Client does not support AOT!", HttpStatus.BAD_REQUEST);
        }
        if (!AotToolUtils.isPlatformSupported(client.getCpu())) {
          return new ResponseEntity<String>(
              "AOT tool does not support platform: " + client.getCpu() + " !",
              HttpStatus.BAD_REQUEST);
        }
      } else {
        /** If the product is fw_app_wasm and aot_enable is false, check if wasm can be deployed */
        if (client.getWasmEnable() == null || client.getWasmEnable() != true) {
          return new ResponseEntity<String>(
              "Client does not support WASM bytecode execution!", HttpStatus.BAD_REQUEST);
        }
        if (client.getWasmVersion() == null
            || client.getWasmVersion() < instance.getMinWasmVersion()) {
          return new ResponseEntity<String>(
              "The product's wasm version does not match the client !", HttpStatus.BAD_REQUEST);
        }
      }
    }

    /** delete the old deploy if it exists */
    deploySrv.removeByClientUuidAndProductName(info.getClientUuid(), p.getName());
    logSrv.LogToMysql(
        "Delete",
        "Software Deploy",
        "Delete deployed software(" + p.getName() + ") of client because of deploying a new one",
        info.getClientUuid());

    /** add new deploy */
    ProductDeploy deploy = new ProductDeploy();
    deploy.setClientUuid(info.getClientUuid());
    deploy.setProductName(p.getName());
    deploy.setVersion(info.getVersion());
    if (info.getAotEnable() != null) {
      deploy.setIsAot(info.getAotEnable());
    }

    deploySrv.save(deploy);

    /** Log to MySQL */
    logSrv.LogToMysql(
        "Add",
        "Software Deploy",
        "Deploy software( name: "
            + p.getName()
            + ", version:"
            + info.getVersion()
            + " ) for client",
        client.getClientUuid());

    /** Delete all old product changes of this client */
    changeSrv.removeByClientUuid(info.getClientUuid());

    /** Create a AmsTask to calculate product changes of this client */
    AmsTask task = new AmsTask();
    task.setTaskType(AmsTaskType.CALCULATE_PRODUCT_CHANGES);
    task.setTaskCreateTime(new Date());
    JsonObject jTaskProperty = new JsonObject();
    jTaskProperty.addProperty("client_uuid", info.getClientUuid());
    task.setTaskProperties(jTaskProperty.toString());
    taskSrv.save(task);

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  private ResponseEntity<String> deployProductWithDeviceId(
      AddProductForClientInfo info, Product p) {

    ClientDeviceMapping mapping = mappingSrv.findByProductDeviceId(info.getDeviceId());
    if (mapping != null) {
      AmsClient client = clientSrv.findByClientUUID(mapping.getClientUuid());
      if (client == null) {
        return new ResponseEntity<String>(
            "No such client: " + info.getClientUuid(), HttpStatus.BAD_REQUEST);
      }

      /** make sure AMS has the product instance of the adding product for the client */
      ProductInstance instance =
          findInstanceForClient(
              p.getName(),
              info.getVersion(),
              p.getCategory(),
              client);
      if (instance == null) {
        return new ResponseEntity<String>(
            "AMS has no suitable product version package for this client", HttpStatus.BAD_REQUEST);
      }

      /** If the product is fw_app_wasm and aot_enable is true, check if the aot can be done */
      if (p.getCategory() == 5 && info.getAotEnable() == true) {
        if (instance.getAotEnable() == null || instance.getAotEnable() != true) {
          return new ResponseEntity<String>(
              "Deployed product version has no AOT support!", HttpStatus.BAD_REQUEST);
        }
        if (client.getAotEnable() != true) {
          return new ResponseEntity<String>("Client does not support AOT!", HttpStatus.BAD_REQUEST);
        }

        if (!AotToolUtils.isPlatformSupported(client.getCpu())) {
          return new ResponseEntity<String>(
              "AOT tool does not support platform: " + client.getCpu() + " !",
              HttpStatus.BAD_REQUEST);
        }
      }

      /** delete the old deploy if it exists */
      deploySrv.removeByClientUuidAndProductName(mapping.getClientUuid(), p.getName());

      /** add new deploy */
      ProductDeploy deploy = new ProductDeploy();
      deploy.setClientUuid(mapping.getClientUuid());
      deploy.setProductDeviceId(info.getDeviceId());
      deploy.setProductName(p.getName());
      deploy.setVersion(info.getVersion());
      if (info.getAotEnable() != null) {
        deploy.setIsAot(info.getAotEnable());
      }
      deploySrv.save(deploy);

      /** Delete all old product changes of this client */
      changeSrv.removeByClientUuid(info.getClientUuid());

      /** Create a AmsTask to calculate product changes of this client */
      AmsTask task = new AmsTask();
      task.setTaskType(AmsTaskType.CALCULATE_PRODUCT_CHANGES);
      task.setTaskCreateTime(new Date());
      JsonObject jTaskProperty = new JsonObject();
      jTaskProperty.addProperty("client_uuid", mapping.getClientUuid());
      task.setTaskProperties(jTaskProperty.toString());
      taskSrv.save(task);
    } else {
      /** delete the old deploy if it exsits */
      deploySrv.removeByDeviceIdAndProductName(info.getDeviceId(), p.getName());

      /** add new deploy */
      ProductDeploy deploy = new ProductDeploy();
      deploy.setProductDeviceId(info.getDeviceId());
      deploy.setProductName(p.getName());
      deploy.setVersion(info.getVersion());
      if (info.getAotEnable() != null) {
        deploy.setIsAot(info.getAotEnable());
      }

      deploySrv.save(deploy);
    }

    return new ResponseEntity<String>(HttpStatus.OK);
  }
}

/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intel.iot.ams.entity.AmsTask;
import com.intel.iot.ams.task.AmsTaskType;
import com.intel.iot.ams.api.requestbody.UpdateAmsClientInfo;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.entity.ClientDeviceMapping;
import com.intel.iot.ams.entity.Product;
import com.intel.iot.ams.security.AuthUtils;
import com.intel.iot.ams.service.*;
import com.openiot.cloud.base.common.model.TokenContent;
import com.openiot.cloud.base.help.ConstDef;
import com.openiot.cloud.base.mongo.model.help.UserRole;
import com.openiot.cloud.base.service.model.ProjectDTO;
import com.openiot.cloud.sdk.service.IConnectRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RESTAPIs implementation of APIs document Chapter 2.2 Client management
 *
 * @author Zhang, Yi Y <yi.y.zhang@intel.com>
 */
@Slf4j
@RestController
@RequestMapping("/ams_user_cloud")
public class ClientMgrAPIs {

  @Autowired private AmsTaskService AstSrv;

  @Autowired private AmsClientService clientSrv;

  @Autowired private ClientDeviceMappingService mappingSrv;

  @Autowired private ProductService pSrv;

  @Autowired private ProductInstalledService piSrv;

  @Autowired private ProductDeployService pdSrv;

  @Autowired private ProductChangesService pcSrv;

  @Autowired private ProductDownloadHistoryService pdhSrv;

  @Autowired private AmsTaskService taskSrv;

  @Autowired private LogService logSrv;

  private static final int DFLTOFFSET = 0; // The first page, which starts
  // from 0
  private static final int DFLTLIMIT = 10; // Default size for each page.
  private static final int MAXLIMIT = 100;

  // ----------------------------------------------------------------
  //
  // RESTful APIs
  //
  // ----------------------------------------------------------------

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.2.1 Query client information
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Get the AMS client information
   *
   * <p>RESTful API: GET /ams/v1/ams_client
   *
   * @param offset the offset of the search
   * @param limit the limitation for one search
   * @return an instance of ResponseEntity<String> with response code and AMS client information
   *     data in a JSON format string
   */
  // @formatter:off
  @RequestMapping(
      value = "/ams/v1/ams_client",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getAmsClientInformation(
      @RequestParam(value = "client_uuid", required = false) String clientUuid,
      @RequestParam(value = "product_name", required = false) String productName,
      @RequestParam(value = "product_device_id", required = false) String productDeviceId,
      @RequestParam(value = "project", required = false) String projectIdParam,
      @RequestParam(value = "fuzz_str", required = false) String fuzzStr,
      @RequestParam(value = "offset", required = false) Integer offset,
      @RequestParam(value = "limit", required = false) Integer limit) {
    // @formatter:on
    log.debug(
        "getAmsClientInformation client_uuid={}, product_name={}, product_device_id={}, fuzz_str={}",
        clientUuid,
        productName,
        productDeviceId,
        fuzzStr);

    List<ClientDeviceMapping> mappingList = new ArrayList<ClientDeviceMapping>();
    List<AmsClient> clientList = new ArrayList<AmsClient>();
    JsonObject jResult = new JsonObject();
    JsonArray jClients = new JsonArray();
    jResult.add("client_list", jClients);

    // for special case project=null to get all ams client without project assigned
    if (projectIdParam != null && !projectIdParam.equals("null")) {
      return new ResponseEntity<String>(
          "parameter project accept 'null' only!", HttpStatus.BAD_REQUEST);
    }

    // normally, we are using the project id from the token, only overwrite it when
    // specific "null"
    String projectId = AuthUtils.getProjectIdFromContext();
    TokenContent token = AuthUtils.getTokenContextFromContext();

    if (projectIdParam != null && projectIdParam.equals("null")) {
      projectId = null;
    }

    // at least, there is a query string "fuzz_str="
    if (clientUuid == null && productDeviceId == null && productName == null && fuzzStr == null) {
      log.warn(
          "At least, should give \"fuzz_str=\" or one of client_uuid, product_name or product_device_id");
      return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    // can not be together
    if (fuzzStr != null && (clientUuid != null || productDeviceId != null || productName != null)) {
      log.warn(
          "Query parameter \"fuzz_str\" cannot be used together with \"client_uuid\", \"product_device_id\" and \"product_name\".");
      return new ResponseEntity<String>(
          "Query parameter \"fuzz_str\" cannot be used together with \"client_uuid\", \"product_device_id\" and \"product_name\".",
          HttpStatus.BAD_REQUEST);
    }

    if (offset != null) {
      if (offset < 0) {
        log.warn("offset must be equal to or larger than 0.");
        return new ResponseEntity<String>(
            "offset must be equal to or larger than 0.", HttpStatus.BAD_REQUEST);
      }
    } else {
      offset = DFLTOFFSET;
    }

    if (limit != null) {
      if (limit > MAXLIMIT) {
        log.warn("limit is too large");
        return new ResponseEntity<String>("limit is too large", HttpStatus.BAD_REQUEST);
      }
    } else {
      limit = DFLTLIMIT;
    }

    if (clientUuid != null || productName != null || productDeviceId != null) {
      if (clientUuid != null) {
        if (productDeviceId != null) {
          return new ResponseEntity<String>(
              "Query parameter \"client_uuid\" cannot be used together with \"product_device_id\".",
              HttpStatus.BAD_REQUEST);
        }
        if (productName != null) {
          Product p = pSrv.findByName(productName);
          if (p == null) {
            return new ResponseEntity<String>(
                "No such product: " + productName, HttpStatus.BAD_REQUEST);
          }
          AmsClient client = clientSrv.findByClientUUID(clientUuid, projectId);
          if (client == null) {
            return new ResponseEntity<String>("Can't find the client.", HttpStatus.NOT_FOUND);
          }
          if (client != null) {
            ClientDeviceMapping mapping =
                mappingSrv.findByAmsClientUuidAndProductName(clientUuid, p.getName());
            if (mapping != null) {
              clientList.add(client);
            }
          }
        } else {
          AmsClient client = clientSrv.findByClientUUID(clientUuid);
          if (client == null) {
            return new ResponseEntity<String>("Can't find the client.", HttpStatus.NOT_FOUND);
          } else {
            if (userHasRight(client, projectId, token)) {
              clientList.add(client);
            } else {
              return new ResponseEntity<String>("Invalid request.", HttpStatus.BAD_REQUEST);
            }
          }
        }
      } else {
        if (productName != null && productDeviceId == null) {
          Product p = pSrv.findByName(productName);
          if (p == null) {
            return new ResponseEntity<String>(
                "No such product: " + productName, HttpStatus.BAD_REQUEST);
          }
          mappingList = mappingSrv.findByFilter(p.getName(), offset, limit);
          if (mappingList != null) {
            for (ClientDeviceMapping mapping : mappingList) {
              AmsClient client = clientSrv.findByClientUUID(mapping.getClientUuid(), projectId);
              if (client != null) {
                clientList.add(client);
              }
            }
          }
        }

        if (productDeviceId != null) {
          ClientDeviceMapping mapping = mappingSrv.findByProductDeviceId(productDeviceId);
          if (mapping == null) {
            return new ResponseEntity<String>(
                "Device ID: " + productDeviceId + " does not map to any AMS client.",
                HttpStatus.BAD_REQUEST);
          }

          Product p = pSrv.findByName(mapping.getProductName());
          if (p == null) {
            return new ResponseEntity<String>(
                "Device ID: " + productDeviceId + " is not used anymore.", HttpStatus.BAD_REQUEST);
          }

          if (productName != null && !p.getName().equals(productName)) {
            return new ResponseEntity<String>(
                "Device ID: "
                    + productDeviceId
                    + " does not belong to Product: "
                    + productName
                    + "!",
                HttpStatus.BAD_REQUEST);
          }

          AmsClient client = clientSrv.findByClientUUID(mapping.getClientUuid(), projectId);
          if (client != null) {
            clientList.add(client);
          }
        }
      }
    }

    if (fuzzStr != null) {
      log.debug("fuzzStr={}, size={}", fuzzStr, fuzzStr.length());
      clientList = clientSrv.fuzzySearch(fuzzStr, offset, limit, projectId);
    }

    if (clientList.isEmpty()) {
      return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
    }

    if (clientList != null) {
      for (AmsClient client : clientList) {
        JsonObject jClient = new JsonObject();
        jClient.addProperty("ams_client_uuid", client.getClientUuid());
        jClient.addProperty("ams_version", client.getAmsClientVersion());
        if (client.getDeviceName() != null) {
          jClient.addProperty("device_name", client.getDeviceName());
        }
        if (productDeviceId != null) {
          jClient.addProperty("product_device_id", productDeviceId);
        } else {
          if (productName != null) {
            ClientDeviceMapping mapping =
                mappingSrv.findByAmsClientUuidAndProductName(clientUuid, productName);
            if (mapping.getProductDeviceId() != null) {
              jClient.addProperty("product_device_id", mapping.getProductDeviceId());
            }
          }
        }
        if (client.getTemplateName() != null) {
          jClient.addProperty("template_name", client.getTemplateName());
        }

        jClient.addProperty("cpu", client.getCpu());
        jClient.addProperty("os", client.getOs());
        if (client.getProjectId() != null) {
          jClient.addProperty("project", client.getProjectId());
        }
        if (client.getPlatform() != null) {
          jClient.addProperty("platform", client.getPlatform());
        }
        if (client.getOsVer() != null) {
          jClient.addProperty("os_ver", client.getOsVer());
        }
        if (client.getSystem() != null) {
          jClient.addProperty("system", client.getSystem());
        }
        if (client.getSysVer() != null) {
          jClient.addProperty("sys_ver", client.getSysVer());
        }
        if (client.getBits() != null) {
          jClient.addProperty("bits", client.getBits());
        }

        jClient.addProperty("hardware_serial", client.getSerial());
        if (client.getDescription() != null) {
          jClient.addProperty("description", client.getDescription());
        }
        jClient.addProperty("product_num", piSrv.getProductNumByClientUuid(client.getClientUuid()));
        jClient.addProperty(
            "provisioning_time", String.valueOf(client.getProvisionTime().getTime()));
        if (client.getLastProductUpdateTime() != null) {
          jClient.addProperty(
              "last_product_update_time",
              String.valueOf(client.getLastProductUpdateTime().getTime()));
        }
        if (client.getLastConfigUpdateTime() != null) {
          jClient.addProperty(
              "last_config_update_time",
              String.valueOf(client.getLastConfigUpdateTime().getTime()));
        }
        if (client.getLastConnectionTime() != null) {
          jClient.addProperty(
              "last_connection_time", String.valueOf(client.getLastConnectionTime().getTime()));
        }
        if (client.getFwVersion() != null) {
          jClient.addProperty("fw_version", client.getFwVersion());
        }
        if (client.getDeviceType() != null) {
          jClient.addProperty("device_type", client.getDeviceType());
        }
        if (client.getProductLock() != null) {
          jClient.addProperty("product_lock", client.getProductLock());
        }
        if (client.getAotEnable() != null) {
          jClient.addProperty("aot_enable", client.getAotEnable());
        }
        if (client.getWasmEnable() != null) {
          jClient.addProperty("wasm_enable", client.getWasmEnable());
        }
        if (client.getWasmVersion() != null) {
          jClient.addProperty("wasm_version", client.getWasmVersion());
        }

        jClients.add(jClient);
      }
    }

    return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.2.2 Update client information
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Update the AMS client information
   *
   * <p>RESTful API: POST /ams/v1/ams_client
   *
   * @return an instance of ResponseEntity<String> with response code and AMS client information
   *     data in a JSON format string
   */
  @RequestMapping(
      value = "/ams/v1/ams_client",
      produces = "application/json",
      method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> updateAmsClientInformation(@RequestBody UpdateAmsClientInfo info) {

    String projectId = AuthUtils.getProjectIdFromContext();
    // post request body does not include product_lock status update
    if (info.getProductLock() == null) {
      if (info == null
          || info.getClientUuid() == null
          || (info.getDescription() == null && info.getDeviceName() == null)
          || (info.getDescription() == "" && info.getDeviceName() == "")) {
        return new ResponseEntity<String>(
            "POST payload format is not correct", HttpStatus.BAD_REQUEST);
      }
    }

    AmsClient client = clientSrv.findByClientUUID(info.getClientUuid(), projectId);
    if (client == null) {
      return new ResponseEntity<String>(
          "No such client: " + info.getClientUuid(), HttpStatus.BAD_REQUEST);
    }

    StringBuilder details = new StringBuilder("Update client information.");
    if (info.getDescription() != null) {
      client.setDescription(info.getDescription());
      details.append("description: " + info.getDescription() + "  ");
    }

    /** TODO: should device name unique? */
    if (info.getDeviceName() != null) {
      client.setDeviceName(info.getDeviceName());
      details.append("device name: " + info.getDeviceName() + "   ");
    }

    if (info.getProductLock() != null) {
      client.setProductLock(info.getProductLock());
      /** if the product lock switch: on (true) --> off (false)
       *  should create ams task to calculate product change
       */
      if (!info.getProductLock()){
        /** Create a AmsTask to calculate product changes of this client */
        AmsTask task = new AmsTask();
        task.setTaskType(AmsTaskType.CALCULATE_PRODUCT_CHANGES);
        task.setTaskCreateTime(new Date());
        JsonObject jTaskProperty = new JsonObject();
        jTaskProperty.addProperty("client_uuid", client.getClientUuid());
        task.setTaskProperties(jTaskProperty.toString());
        taskSrv.save(task);
      }
    }

    clientSrv.update(client);
    /** Log to MySQL */
    logSrv.LogToMysql("Update", "AmsClient", details.toString(), client.getClientUuid());

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  @RequestMapping(
      value = "/ams/v1/ams_client",
      produces = "application/json",
      method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<String> createClient(@RequestBody AmsClient newClient) {
    AmsClient duplication = clientSrv.findByClientUUID(newClient.getClientUuid());
    if (duplication != null) {
      return new ResponseEntity<String>(
          "such client exists: " + newClient.getClientUuid(), HttpStatus.BAD_REQUEST);
    }

    clientSrv.save(newClient);
    /** Log to MySQL */
    logSrv.LogToMysql(
        "Add",
        "AmsClient",
        "Add a new client: " + newClient.getClientUuid() + "",
        newClient.getClientUuid());

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  @RequestMapping(
      value = "/ams/v1/ams_client",
      produces = "application/json",
      method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<String> deleteClient(
      @RequestParam(value = "client_uuid", required = true) String clientUuid) {

    String projectId = AuthUtils.getProjectIdFromContext();
    AmsClient client = clientSrv.findByClientUUID(clientUuid, projectId);
    if (client == null) {
      return new ResponseEntity<String>("No such client: " + clientUuid, HttpStatus.NOT_FOUND);
    }

    // Delete client product install info
    piSrv.deleteByClientUuid(clientUuid);
    logSrv.LogToMysql("Delete", "Software", "Delete installed software of client", clientUuid);

    // Delete client product deploy info
    pdSrv.removeByClientUUID(clientUuid);
    logSrv.LogToMysql(
        "Delete", "Software Deploy", "Delete deployed software of client", clientUuid);

    // Delete client product change info
    pcSrv.removeByClientUuid(clientUuid);
    // Delete client product download history
    pdhSrv.removeByClientUuid(clientUuid);
    // Delete client mapping info
    mappingSrv.removeByClientUuid(clientUuid);
    // Delete client info
    clientSrv.removeByClientUUID(clientUuid);
    logSrv.LogToMysql(
        "Delete", "AmsClient", "Delete AmsClient, clientUuid: " + clientUuid + "", clientUuid);

    // Delete client info
    AstSrv.removeByTaskProperty(clientUuid);

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  public boolean userHasRight(AmsClient client, String projectId, TokenContent token) {
    /*
     * Firstly, should check if user has selected project. If not, he can operate
     * 1.the client which is not allocated to any project. 2.the client which has
     * been allocated to a project, but the project is in the user's assigned list.
     *
     * Special case: 3.the user's ROLE is SYS_ADMIN, although the client's allocated
     * project is not in user's assigned list, he can operate it as well.
     *
     * If user has selected project, he can operate 1.the client which is not
     * allocated to any projects 2.the client which is also allocated to this
     * project.
     */
    UserRole role = null;
    String user = "";
    if (token != null) {
      role = token.getRole();
      user = token.getUser();
    }
    /*
     * 1. Check USER ROLE firstly, if user GLOBAL ROLE is not SYS_ADMIN, then the
     * role in token is null. if it is SYS_ADMIN,allow operating.
     */
    boolean right_flag = false;
    if (role != null && role.getValue() == "ROLE_SYS_ADMIN") {
      right_flag = true;
    } else {
      /*
       * 2. Check if the client has been allocated to a project. User can operate the
       * clients which haven't been allocated.
       */
      String proId = client.getProjectId();
      if (proId == null) {
        right_flag = true;
      } else {
        if (projectId == null) {
          /*
           * 3. The normal user should be checked if client's allocated ProjectId is
           * included in User's assigned project list when user hasn't selected one
           * project.
           */
          try {
            CompletableFuture<List<ProjectDTO>> result = new CompletableFuture<>();
            IConnectRequest.create(
                    HttpMethod.GET,
                    String.format("%s?%s=%s", ConstDef.U_PROJECT, ConstDef.Q_USER, user),
                    MediaType.APPLICATION_JSON,
                    null)
                .send(
                    response -> {
                      if (response.getStatus().is2xxSuccessful()) {
                        try {
                          ProjectDTO[] projectDTOS =
                              new ObjectMapper()
                                  .readValue(response.getPayload(), ProjectDTO[].class);
                          result.complete(Arrays.asList(projectDTOS));
                        } catch (IOException e) {
                          log.warn(
                              "can not deserialize the payload of a response from GET /api/project");
                          result.complete(Collections.emptyList());
                        }
                      } else {
                        log.warn("GET /api/project return error {}", response);
                        result.complete(Collections.emptyList());
                      }
                    },
                    5,
                    TimeUnit.SECONDS);

            right_flag =
                result.get(5, TimeUnit.SECONDS).stream()
                    .filter(project -> Objects.equals(proId, project.getId()))
                    .findAny()
                    .map(project -> Boolean.TRUE)
                    .orElse(Boolean.FALSE);
          } catch (TimeoutException | InterruptedException | ExecutionException e) {
            log.warn("can not communicate with GET /api/project return {}", e);
          }
        } else {
          /*
           * 4. The normal user has selected a project which is the same as the client's
           * allocated project.
           */
          if (projectId.equals(proId)) {
            right_flag = true;
          }
        }
      }
    }
    return right_flag;
  }
}

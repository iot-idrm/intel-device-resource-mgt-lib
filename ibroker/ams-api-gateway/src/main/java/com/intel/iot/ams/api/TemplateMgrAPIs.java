/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.intel.iot.ams.api.requestbody.PostTemplate;
import com.intel.iot.ams.api.requestbody.PostTemplateDeploy;
import com.intel.iot.ams.api.requestbody.TemplateItem;
import com.intel.iot.ams.api.requestbody.TemplateItem.TemplateConfigItem;
import com.intel.iot.ams.entity.*;
import com.intel.iot.ams.security.AuthUtils;
import com.intel.iot.ams.service.*;
import com.intel.iot.ams.task.AmsTaskType;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RESTAPIs implementation of APIs document Chapter 2.4 Cloud configuration management
 *
 * @author Zhang, Yi Y <yi.y.zhang@intel.com>
 */
@RestController
@RequestMapping("/ams_user_cloud")
public class TemplateMgrAPIs {
  @Autowired private ProductService pSrv;

  @Autowired private AmsTemplateService tempSrv;

  @Autowired private AmsClientService clientSrv;

  @Autowired private ProductChangesService changeSrv;

  @Autowired private AmsTaskService taskSrv;

  @Autowired private CfgIdentifierService cfgIdSrv;

  @Autowired private CfgContentService cfgCntSrv;

  @Autowired private LogService logSrv;

  // ----------------------------------------------------------------
  //
  // RESTful APIs
  //
  // ----------------------------------------------------------------

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.1 Query AMS template
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Query AMS template
   *
   * <p>RESTful API: GET /ams/v1/template
   *
   * @param name the name of the template
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/template",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getTemplates(
      @RequestParam(value = "name", required = false) String name) {
    JsonArray jResult = new JsonArray();

    if (name != null) {
      AmsTemplate t = tempSrv.findByName(name);
      if (t == null) {
        return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
      } else {
        JsonObject jTemp = new JsonObject();
        jTemp.addProperty("id", t.getId());
        jTemp.addProperty("template_name", t.getName());
        if (t.getTitle() != null) {
          jTemp.addProperty("title", t.getTitle());
        }
        if (t.getDescription() != null) {
          jTemp.addProperty("description", t.getDescription());
        }
        jTemp.addProperty("content", t.getContent());

        jResult.add(jTemp);
      }
    } else {
      List<AmsTemplate> list = tempSrv.findAll();
      if (list != null) {
        for (AmsTemplate t : list) {
          // return list ignore the _global_ template & project default template
          if (t.getName() != null
              && (t.getName().equals("_global_") || t.getName().contains("projectId_"))) {
            continue;
          }
          JsonObject jTemp = new JsonObject();
          jTemp.addProperty("id", t.getId());
          jTemp.addProperty("template_name", t.getName());
          if (t.getTitle() != null) {
            jTemp.addProperty("title", t.getTitle());
          }
          if (t.getDescription() != null) {
            jTemp.addProperty("description", t.getDescription());
          }
          jTemp.addProperty("content", t.getContent());
          jResult.add(jTemp);
        }
      }
    }

    return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.2 Create/Update AMS template
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Create/Update AMS template
   *
   * <p>RESTful API: POST /ams/v1/template
   *
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(value = "/ams/v1/template", method = RequestMethod.POST)
  public ResponseEntity<String> postAmsTemplate(@RequestBody PostTemplate template) {

    if (template.getTemplateName() == null) {
      return new ResponseEntity<String>("template name can not be null", HttpStatus.BAD_REQUEST);
    }

    if (template.getContent() == null) {
      return new ResponseEntity<String>("template content can not be null", HttpStatus.BAD_REQUEST);
    }

    /**
     * Check the content format and the products, configuration identifiers and shared contents
     * existence
     */
    String content = template.getContent();
    List<TemplateItem> list = parseTemplateContent(content);
    if (list == null) {
      return new ResponseEntity<String>(
          "template content format is not correct!", HttpStatus.BAD_REQUEST);
    }

    for (TemplateItem item : list) {
      Product p = pSrv.findByName(item.getProductName());
      if (p == null) {
        return new ResponseEntity<String>(
            "Product: " + item.getProductName() + " cannot be found!", HttpStatus.BAD_REQUEST);
      }
      if (item.getCfgs() != null) {
        for (TemplateConfigItem cfg : item.getCfgs()) {
          if (cfg.getPathName() == null
              || cfg.getContentName() == null
              || cfg.getCfgType() == null) {
            return new ResponseEntity<String>(
                "template content format is not correct!", HttpStatus.BAD_REQUEST);
          }
          String pathName = cfg.getPathName();
          pathName.trim().replaceAll("\\\\", "/");
          if (pathName.startsWith("./")) {
            pathName = pathName.substring(1);
          } else {
            if (!pathName.startsWith("/")) {
              pathName = "/" + pathName;
            }
          }
          CfgIdentifier cfgId =
              cfgIdSrv.findByUserNameAndPathNameAndTargetType(
                  item.getProductName(), pathName, cfg.getCfgType());
          if (cfgId == null) {
            return new ResponseEntity<String>(
                "Configuration Identifier ( product name:  "
                    + item.getProductName()
                    + ", path name: "
                    + cfg.getPathName()
                    + ", target type: "
                    + cfg.getCfgType()
                    + ") cannot be found!",
                HttpStatus.BAD_REQUEST);
          }
          CfgContent share = cfgCntSrv.findBySharedName(cfg.getContentName());
          if (share == null) {
            return new ResponseEntity<String>(
                "Shared content: " + cfg.getContentName() + " cannot be found!",
                HttpStatus.BAD_REQUEST);
          }
        }
      }
    }

    AmsTemplate t = tempSrv.findByName(template.getTemplateName());
    if (t == null) {
      t = new AmsTemplate();
      t.setName(template.getTemplateName());
      t.setTitle(template.getTitle());
      t.setDescription(template.getDescription());
      t.setContent(template.getContent());
      tempSrv.save(t);
      /** Log to mysql */
      logSrv.LogToMysql("Add", "Template", "Template name:" + t.getName() + "", null);
    } else {
      t.setTitle(template.getTitle());
      t.setDescription(template.getDescription());
      t.setContent(template.getContent());
      tempSrv.update(t);
      /** Log to mysql */
      logSrv.LogToMysql("Update", "Template", "Template name:" + t.getName() + "", null);
    }

    /** Create AmsTask to calculate product changes of all clients */
    List<AmsClient> clientList = clientSrv.findAll();
    if (clientList != null) {
      for (AmsClient client : clientList) {
        /** Remove old product changes of this client */
        changeSrv.removeByClientUuid(client.getClientUuid());

        AmsTask task = new AmsTask();
        task.setTaskType(AmsTaskType.CALCULATE_PRODUCT_CHANGES);
        task.setTaskCreateTime(new Date());
        JsonObject jTaskProperty = new JsonObject();
        jTaskProperty.addProperty("client_uuid", client.getClientUuid());
        task.setTaskProperties(jTaskProperty.toString());
        taskSrv.save(task);
      }
    }

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.3 Delete AMS template
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Delete AMS template
   *
   * <p>RESTful API: Delete /ams/v1/template
   *
   * @param name the name of the AMS template
   */
  @RequestMapping(
      value = "/ams/v1/template",
      produces = "application/json",
      method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<String> deleteTemplate(
      @RequestParam(value = "name", required = true) String name) {
    tempSrv.removeByName(name);
    /** update the AmsClient's templateName bound with this template */
    List<AmsClient> cltList = clientSrv.findByTemplateName(name);
    cltList.stream()
        .filter(item -> item != null)
        .forEach(
            item -> {
              item.setTemplateName(null);
              clientSrv.update(item);
            });

    /** Log to mysql */
    logSrv.LogToMysql("Delete", "Template", "Template name:" + name + "", null);

    /** Create AmsTask to calculate product changes of all clients */
    List<AmsClient> clientList = clientSrv.findAll();
    if (clientList != null) {
      for (AmsClient client : clientList) {
        /** Remove old product changes of this client */
        changeSrv.removeByClientUuid(client.getClientUuid());

        AmsTask task = new AmsTask();
        task.setTaskType(AmsTaskType.CALCULATE_PRODUCT_CHANGES);
        task.setTaskCreateTime(new Date());
        JsonObject jTaskProperty = new JsonObject();
        jTaskProperty.addProperty("client_uuid", client.getClientUuid());
        task.setTaskProperties(jTaskProperty.toString());
        taskSrv.save(task);
      }
    }

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.4 Query AMS template deploy
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Query AMS template deploy
   *
   * <p>RESTful API: GET /ams/v1/template/deploy
   *
   * @param name the name of the template
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/template/deploy",
      produces = "application/json",
      method = RequestMethod.GET)
  public ResponseEntity<String> getTemplateDeploy(
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "client_uuid", required = false) String uuid) {

    String projectId = AuthUtils.getProjectIdFromContext();

    if (name == null && uuid == null) {
      return new ResponseEntity<String>(
          "Query parameter \"name\" or \"client_uuid\" is required.", HttpStatus.BAD_REQUEST);
    }

    if (name != null && uuid != null) {
      return new ResponseEntity<String>(
          "Query parameter \"name\" and \"client_uuid\" cannot be used together.",
          HttpStatus.BAD_REQUEST);
    }

    if (name != null) {
      List<AmsClient> list = clientSrv.findByTemplateName(name, projectId);
      if (list == null) {
        return new ResponseEntity<String>(
            "No client deployed with template: " + name, HttpStatus.NOT_FOUND);
      }
      JsonArray r = new JsonArray();
      for (AmsClient client : list) {
        r.add(new JsonPrimitive(client.getClientUuid()));
      }
      return new ResponseEntity<String>(r.toString(), HttpStatus.OK);
    }

    if (uuid != null) {
      AmsClient client = clientSrv.findByClientUUID(uuid, projectId);
      if (client == null) {
        return new ResponseEntity<String>("Cannot find client: " + uuid, HttpStatus.NOT_FOUND);
      }

      if (client.getTemplateName() != null) {
        JsonObject r = new JsonObject();
        r.addProperty("template_name", client.getTemplateName());
        return new ResponseEntity<String>(r.toString(), HttpStatus.OK);
      } else {
        return new ResponseEntity<String>(
            "Client: " + uuid + " is not deployed with any template!", HttpStatus.NOT_FOUND);
      }
    }

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.5 Create/Update AMS template deploy
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Create/Update AMS template deploy
   *
   * <p>RESTful API: POST /ams/v1/template/deploy
   *
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/template/deploy",
      produces = "application/json",
      method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> postTemplateDeploy(@RequestBody PostTemplateDeploy deploy) {

    String projectId = AuthUtils.getProjectIdFromContext();

    if (deploy.getTemplateName() == null) {
      return new ResponseEntity<String>("template name can not be null", HttpStatus.BAD_REQUEST);
    }

    if (deploy.getTemplateName().equals("_global_")) {
      return new ResponseEntity<String>(
          "global template can not be assigned.", HttpStatus.BAD_REQUEST);
    }

    if (deploy.getClientList() == null || deploy.getClientList().size() == 0) {
      return new ResponseEntity<String>("client list can not be null", HttpStatus.BAD_REQUEST);
    }

    for (String uuid : deploy.getClientList()) {
      AmsClient client = clientSrv.findByClientUUID(uuid, projectId);
      if (client != null) {
        client.setTemplateName(deploy.getTemplateName());
        clientSrv.update(client);

        /** log to mysql */
        logSrv.LogToMysql(
            "Add",
            "Template Deploy",
            "Deploy template for client, template name:" + deploy.getTemplateName() + "",
            client.getClientUuid());

        /** Delete all old product changes of this client */
        changeSrv.removeByClientUuid(client.getClientUuid());

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

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.6 Delete AMS template deploy
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Delete AMS template deploy
   *
   * <p>RESTful API: DELETE /ams/v1/template/deploy
   *
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/template/deploy",
      produces = "application/json",
      method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<String> deleteTemplateDeploy(
      @RequestParam(value = "client_uuid", required = true) String uuid) {

    String projectId = AuthUtils.getProjectIdFromContext();
    AmsClient client = clientSrv.findByClientUUID(uuid, projectId);
    if (client != null) {
      client.setTemplateName(null);
      clientSrv.update(client);
      logSrv.LogToMysql(
          "Delete", "Template Deploy", "Remove the template of the client", client.getClientUuid());

      /** Delete all old product changes of this client */
      changeSrv.removeByClientUuid(client.getClientUuid());

      /** Create a AmsTask to calculate product changes of this client */
      AmsTask task = new AmsTask();
      task.setTaskType(AmsTaskType.CALCULATE_PRODUCT_CHANGES);
      task.setTaskCreateTime(new Date());
      JsonObject jTaskProperty = new JsonObject();
      jTaskProperty.addProperty("client_uuid", client.getClientUuid());
      task.setTaskProperties(jTaskProperty.toString());
      taskSrv.save(task);
    }

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  // ----------------------------------------------------------------
  //
  // Private Methods
  //
  // ----------------------------------------------------------------

  private List<TemplateItem> parseTemplateContent(String content) {
    List<TemplateItem> list;
    Gson gson = new Gson();
    try {
      list = gson.fromJson(content, new TypeToken<List<TemplateItem>>() {}.getType());
    } catch (JsonSyntaxException jse) {
      return null;
    }
    return list;
  }
}

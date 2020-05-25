/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.intel.iot.ams.api.requestbody.PostCfgIdPayload;
import com.intel.iot.ams.entity.*;
import com.intel.iot.ams.entity.TemplateItem.TemplateConfigItem;
import com.intel.iot.ams.service.*;
import com.intel.iot.ams.utils.HashUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CfgMgrAPIs {
  private static final Logger logger = LoggerFactory.getLogger(CfgMgrAPIs.class);

  @Autowired private CfgIdentifierService cfgIdSrv;

  @Autowired private CfgInstanceService cfgInstSrv;

  @Autowired private CfgContentService cfgCntSrv;

  @Autowired private ProductService pSrv;

  /** * added by wangning */
  @Autowired private AmsClientService amsSrv;

  @Autowired private ClientCurrentCfgService clicSrv;
  @Autowired private ProductInstalledService prdinstSrv;
  @Autowired private ClientcfgCheckPointService clickpSrv;
  @Autowired private AmsTemplateService amsTpltSrv;
  @Autowired private LogService logSrv;

  private static final int DFLTOFFSET = 0; // The first page, which starts
  // from 0
  private static final int DFLTLIMIT = 10; // Default size for each page.
  // to 100
  private static final int MAXLIMIT = 100;

  // end wn

  // ----------------------------------------------------------------
  //
  // RESTful APIs
  //
  // ----------------------------------------------------------------

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.1 User system post configuration instance to target
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * User posts configuration instance to target
   *
   * <p>RESTful API: POST /ams/v1/config/instance
   *
   * @param payload the POST payload data
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/config/instance",
      produces = "application/json",
      method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> postCfgInstanceToTarget(
      @RequestParam(value = "product_name", required = true) String productName,
      @RequestParam(value = "path_name", required = true) String pathName,
      @RequestParam(value = "target_type", required = true) String targetType,
      @RequestParam(value = "target_id", required = true) String targetId,
      @RequestParam(value = "content_name", required = false) String contentName,
      @RequestBody(required = false) String payload) {

    if (contentName == null && (payload == null || payload.equals(""))) {
      return new ResponseEntity<String>(
          "content_name and payload cannot be null at the same time!", HttpStatus.BAD_REQUEST);
    }

    Product p = pSrv.findByName(productName);
    if (p == null) {
      return new ResponseEntity<String>("Unknown Product: " + productName, HttpStatus.BAD_REQUEST);
    }

    pathName.trim().replaceAll("\\\\", "/");
    if (pathName.startsWith("./")) {
      pathName = pathName.substring(1);
    } else {
      if (!pathName.startsWith("/")) {
        pathName = "/" + pathName;
      }
    }

    CfgContent content = null;
    if (contentName != null && !contentName.equals("")) {
      content = cfgCntSrv.findBySharedName(contentName);
      if (content == null) {
        return new ResponseEntity<String>(
            "Shared content \"" + contentName + "\" cannot be found.", HttpStatus.NOT_FOUND);
      }
    }

    CfgIdentifier cfgId =
        cfgIdSrv.findByUserNameAndPathNameAndTargetType(productName, pathName, targetType);
    if (cfgId == null) {
      cfgId = new CfgIdentifier();
      cfgId.setPathName(pathName);
      cfgId.setUserName(productName);
      cfgId.setTargetType(targetType);
      cfgId.setCfgUuid(UUID.randomUUID().toString());
      cfgIdSrv.save(cfgId);
      /** Log to MySQL */
      logSrv.LogToMysql(
          "Add",
          "Software Config",
          "Add config, software name: "
              + cfgId.getUserName()
              + ", "
              + "config pathname: "
              + cfgId.getPathName()
              + " , "
              + "config targetType:"
              + cfgId.getTargetType()
              + "",
          null);
    }

    CfgInstance instance =
        cfgInstSrv.findByCfgIdentifierUUIDAndTargetId(cfgId.getCfgUuid(), targetId);
    if (instance == null) {
      instance = new CfgInstance();
      instance.setCfgUuid(cfgId.getCfgUuid());
      instance.setTargetId(targetId);
      if (content == null) {
        if (payload != null && !payload.equals("")) {
          String hash = HashUtils.getMd5Hash(payload.getBytes());
          content = new CfgContent();
          content.setContentType(2);
          content.setContent(payload);
          content.setContentHash(hash);
          cfgCntSrv.save(content);
          /** Log to MySQL */
          logSrv.LogToMysql(
              "Add",
              "Software Config",
              "Add customized content for instance whose target id:" + instance.getTargetId() + "",
              null);
          instance.setContentId(content.getId());
        }
      } else {
        instance.setContentId(content.getId());
      }
      cfgInstSrv.save(instance);
      /** Log to MySQL */
      logSrv.LogToMysql(
          "Add",
          "Software Config",
          "Add config instance, targetId: " + instance.getTargetId() + "",
          null);

    } else {
      if (instance.getContentId() != null) {
        CfgContent old = cfgCntSrv.findById(instance.getContentId());
        if (old != null && old.getContentType() == 2) {
          cfgCntSrv.delete(old);
          /** Log to MySQL */
          logSrv.LogToMysql(
              "Delete",
              "Software Config",
              "Delete customized content of instance whose target id: "
                  + instance.getTargetId()
                  + "",
              null);
        }
      }
      if (content == null) {
        if (payload != null && !payload.equals("")) {
          String hash = HashUtils.getMd5Hash(payload.getBytes());
          content = new CfgContent();
          content.setContentType(2);
          content.setContent(payload);
          content.setContentHash(hash);
          cfgCntSrv.save(content);
          /** Log to MySQL */
          logSrv.LogToMysql(
              "Add",
              "Software Config",
              "Add customized content for instance whose target id: " + instance.getTargetId() + "",
              null);
          instance.setContentId(content.getId());
        }
      } else {
        instance.setContentId(content.getId());
      }
      cfgInstSrv.update(instance);
      /** Log to MySQL */
      logSrv.LogToMysql(
          "Update",
          "Software Config",
          "Update config instance, targetId" + instance.getTargetId() + "",
          null);
    }

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.2 User system post default content for configuration identifier
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * User posts default content for configuration identifier
   *
   * <p>RESTful API: POST /ams/v1/config/identifier
   *
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/config/identifier",
      produces = "application/json",
      method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> postDefaultContentToCfgIdentifier(
      @RequestParam(value = "product_name", required = true) String productName,
      @RequestParam(value = "path_name", required = true) String pathName,
      @RequestParam(value = "target_type", required = true) String targetType,
      @RequestBody String payload) {

    Product p = pSrv.findByName(productName);
    if (p == null) {
      return new ResponseEntity<String>("No such Product: " + productName, HttpStatus.BAD_REQUEST);
    }

    if (payload == null) {
      return new ResponseEntity<String>("No Payload", HttpStatus.BAD_REQUEST);
    }

    PostCfgIdPayload info = parseCfgIdPayload(payload);

    pathName.trim().replaceAll("\\\\", "/");

    if (pathName.startsWith("./")) {
      pathName = pathName.substring(1);
    } else {
      if (!pathName.startsWith("/")) {
        pathName = "/" + pathName;
      }
    }
    CfgContent content = null;

    CfgIdentifier cfgId =
        cfgIdSrv.findByUserNameAndPathNameAndTargetType(p.getName(), pathName, targetType);
    if (cfgId == null) {
      cfgId = new CfgIdentifier();
      cfgId.setPathName(pathName);
      cfgId.setUserName(p.getName());
      cfgId.setTargetType(targetType);
      cfgId.setCfgUuid(UUID.randomUUID().toString());
      if (info != null) {
        if (info.getContent() != null && !info.getContent().equals("")) {
          String hash = HashUtils.getMd5Hash(info.getContent().getBytes());
          content = new CfgContent();
          content.setContentType(0);
          content.setContent(info.getContent());
          content.setContentHash(hash);
          cfgCntSrv.save(content);
          /** Log to MySQL */
          logSrv.LogToMysql(
              "Add",
              "Software Config",
              "Add default content for config, config pathName:"
                  + cfgId.getPathName()
                  + ", config targetType: "
                  + cfgId.getTargetType()
                  + ", software name:"
                  + cfgId.getUserName()
                  + "",
              null);

          cfgId.setDefaultContentId(content.getId());
        }
        if (info.getSchema() != null && !info.getSchema().equals("")) {
          cfgId.setSchema(info.getSchema());
        }
      }
      cfgIdSrv.save(cfgId);
      /** Log to MySQL */
      logSrv.LogToMysql(
          "Add",
          "Software Config",
          "Add software config, software name: "
              + cfgId.getUserName()
              + ", "
              + "config pathname: "
              + cfgId.getPathName()
              + ", "
              + "config targetType:"
              + cfgId.getTargetType()
              + "",
          null);

    } else {
      if (info != null) {
        if (info.getContent() != null && !info.getContent().equals("")) {
          String hash = HashUtils.getMd5Hash(info.getContent().getBytes());
          if (cfgId.getDefaultContentId() != null) {
            content = cfgCntSrv.findById(cfgId.getDefaultContentId());
          }
          if (content != null) {
            content.setContent(info.getContent());
            content.setContentHash(hash);
            cfgCntSrv.update(content);
            /** Log to MySQL */
            logSrv.LogToMysql(
                "Update",
                "Software Config",
                "Update default content for config, config pathName:"
                    + cfgId.getPathName()
                    + ", config targetType: "
                    + cfgId.getTargetType()
                    + ", software name:"
                    + cfgId.getUserName()
                    + "",
                null);

          } else {
            content = new CfgContent();
            content.setContentType(0);
            content.setContent(info.getContent());
            content.setContentHash(hash);
            cfgCntSrv.save(content);
            /** Log to MySQL */
            logSrv.LogToMysql(
                "Add",
                "Software Config",
                "Add default content for config, config pathName:"
                    + cfgId.getPathName()
                    + ", config targetType: "
                    + cfgId.getTargetType()
                    + ", software name:"
                    + cfgId.getUserName()
                    + "",
                null);
          }
          cfgId.setDefaultContentId(content.getId());
        } else {
          if (cfgId.getDefaultContentId() != null) {
            content = cfgCntSrv.findById(cfgId.getDefaultContentId());
            if (content != null) {
              cfgCntSrv.delete(content);
              /** Log to MySQL */
              logSrv.LogToMysql(
                  "Delete",
                  "Software Config",
                  "Delete default of config, config pathName:"
                      + cfgId.getPathName()
                      + ", config targetType: "
                      + cfgId.getTargetType()
                      + ", software name:"
                      + cfgId.getUserName()
                      + "",
                  null);
            }
            cfgId.setDefaultContentId(null);
          }
        }

        if (info.getSchema() != null && !info.getSchema().equals("")) {
          cfgId.setSchema(info.getSchema());
        } else {
          cfgId.setSchema(null);
        }
        cfgIdSrv.update(cfgId);
        /** Log to MySQL */
        logSrv.LogToMysql(
            "Update",
            "Software Config",
            "Update software config, software name: "
                + cfgId.getUserName()
                + ", "
                + "config pathname: "
                + cfgId.getPathName()
                + " , "
                + "config targetType:"
                + cfgId.getTargetType()
                + "",
            null);
      }
    }
    return new ResponseEntity<String>(HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.3 Query configuration identifier information
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * User query configuration identifier information
   *
   * <p>RESTful API: GET /ams/v1/config/identifier
   *
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/config/identifier",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getCfgIdentifier(
      @RequestParam(value = "product_name", required = false) String productName,
      @RequestParam(value = "path_name", required = false) String pathName,
      @RequestParam(value = "target_type", required = false) String targetType) {

    if (productName != null) {
      Product p = pSrv.findByName(productName);
      if (p == null) {
        return new ResponseEntity<String>(
            "Unknown Product: " + productName, HttpStatus.BAD_REQUEST);
      }
    }

    if (pathName != null) {
      pathName.trim().replaceAll("\\\\", "/");

      if (pathName.startsWith("./")) {
        pathName = pathName.substring(1);
      } else {
        if (!pathName.startsWith("/")) {
          pathName = "/" + pathName;
        }
      }
    }

    JsonArray jResult = new JsonArray();

    List<CfgIdentifier> idList = null;

    if (productName == null && pathName == null && targetType == null) {
      idList = cfgIdSrv.findAll();
    }

    if (productName != null && pathName == null && targetType == null) {
      idList = cfgIdSrv.findByUserName(productName);
    }

    if (productName == null && pathName != null && targetType == null) {
      idList = cfgIdSrv.findByPathName(pathName);
    }

    if (productName == null && pathName == null && targetType != null) {
      idList = cfgIdSrv.findByTargetType(targetType);
    }

    if (productName != null && pathName != null && targetType == null) {
      idList = cfgIdSrv.findByUserNameAndPathName(productName, pathName);
    }

    if (productName != null && pathName == null && targetType != null) {
      idList = cfgIdSrv.findByUserNameAndTargetType(productName, targetType);
    }

    if (productName != null && pathName != null && targetType != null) {
      CfgIdentifier id =
          cfgIdSrv.findByUserNameAndPathNameAndTargetType(productName, pathName, targetType);
      if (id != null) {
        idList = new ArrayList<CfgIdentifier>();
        idList.add(id);
      }
    }

    if (idList != null) {
      for (CfgIdentifier cfgId : idList) {
        JsonObject j = cfgIdSerialize(cfgId);
        if (j != null) {
          jResult.add(j);
        }
      }
    }

    return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.4 Delete configuration identifier
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Delete a configuration identifier
   *
   * <p>RESTful API: DELETE /ams/v1/config/identifier
   *
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/config/identifier",
      produces = "application/json",
      method = RequestMethod.DELETE)
  public ResponseEntity<String> deleteCfgId(
      @RequestParam(value = "product_name", required = false) String productName,
      @RequestParam(value = "path_name", required = false) String pathName,
      @RequestParam(value = "target_type", required = false) String targetType) {
    if (productName == null || pathName == null) {
      return new ResponseEntity<String>(
          "Query parameter \"product_name\", \"path_name\" and \"target_type\" are required.",
          HttpStatus.BAD_REQUEST);
    }

    Product p = pSrv.findByName(productName);
    if (p == null) {
      return new ResponseEntity<String>("Unknown Product: " + productName, HttpStatus.BAD_REQUEST);
    }

    if (pathName != null) {
      pathName.trim().replaceAll("\\\\", "/");

      if (pathName.startsWith("./")) {
        pathName = pathName.substring(1);
      } else {
        if (!pathName.startsWith("/")) {
          pathName = "/" + pathName;
        }
      }
    }

    CfgIdentifier cfgId =
        cfgIdSrv.findByUserNameAndPathNameAndTargetType(productName, pathName, targetType);
    if (cfgId != null) {
      List<CfgInstance> instanceList = cfgInstSrv.findByCfgIdentifierUUID(cfgId.getCfgUuid());
      if (instanceList != null) {
        for (CfgInstance instance : instanceList) {
          CfgContent content = cfgCntSrv.findById(instance.getContentId());
          if (content != null) {
            /** Delete individual content before delete instance */
            if (content.getContentType() == 2) {
              cfgCntSrv.delete(content);
              /** Log to MySQL */
              logSrv.LogToMysql(
                  "Delete",
                  "Software Config",
                  "Delete customized content of instance whose target id: "
                      + instance.getTargetId()
                      + "",
                  null);
            }
          }
          cfgInstSrv.delete(instance);
          /** Log to MySQL */
          logSrv.LogToMysql(
              "Delete",
              "Software Config",
              "Delete config instance, targetId: " + instance.getTargetId() + "",
              null);
        }
      }
      if (cfgId.getDefaultContentId() != null) {
        CfgContent defaultCnt = cfgCntSrv.findById(cfgId.getDefaultContentId());
        if (defaultCnt != null) {
          cfgCntSrv.delete(defaultCnt);
          /** Log to MySQL */
          logSrv.LogToMysql(
              "Delete",
              "Software Config",
              "Delete default content of config, config pathName:"
                  + cfgId.getPathName()
                  + ", config targetType: "
                  + cfgId.getTargetType()
                  + ", software name:"
                  + cfgId.getUserName()
                  + "",
              null);
        }
      }
      cfgIdSrv.delete(cfgId);
      /** Log to MySQL */
      logSrv.LogToMysql(
          "Delete",
          "Software Config",
          "Delete config, software name: "
              + cfgId.getUserName()
              + ", "
              + "config pathname: "
              + cfgId.getPathName()
              + " , "
              + "config targetType:"
              + cfgId.getTargetType()
              + "",
          null);
    }

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.5 Query shared configuration content information
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * User query configuration content information
   *
   * <p>RESTful API: GET /ams/v1/config/shared
   *
   * @return an instance of ResponseEntity<String> with response code
   */
  // @RequestMapping(value = "/ams/v1/config/shared", produces =
  // "application/json",
  // method = RequestMethod.GET)
  // @ResponseBody
  // public ResponseEntity<String>
  // getSharedContent(@RequestParam(value = "shared_name", required = false)
  // String sharedName,
  // @RequestParam(value = "name_like", required = false) String nameLike,
  // @RequestParam(value = "tag", required = false) String tag) {
  //
  // if (sharedName != null || nameLike != null || tag != null) {
  // if (sharedName != null) {
  // CfgContent content = cfgCntSrv.findBySharedName(sharedName);
  // if (content == null) {
  // logger.warn("No shared content found for " + sharedName);
  // return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
  // }
  // return new ResponseEntity<String>(cfgContentSerialize(content).toString(),
  // HttpStatus.OK);
  // } else {
  //
  // JsonArray jResult = new JsonArray();
  // List<CfgContent> cntList = null;
  //
  // if (tag != null && nameLike == null) {
  // /* WHERE tag == ?tag */
  // cntList = cfgCntSrv.findByTag(tag);
  // } else if (tag == null && nameLike != null) {
  // /* WHERE sharedName like ?nameLike */
  // cntList = cfgCntSrv.findByNameLike(nameLike);
  // } else { // tag != null && nameLike != null
  // /* WHERE tag == ?tag AND sharedName like ?nameLike */
  // cntList = cfgCntSrv.findByNameLikeAndTag(nameLike, tag);
  // }
  //
  // if (cntList != null) {
  // for (CfgContent content : cntList) {
  // jResult.add(cfgContentSerialize(content));
  // }
  // } else {
  // logger.warn("No shared content found for *" + nameLike + "*" + " with tag "
  // + (tag == null ? "null" : tag));
  // }
  // return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
  // }
  // }
  //
  // JsonArray jResult = new JsonArray();
  //
  // List<CfgContent> cntList = cfgCntSrv.findAll();
  // if (cntList != null) {
  // for (CfgContent content : cntList) {
  // jResult.add(cfgContentSerialize(content));
  // }
  // } else {
  // logger.warn("No shared content found in server!");
  // }
  //
  // return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
  // }

  @RequestMapping(
      value = "/ams/v1/config/shared",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getSharedContent_new(
      @RequestParam(value = "shared_name", required = false) String sharedName,
      @RequestParam(value = "name_like", required = false) String nameLike,
      @RequestParam(value = "tag", required = false) String tag,
      @RequestParam(value = "content_type", required = false) Integer contentType,
      @RequestParam(value = "fuzz_str", required = false) String fuzzStr,
      @RequestParam(value = "offset", required = false) Integer offset,
      @RequestParam(value = "limit", required = false) Integer limit) {

    logger.debug(
        "getCfgContent shared_name={}, name_like={}, tag={},content_type={}, fuzz_str={}",
        sharedName,
        nameLike,
        tag,
        contentType,
        fuzzStr);

    JsonArray jResult = new JsonArray();
    List<CfgContent> cntList = null;

    if (sharedName == null
        && nameLike == null
        && tag == null
        && contentType == null
        && fuzzStr == null) {
      logger.warn(
          "At least, should give \"fuzz_str=\" or one of sharedName, nameLike,tag or contentType");
      return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    if (fuzzStr != null
        && (sharedName != null || nameLike != null || tag != null || contentType != null)) {
      return new ResponseEntity<String>(
          "Fuzzy search cannot be used together with other parameters.", HttpStatus.BAD_REQUEST);
    }

    // right now, contentType can't be used with other parameters.
    if (contentType != null && (sharedName != null || nameLike != null || tag != null)) {
      return new ResponseEntity<String>(
          "Query parameter contentType cannot be used together with other parameters.",
          HttpStatus.BAD_REQUEST);
    }

    if (offset != null) {
      if (offset < 0) {
        logger.warn("offset must be equal to or larger than 0.");
        return new ResponseEntity<String>(
            "offset must be equal to or larger than 0.", HttpStatus.BAD_REQUEST);
      }
    } else {
      offset = DFLTOFFSET;
    }

    if (limit != null) {
      if (limit > MAXLIMIT) {
        logger.warn("limit is too large");
        return new ResponseEntity<String>("limit is too large", HttpStatus.BAD_REQUEST);
      }
    } else {
      limit = DFLTLIMIT;
    }

    if (sharedName != null || nameLike != null || tag != null) {
      if (sharedName != null) {
        CfgContent content = cfgCntSrv.findBySharedName(sharedName);
        if (content == null) {
          logger.warn("No shared content found for " + sharedName);
          return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(cfgContentSerialize(content).toString(), HttpStatus.OK);
      } else {

        if (tag != null && nameLike == null) {
          /* WHERE tag == ?tag */
          cntList = cfgCntSrv.findByTag(tag);
        } else if (tag == null && nameLike != null) {
          /* WHERE sharedName like ?nameLike */
          cntList = cfgCntSrv.findByNameLike(nameLike);
        } else { // tag != null && nameLike != null
          /* WHERE tag == ?tag AND sharedName like ?nameLike */
          cntList = cfgCntSrv.findByNameLikeAndTag(nameLike, tag);
        }
        if (cntList != null) {
          for (CfgContent content : cntList) {
            jResult.add(cfgContentSerialize(content));
          }
        } else {
          logger.warn(
              "No shared content found for *"
                  + nameLike
                  + "*"
                  + " with tag "
                  + (tag == null ? "null" : tag));
        }
        return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
      }
    } else {
      // find one type content or fuzz search.
      if (contentType != null) {
        cntList = cfgCntSrv.findByType(contentType);
      } else if (fuzzStr != null) {
        logger.debug("fuzzStr={}, size={}", fuzzStr, fuzzStr.length());
        cntList = cfgCntSrv.fuzzySearch(fuzzStr, offset, limit);
      }
    }

    if (cntList != null) {
      for (CfgContent content : cntList) {
        jResult.add(cfgContentSerialize(content));
      }
    } else {
      logger.warn("No shared content found in server!");
    }

    return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.6 Create/Update shared configuration content
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Create/Update shared configuration content
   *
   * <p>RESTful API: POST /ams/v1/config/shared
   *
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/config/shared",
      produces = "application/json",
      method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> postSharedContent(@RequestBody Map<String, String> payload) {

    if (payload == null || payload.size() == 0 || payload.get("content") == null) {
      logger.warn("payload.content is empty");
      return new ResponseEntity<String>(
          "content in payload cannot be null", HttpStatus.BAD_REQUEST);
    }

    if (payload.get("shared_name") == null) {
      logger.warn("payload.shared_name is empty");
      return new ResponseEntity<String>(
          "\"shared_name\" in payload cannot be null", HttpStatus.BAD_REQUEST);
    }

    String hash = HashUtils.getMd5Hash(payload.get("content").getBytes());
    CfgContent content = cfgCntSrv.findBySharedName(payload.get("shared_name"));
    if (content == null) {
      content = new CfgContent();
      content.setContentType(1);
      content.setSharedName(payload.get("shared_name"));
      content.setContent(payload.get("content"));
      content.setContentHash(hash);
      content.setTag(payload.get("tag"));
      content.setDescription(payload.get("description"));
      cfgCntSrv.save(content);
      /** Log to MySQL */
      logSrv.LogToMysql(
          "Add",
          "Shared Content",
          "Add shared content, shared name:" + content.getSharedName() + "",
          null);

    } else {
      content.setContent(payload.get("content"));
      content.setContentHash(hash);
      if (payload.get("tag") != null) content.setTag(payload.get("tag"));
      if (payload.get("description") != null) content.setDescription(payload.get("description"));
      cfgCntSrv.update(content);
      /** Log to MySQL */
      logSrv.LogToMysql(
          "Update",
          "Shared Content",
          "Update shared content, shared name:" + content.getSharedName() + "",
          null);
    }

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.7 Delete shared configuration content
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Delete shared configuration content
   *
   * <p>RESTful API: DELETE /ams/v1/config/shared
   *
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/config/shared",
      produces = "application/json",
      method = RequestMethod.DELETE)
  public ResponseEntity<String> deleteSharedCnt(
      @RequestParam(value = "shared_name", required = false) String sharedName) {

    /** TODO: how to deal with the instance which use the shared content */
    if (sharedName != null) {
      CfgContent content = cfgCntSrv.findBySharedName(sharedName);
      if (content != null) {
        cfgCntSrv.delete(content);
        /** Log to MySQL */
        logSrv.LogToMysql(
            "Delete",
            "Shared Content",
            "Delete shared content, shared name:" + content.getSharedName() + "",
            null);
      }
    }
    return new ResponseEntity<String>(HttpStatus.OK);
  }

  /**
   * ----------------------------------------------------------------
   *
   * <p>Chapter 2.4.8 Query configuration instance information
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * User query configuration instance information
   *
   * <p>RESTful API: GET /ams/v1/config/instance
   *
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/config/instance",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getCfgInstance(
      @RequestParam(value = "product_name", required = true) String productName,
      @RequestParam(value = "path_name", required = false) String pathName,
      @RequestParam(value = "target_type", required = false) String targetType,
      @RequestParam(value = "target_id", required = false) String targetId) {

    if (targetId != null && targetType == null) {
      return new ResponseEntity<String>(
          "Query parameter \"target_id\" must be used together with \"target_type\"!",
          HttpStatus.BAD_REQUEST);
    }

    Product p = pSrv.findByName(productName);
    if (p == null) {
      return new ResponseEntity<String>("Unknown Product: " + productName, HttpStatus.BAD_REQUEST);
    }

    if (pathName != null) {
      pathName.trim().replaceAll("\\\\", "/");

      if (pathName.startsWith("./")) {
        pathName = pathName.substring(1);
      } else {
        if (!pathName.startsWith("/")) {
          pathName = "/" + pathName;
        }
      }
    }

    JsonArray jResult = new JsonArray();
    List<CfgIdentifier> idList = null;

    if (pathName != null && targetType != null) {
      CfgIdentifier cfgId =
          cfgIdSrv.findByUserNameAndPathNameAndTargetType(p.getName(), pathName, targetType);
      if (cfgId != null) {
        idList = new ArrayList<CfgIdentifier>();
        idList.add(cfgId);
      }
    }

    if (pathName != null && targetType == null) {
      idList = cfgIdSrv.findByUserNameAndPathName(productName, pathName);
    }

    if (pathName == null && targetType != null) {
      idList = cfgIdSrv.findByUserNameAndTargetType(productName, targetType);
    }

    if (pathName == null && targetType == null) {
      idList = cfgIdSrv.findByUserName(productName);
    }

    if (idList != null) {
      for (CfgIdentifier cfgId : idList) {
        if (targetId == null) {
          List<CfgInstance> instanceList = cfgInstSrv.findByCfgIdentifierUUID(cfgId.getCfgUuid());
          if (instanceList != null) {
            for (CfgInstance instance : instanceList) {
              JsonObject j = cfgInstanceSerialize(cfgId, instance);
              if (j != null) {
                jResult.add(j);
              }
            }
          }
        } else {
          CfgInstance cfgInstance =
              cfgInstSrv.findByCfgIdentifierUUIDAndTargetId(cfgId.getCfgUuid(), targetId);
          if (cfgInstance != null) {
            JsonObject j = cfgInstanceSerialize(cfgId, cfgInstance);
            if (j != null) {
              jResult.add(j);
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
   * <p>Chapter 2.4.9 Delete configuration instance
   *
   * <p>----------------------------------------------------------------
   */

  /**
   * Delete configuration instance
   *
   * <p>RESTful API: DELETE /ams/v1/config/instance
   *
   * @return an instance of ResponseEntity<String> with response code
   */
  @RequestMapping(
      value = "/ams/v1/config/instance",
      produces = "application/json",
      method = RequestMethod.DELETE)
  public ResponseEntity<String> deleteCfgInstance(
      @RequestParam(value = "product_name", required = true) String productName,
      @RequestParam(value = "path_name", required = true) String pathName,
      @RequestParam(value = "target_type", required = true) String targetType,
      @RequestParam(value = "target_id", required = true) String targetId) {

    Product p = pSrv.findByName(productName);
    if (p == null) {
      return new ResponseEntity<String>("Unknown Product: " + productName, HttpStatus.BAD_REQUEST);
    }

    if (pathName != null) {
      pathName.trim().replaceAll("\\\\", "/");

      if (pathName.startsWith("./")) {
        pathName = pathName.substring(1);
      } else {
        if (!pathName.startsWith("/")) {
          pathName = "/" + pathName;
        }
      }
    }

    CfgIdentifier cfgId =
        cfgIdSrv.findByUserNameAndPathNameAndTargetType(p.getName(), pathName, targetType);
    if (cfgId != null) {
      CfgInstance instance =
          cfgInstSrv.findByCfgIdentifierUUIDAndTargetId(cfgId.getCfgUuid(), targetId);
      if (instance != null) {
        CfgContent content = cfgCntSrv.findById(instance.getContentId());
        if (content != null) {
          if (content.getContentType() == 2) {
            cfgCntSrv.delete(content);
            /** Log to MySQL */
            logSrv.LogToMysql(
                "Delete",
                "Software Config",
                "Delete customized content of instance whose target id: "
                    + instance.getTargetId()
                    + "",
                null);
          }
        }
        cfgInstSrv.delete(instance);
        /** Log to MySQL */
        logSrv.LogToMysql(
            "Delete",
            "Software Config",
            "Delete config instance, targetId: " + instance.getTargetId() + "",
            null);
      }
    }

    return new ResponseEntity<String>(HttpStatus.OK);
  }

  /**
   * Serialize the Configuration identifier to JSON Object
   *
   * @return the JSON object serialized from the configuration identifier.
   */
  private JsonObject cfgIdSerialize(CfgIdentifier cfgId) {

    Product p = pSrv.findByName(cfgId.getUserName());
    if (p == null) {
      return null;
    }

    JsonObject jCfgId = new JsonObject();
    jCfgId.addProperty("cfg_uuid", cfgId.getCfgUuid());
    jCfgId.addProperty("product_name", p.getName());
    jCfgId.addProperty("path_name", cfgId.getPathName());
    jCfgId.addProperty("target_type", cfgId.getTargetType());
    if (cfgId.getDefaultContentId() != null) {
      CfgContent content = cfgCntSrv.findById(cfgId.getDefaultContentId());
      if (content != null && !content.equals("")) {
        jCfgId.addProperty("default_content", new String(content.getContent()));
      }
    }

    if (cfgId.getSchema() != null) {
      jCfgId.addProperty("schema", cfgId.getSchema());
    }

    return jCfgId;
  }

  /**
   * Serialize the Configuration instance to JSON Object
   *
   * @return the JSON object serialized from the configuration instance.
   */
  private JsonObject cfgInstanceSerialize(CfgIdentifier cfgId, CfgInstance cfgInstance) {

    Product p = pSrv.findByName(cfgId.getUserName());
    if (p == null) {
      return null;
    }

    JsonObject jCfgInstance = new JsonObject();

    jCfgInstance.addProperty("cfg_uuid", cfgId.getCfgUuid());

    jCfgInstance.addProperty("product_name", p.getName());

    jCfgInstance.addProperty("path_name", cfgId.getPathName());

    jCfgInstance.addProperty("target_type", cfgId.getTargetType());

    if (cfgInstance.getTargetId() != null) {
      jCfgInstance.addProperty("target_id", cfgInstance.getTargetId());
    }

    if (cfgInstance.getContentId() != null) {
      CfgContent content = cfgCntSrv.findById(cfgInstance.getContentId());
      if (content != null && !content.equals("")) {
        if (content.getContentType() == 1) {
          if (content.getSharedName() != null) {
            jCfgInstance.addProperty("content_name", content.getSharedName());
            jCfgInstance.addProperty("content", new String(content.getContent()));
          }
        } else if (content.getContentType() == 2) {
          jCfgInstance.addProperty("content", new String(content.getContent()));
        }
      }
    }

    return jCfgInstance;
  }

  /**
   * Serialize the Configuration content to JSON Object
   *
   * @return the JSON object serialized from the configuration content.
   */
  private JsonObject cfgContentSerialize(CfgContent content) {

    if (content == null) {
      return null;
    }

    JsonObject jCfgContent = new JsonObject();

    if (content.getSharedName() != null) {
      jCfgContent.addProperty("shared_name", content.getSharedName());
    }
    jCfgContent.addProperty("content_hash", content.getContentHash());
    if (content.getFormatId() != null) {
      jCfgContent.addProperty("format_id", content.getFormatId());
    }
    if (content.getTag() != null) {
      jCfgContent.addProperty("tag", content.getTag());
    }
    if (content.getDescription() != null) {
      jCfgContent.addProperty("description", content.getDescription());
    }
    jCfgContent.addProperty("content", new String(content.getContent()));

    return jCfgContent;
  }

  private PostCfgIdPayload parseCfgIdPayload(String strPayload) {
    if (strPayload == null) {
      return null;
    }

    PostCfgIdPayload payload;
    Gson gson = new Gson();
    try {
      payload = gson.fromJson(strPayload, PostCfgIdPayload.class);
    } catch (JsonSyntaxException jse) {
      return null;
    }

    return payload;
  }

  /**
   * Added by WangNing
   *
   * <p>RESTful Api:// GET ams/v1/config/getClientdifferCfgs //
   */
  @RequestMapping(
      value = "/ams/v1/config/getClientdifferCfgs",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getClientCfgs(
      @RequestParam(value = "clientId", required = true) String clientId) {
    AmsClient client = amsSrv.findByClientUUID(clientId);

    if (client == null)
      return new ResponseEntity<String>(
          "Cannot find client with clientUuId: " + clientId, HttpStatus.BAD_REQUEST);

    List<ClientCfgCheckPoint> cpList = clickpSrv.findByClientId(client.getId());

    if (cpList == null)
      return new ResponseEntity<String>(
          "Cannot find client checkpoint with clientUuId: " + clientId, HttpStatus.BAD_REQUEST);

    AmsTemplate template = amsTpltSrv.findByName(client.getTemplateName());
    List<TemplateItem> tempList = null;
    if (template != null) {
      tempList = parseTemplateContent(template.getContent());
    }

    // get template_item list in project template content
    AmsTemplate proj_template = amsTpltSrv.findByName("projectId_" + client.getProjectId() + "");
    List<TemplateItem> proj_itemList = null;
    if (proj_template != null) {
      proj_itemList = parseTemplateContent(proj_template.getContent());
    }

    // get template_item list in global template content
    AmsTemplate global_template = amsTpltSrv.findByName("_global_");
    List<TemplateItem> global_itemList = null;
    if (global_template != null) {
      global_itemList = parseTemplateContent(global_template.getContent());
    }

    JsonArray jResult = new JsonArray();

    for (ClientCfgCheckPoint cp : cpList) {
      List<CfgIdentifier> idList =
          cfgIdSrv.findByUserNameAndTargetType(cp.getProductName(), cp.getTargetType());

      if (idList == null) {
        continue;
      }
      for (CfgIdentifier id : idList) {
        /**
         * priority : INSTANCE 1st; TEMPLATE 2nd; DEFAULT 3rd;
         *
         * <p>mark the state and source;
         *
         * <p>if( targetId exist ) // the ConfigData from Client include targetId { if ( the content
         * searched by targetId&ClientUuid exist) { if content.hashcode equals to the
         * configcontent.hashcode in cloud then sync_state is sync(synchronized)
         *
         * <p>if contend.hashcode doesn't equals to the config.hashcode in cloud the sync_state is
         * update(Client local should be updated) } else { goto next if (template or default); }
         *
         * <p>*** anyway, the config should always be updated to the cloud; ***
         *
         * <p>in this situation, the source is CUSTOM; }
         *
         * <p>else if (targetId is null) // { if( templates searched by clientId exist ) { loop the
         * templates ; then source is TEMPLATE; } else { goto default; source is DEFAULT; } sync is
         * NEW; }
         */
        String Src = "";
        String Sync = "";
        CfgContent content = null;

        /** Step 1: Find exact instance content */
        if (cp.getTargetId() != null && !cp.getTargetId().equals("")) {
          CfgInstance instance =
              cfgInstSrv.findByCfgIdentifierUUIDAndTargetId(id.getCfgUuid(), cp.getTargetId());
          if (instance != null && instance.getContentId() != null) {
            content = cfgCntSrv.findById(instance.getContentId());
            Src = "CUSTOM";
          }
        }

        /** Step 2: Find template content */
        if (content == null && tempList != null) {
          for (TemplateItem item : tempList) {
            boolean isFoundProduct = false;
            if (item.getProductName() != null
                && item.getProductName().equals(cp.getProductName())) {
              isFoundProduct = true;
              if (item.getCfgs() != null) {
                for (TemplateConfigItem cfgItem : item.getCfgs()) {
                  String pathName = cfgItem.getPathName();
                  pathName.trim().replaceAll("\\\\", "/");
                  if (pathName.startsWith("./")) {
                    pathName = pathName.substring(1);
                  } else {
                    if (!pathName.startsWith("/")) {
                      pathName = "/" + pathName;
                    }
                  }
                  String cfgType = cfgItem.getCfgType();
                  if (pathName.equals(id.getPathName()) && cfgType.equals(id.getTargetType())) {
                    content = cfgCntSrv.findBySharedName(cfgItem.getContentName());
                    Src = "TEMPLATE";
                    break;
                  }
                }
              }
            }
            if (isFoundProduct) {
              break;
            }
          }
        }

        /** Step 2.2: Find project default template content */
        if (content == null && global_itemList != null) {
          for (TemplateItem item : global_itemList) {
            boolean isFoundProduct = false;
            if (item.getProductName() != null
                && item.getProductName().equals(cp.getProductName())) {
              isFoundProduct = true;

              if (item.getCfgs() != null) {
                for (TemplateConfigItem cfgItem : item.getCfgs()) {
                  String pathName = cfgItem.getPathName();
                  pathName.trim().replaceAll("\\\\", "/");
                  if (pathName.startsWith("./")) {
                    pathName = pathName.substring(1);
                  } else {
                    if (!pathName.startsWith("/")) {
                      pathName = "/" + pathName;
                    }
                  }
                  String cfgType = cfgItem.getCfgType();
                  if (pathName.equals(id.getPathName()) && cfgType.equals(id.getTargetType())) {
                    content = cfgCntSrv.findBySharedName(cfgItem.getContentName());
                    Src = "TEMPLATE";
                    break;
                  }
                }
              }
            }
            if (isFoundProduct) {
              break;
            }
          }
        }

        /** Step 2.3: Find global template content */
        if (content == null && global_itemList != null) {
          for (TemplateItem item : global_itemList) {
            boolean isFoundProduct = false;
            if (item.getProductName() != null
                && item.getProductName().equals(cp.getProductName())) {
              isFoundProduct = true;

              if (item.getCfgs() != null) {
                for (TemplateConfigItem cfgItem : item.getCfgs()) {
                  String pathName = cfgItem.getPathName();
                  pathName.trim().replaceAll("\\\\", "/");
                  if (pathName.startsWith("./")) {
                    pathName = pathName.substring(1);
                  } else {
                    if (!pathName.startsWith("/")) {
                      pathName = "/" + pathName;
                    }
                  }
                  String cfgType = cfgItem.getCfgType();
                  if (pathName.equals(id.getPathName()) && cfgType.equals(id.getTargetType())) {
                    content = cfgCntSrv.findBySharedName(cfgItem.getContentName());
                    Src = "TEMPLATE";
                    break;
                  }
                }
              }
            }
            if (isFoundProduct) {
              break;
            }
          }
        }

        /** Step 3: Find default content */
        if (content == null && id.getDefaultContentId() != null) {
          content = cfgCntSrv.findById(id.getDefaultContentId());
          Src = "DEFAULT";
        }

        /**
         * Step 4: Add cfg to response only if the deployed cfg is different with the client current
         * cfg
         */
        if (content != null) {
          ClientCurrentCfg currentCfg =
              clicSrv.findByClientIdAndProductNameAndTargetTypeAndTargetIdAndPathName(
                  cp.getClientId(),
                  cp.getProductName(),
                  cp.getTargetType(),
                  cp.getTargetId(),
                  id.getPathName());

          JsonObject jElement = new JsonObject();
          if (cp.getTargetId() != "") {
            jElement.addProperty("tid", cp.getTargetId());
          }
          jElement.addProperty("pn", id.getUserName());
          jElement.addProperty("tt", cp.getTargetType());
          jElement.addProperty("path", id.getPathName());
          jElement.addProperty("id", content.getId().toString());
          /** CUSTOM: the hash in ClientCurrentCfg is equals content.hash */
          if (currentCfg != null && currentCfg.getHash().equals(content.getContentHash())) {
            Sync = "sync";
          } else if (currentCfg != null && !currentCfg.getHash().equals(content.getContentHash())) {
            Sync = "update";
          } else {
            Sync = "new";
          }

          jElement.addProperty("Sync", Sync);
          jElement.addProperty("Src", Src);

          jResult.add(jElement);
        }
      }
    }
    return new ResponseEntity<String>(jResult.toString(), HttpStatus.OK);
  }

  // end

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

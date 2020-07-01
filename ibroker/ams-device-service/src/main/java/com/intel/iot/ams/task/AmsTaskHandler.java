/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.entity.AmsTask;
import com.intel.iot.ams.entity.AmsTemplate;
import com.intel.iot.ams.entity.Product;
import com.intel.iot.ams.entity.ProductChanges;
import com.intel.iot.ams.entity.ProductDeploy;
import com.intel.iot.ams.entity.ProductDownloadPackage;
import com.intel.iot.ams.entity.ProductInstalled;
import com.intel.iot.ams.entity.ProductInstance;
import com.intel.iot.ams.requestbody.CalculateChangesProperty;
import com.intel.iot.ams.requestbody.ProductInstanceManifest;
import com.intel.iot.ams.requestbody.TemplateItem;
import com.intel.iot.ams.requestbody.ProductInstanceManifest.ComponentInfo;
import com.intel.iot.ams.service.AmsClientService;
import com.intel.iot.ams.service.AmsTaskService;
import com.intel.iot.ams.service.AmsTemplateService;
import com.intel.iot.ams.service.ProductChangesService;
import com.intel.iot.ams.service.ProductDeployService;
import com.intel.iot.ams.service.ProductDownloadPackageService;
import com.intel.iot.ams.service.ProductInstalledService;
import com.intel.iot.ams.service.ProductInstanceService;
import com.intel.iot.ams.service.ProductService;
import com.intel.iot.ams.service.ServiceBundle;
import com.intel.iot.ams.utils.AmsConstant;
import com.intel.iot.ams.utils.HashUtils;
import com.intel.iot.ams.utils.TarUtils;
import com.intel.iot.ams.utils.Utils;

public class AmsTaskHandler implements Runnable {

  private AmsTaskService taskSrv;
  private AmsClientService clientSrv;
  private ProductService productSrv;
  private ProductChangesService changeSrv;
  private ProductDeployService deploySrv;
  private ProductDownloadPackageService pkgSrv;
  private ProductInstanceService instanceSrv;
  private ProductInstalledService installSrv;
  private AmsTemplateService tempSrv;
  private int downloadPerMinute;

  private static Logger logger = Logger.getLogger(AmsTaskHandler.class);

  public AmsTaskHandler() {
    this.taskSrv = ServiceBundle.getInstance().getTaskSrv();
    this.clientSrv = ServiceBundle.getInstance().getClientSrv();
    this.productSrv = ServiceBundle.getInstance().getProductSrv();
    this.changeSrv = ServiceBundle.getInstance().getProductChangeSrv();
    this.deploySrv = ServiceBundle.getInstance().getProductDeploySrv();
    this.pkgSrv = ServiceBundle.getInstance().getProductPkgSrv();
    this.instanceSrv = ServiceBundle.getInstance().getProductInstanceSrv();
    this.installSrv = ServiceBundle.getInstance().getProductInstalledSrv();
    this.tempSrv = ServiceBundle.getInstance().getTemplateSrv();
    this.downloadPerMinute = getDownladProperty();
  }

  public void run() {

    while (true) {
      try {
        boolean taskDone = false;

        /** Fetch the top AMS task */
        AmsTask task = taskSrv.getTopTask();
        if (task == null) {
          Thread.sleep(5000);
        } else {
          switch (task.getTaskType()) {
            case AmsTaskType.CALCULATE_PRODUCT_CHANGES: {
              taskDone = handleCalculateChanges(task.getTaskProperties());
              break;
            }
            default:
              break;
          }
          if (taskDone) {
            taskSrv.delete(task);
          }
        }

      } catch (InterruptedException e) {
        e.printStackTrace();
        logger.warn(Utils.getStackTrace(e));
      } catch (Exception e) {
        e.printStackTrace();
        logger.warn(Utils.getStackTrace(e));
      }
    }
  }

  private boolean handleCalculateChanges(String propStr) {

    if (propStr == null) {
      return false;
    }

    /** parse properties */
    CalculateChangesProperty prop;
    Gson gson = new Gson();
    try {
      prop = gson.fromJson(propStr, CalculateChangesProperty.class);
    } catch (JsonSyntaxException jse) {
      return false;
    }
    if (prop == null || prop.getClientUuid() == null) {
      return false;
    }

    AmsClient client = clientSrv.findByClientUUID(prop.getClientUuid());
    if (client == null) {
      return false;
    }

    /**
      if client's prodcut lock is true, should not calculate product change
      and old product changes items should be deleted
     */
    if ( client.getProductLock() != null && client.getProductLock()){
      logger.info("==> Client:"+client.getClientUuid()+" has been locked, can not calculate prodcut changes");
      // delete old product changes
      changeSrv.removeByClientUuid(client.getClientUuid());
      // delete the task item
      return true;
    }

    /** Calculate product changes of this client */

    /** calculate changes of install/update */
    List<ProductDeploy> deploys = findAllDeploy(client.getClientUuid());
    if (deploys != null) {
      for (ProductDeploy deploy : deploys) {
        Product p = productSrv.findByName(deploy.getProductName());
        if (p == null) {
          continue;
        }

        ProductInstance pDeployInstance =
            findInstanceForClient(p.getName(), deploy.getVersion(), p.getCategory(), client);
        ProductInstance pInstalledInstance = null;
        if (pDeployInstance != null) {
          ProductInstalled install = null;
          if (!deploy.getProductName().equals("ams_client") && p.getCategory() != 2) {
            install = installSrv.findByClientUuidAndProductName(client.getClientUuid(),
                                                                deploy.getProductName());
            if (install != null) {
              pInstalledInstance =
                  findInstanceForClient(p.getName(), install.getVersion(), p.getCategory(), client);
            }
          }

          String productName = pDeployInstance.getProductName();
          Integer fromId = (pInstalledInstance == null ? null : pInstalledInstance.getInstanceId());
          Integer toId = pDeployInstance.getInstanceId();

          /**
           * If the deploy has already installed, delete the changes, then continue
           */
          if (fromId != null && fromId.intValue() == toId.intValue()) {
            if (p.getCategory() != 5 || deploy.getIsAot().equals(install.getIsAot())) {
              changeSrv.removeByClientUuidAndProductName(client.getClientUuid(), p.getName());
              continue;
            }
          }

          if (productName.equals("ams_client")) {
            if (pDeployInstance.getVersion().equals(client.getAmsClientVersion())) {
              changeSrv.removeByClientUuidAndProductName(client.getClientUuid(), p.getName());
              continue;
            }
          }

          /** FW product */
          if (p.getCategory() == 2) {
            if (pDeployInstance.getVersion().equals(client.getFwVersion())) {
              changeSrv.removeByClientUuidAndProductName(client.getClientUuid(), p.getName());
              continue;
            }
          }

          /** Find if the download package has already been created */
          ProductDownloadPackage pack = null;
          if (p.getCategory() == 2 || p.getCategory() == 4) {
            /**
             * FW product and iMRT app package (BPK file) does not support incremental mode
             */
            pack = pkgSrv.findByProductNameAndFromIdAndToId(productName, null, toId);
          } else if (p.getCategory() == 5) {
            pack = pkgSrv.findByProductNameAndFromIdAndToIdAndIsAot(productName,
                                                                    null,
                                                                    toId,
                                                                    deploy.getIsAot());
          } else {
            pack = pkgSrv.findByProductNameAndFromIdAndToId(productName, fromId, toId);
          }

          /** Create the download package if it is not existed */
          if (pack == null) {
            boolean isAot = true;
            if (deploy.getIsAot() == null || !deploy.getIsAot().booleanValue()) {
              isAot = false;
            }
            pack = createUpdatePkg(productName, fromId, toId, isAot);
          }

          /**
           * Only when the download package is existed or created successful, save the change into
           * DB
           */
          if (pack != null) {
            /** Delete the old change and then create new change */
            changeSrv.removeByClientUuidAndProductName(client.getClientUuid(), p.getName());

            Date currentTimeslot = getCurrentTimeslot();
            int countOfCurrentTimeslot = changeSrv.getEnableTimeCount(currentTimeslot);

            ProductChanges change = new ProductChanges();
            change.setClientUuid(client.getClientUuid());
            change.setProductName(p.getName());
            change.setDownloadId(pack.getId().toString());

            if (countOfCurrentTimeslot < this.downloadPerMinute) {
              change.setEnableTime(currentTimeslot);
            } else {
              // if reach the limit count, then enable time add 1 miniuts
              change.setEnableTime(this.getTimeAddMinutes(currentTimeslot, 1));
            }
            changeSrv.save(change);
          }
        }
      }
    }

    /** calculate changes of delete */
    List<ProductInstalled> installs = installSrv.findByClientUUID(client.getClientUuid());
    if (installs != null) {
      for (ProductInstalled install : installs) {
        /**
         * If the product has removed from repo or the product is not in deploy list, then remove it
         * from client. ams_client is exceptional.
         */
        if (install.getProductName().equals("ams_client")) {
          continue;
        }

        Product p = productSrv.findByName(install.getProductName());

        ProductDeploy deploy = deploySrv.findByClientUuidAndProductName(install.getClientUuid(),
                                                                        install.getProductName());
        ProductDeploy tempDeploy =
            findTemplateDeploy(install.getClientUuid(), install.getProductName());

        /**
         * If product is not in AMS repository or neither in product deploy list or in template
         * deploy list, then delete the product from client
         */
        if (p == null || (deploy == null && tempDeploy == null)) {
          /** Delete the old change */
          changeSrv.removeByClientUuidAndProductName(client.getClientUuid(),
                                                     install.getProductName());

          /** Create new change of deleting the installed product */
          ProductChanges change = new ProductChanges();
          change.setClientUuid(client.getClientUuid());
          change.setProductName(install.getProductName());
          changeSrv.save(change);
        }
      }
    }

    /**
     * delete the product which is neither in Deploy (product deploy and template deploy) nor in
     * Installed from product changes, i.e. when a product is deleted from client, it is not in
     * Deploy and Installed, but it still in product changes table, we should delete it
     */

    List<ProductChanges> changes = changeSrv.findByClientUUID(client.getClientUuid());
    if (changes != null) {
      for (ProductChanges change : changes) {
        if (change.getDownloadId() != null) {
          continue;
        }

        boolean delFlag = true;
        if (deploys != null) {
          for (ProductDeploy deploy : deploys) {
            if (change.getProductName().equals(deploy.getProductName())) {
              delFlag = false;
              break;
            }
          }
        }

        if (delFlag == false) {
          continue;
        }

        if (installs != null) {
          for (ProductInstalled install : installs) {
            if (change.getProductName().equals(install.getProductName())) {
              delFlag = false;
              break;
            }
          }
        }

        if (delFlag == true) {
          changeSrv.delete(change);
        }
      }
    }

    return true;
  }

  private ProductDownloadPackage createUpdatePkg(String productName, Integer fromId, Integer toId,
                                                 boolean isAot) {

    if (productName == null || toId == null) {
      return null;
    }

    Product p = productSrv.findByName(productName);
    if (p == null) {
      return null;
    }

    String pkgHash = null;
    long pkgSize = 0;
    String pkgFormat = null;
    String toDir = null;
    ProductInstance to = instanceSrv.findById(toId);
    if (to == null) {
      return null;
    }

    if (p.getCategory() == 2) {
      pkgHash = createFwPkg(p, to);
      if (pkgHash == null) {
        return null;
      }
      pkgSize = new File(AmsConstant.downloadPath + pkgHash + ".fw").length();
      pkgFormat = "fw";
    } else if (p.getCategory() == 4) {
      toDir = AmsConstant.repoPath + p.getName() + "/" + to.getVersion() + ".bpk";
      pkgHash = createFullBpk(toDir);
      if (pkgHash == null) {
        return null;
      }
      pkgSize = new File(AmsConstant.downloadPath + pkgHash + ".bpk").length();
      pkgFormat = "bpk";
    } else if (p.getCategory() == 5) {
      String postfix = null;
      if (isAot) {
        postfix = ".aot";
        pkgFormat = "aot";
      } else {
        postfix = ".wasm";
        pkgFormat = "wasm";
      }
      pkgHash = createFwAppPkg(p, to, isAot);
      if (pkgHash == null) {
        return null;
      }
      pkgSize = new File(AmsConstant.downloadPath + pkgHash + postfix).length();
    } else {
      toDir = AmsConstant.repoPath + p.getName() + "/" + to.getVersion() + "/"
          + to.getInstanceName() + "/";
      ProductInstance from = null;
      if (fromId != null) {
        from = instanceSrv.findById(fromId);
      }

      if (from == null) {
        pkgHash = createFullTarGzPkg(toDir, productName);
      } else {
        String fromDir = null;
        if (p.getCategory() == 4) {
          fromDir = AmsConstant.repoPath + p.getName() + "/" + from.getVersion();
        } else {
          fromDir = AmsConstant.repoPath + p.getName() + "/" + from.getVersion() + "/"
              + from.getInstanceName() + "/";
        }
        pkgHash = createIncrementalTarGzPkg(fromDir, toDir, productName);
      }
      if (pkgHash == null) {
        return null;
      }
      /*
       * pkgSize = new File(AmsConstant.downloadPath + pkgHash + ".zip").length(); pkgFormat =
       * "zip";
       */
      pkgSize = new File(AmsConstant.downloadPath + pkgHash + ".tar.gz").length();
      pkgFormat = "tar.gz";
    }

    ProductDownloadPackage download = new ProductDownloadPackage();
    download.setProductName(p.getName());
    download.setCategory(p.getCategory());
    if (fromId != null) {
      download.setFromId(fromId);
    }
    download.setToId(toId);
    download.setGenDate(new Date());
    download.setHashcode(pkgHash);
    download.setSize(new Long(pkgSize));
    download.setFormat(pkgFormat);
    download.setLastUsedTime(new Date());
    if (p.getCategory() == 5) {
      download.setIsAot(isAot);
    }

    pkgSrv.save(download);

    return download;
  }

  private String createFullBpk(String toDirStr) {
    if (toDirStr == null) {
      return null;
    }

    String pkgHash = null;
    String tempBpkPath = AmsConstant.tempPath + String.valueOf(new Date().getTime()) + ".bpk";
    File tempBpk = new File(tempBpkPath);
    try {
      FileUtils.copyFile(new File(toDirStr), tempBpk);
      if (!tempBpk.exists()) {
        return null;
      }
      pkgHash = HashUtils.getMd5Hash(FileUtils.readFileToByteArray(tempBpk));
      String destDirStr = AmsConstant.downloadPath + pkgHash + ".bpk";
      File bpk = new File(destDirStr);
      if (!bpk.exists()) {
        FileUtils.moveFile(tempBpk, bpk);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    return pkgHash;
  }

  private String createFullTarGzPkg(String toDirStr, String productName) {
    if (toDirStr == null || productName == null) {
      return null;
    }

    String pkgHash = null;

    Gson gson = new Gson();
    ProductInstanceManifest meta = null;

    try {
      String metaStr = FileUtils.readFileToString(new File(toDirStr + "/ams/version/manifest"));
      meta = gson.fromJson(metaStr, ProductInstanceManifest.class);

      if (meta == null) {
        return null;
      }

      /** Create temp dir */
      String tempName = String.valueOf(new Date().getTime());
      String tempDirStr = AmsConstant.tempPath + tempName;
      File tempDir = new File(tempDirStr);
      if (!tempDir.exists()) {
        tempDir.mkdir();
      }

      FileUtils.copyDirectory(new File(toDirStr), tempDir);

      /** Create .tar.gz file */
      String tarPath = AmsConstant.tempPath + tempName + ".tar";
      String tarGzPath = AmsConstant.tempPath + tempName + ".tar.gz";

      new TarUtils().execute(tempDirStr, tarPath, productName);

      File tarFile = new File(tarPath);
      if (!tarFile.exists()) {
        return null;
      } else {
        tarFile.delete(); // Delete the .tar file, preserve the .tar.gz
                          // file
      }

      File tarGzFile = new File(tarGzPath);
      if (!tarGzFile.exists()) {
        return null;
      }

      pkgHash = HashUtils.getMd5Hash(FileUtils.readFileToByteArray(tarGzFile));
      String destDirStr = AmsConstant.downloadPath + pkgHash + ".tar.gz";

      File destTarGz = new File(destDirStr);
      if (!destTarGz.exists()) {
        FileUtils.moveFile(tarGzFile, destTarGz);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } catch (JsonSyntaxException jse) {
      return null;
    }

    return pkgHash;
  }

  private String createIncrementalTarGzPkg(String fromDirStr, String toDirStr, String productName) {
    if (fromDirStr == null || toDirStr == null) {
      return null;
    }
    String pkgHash = null;

    Gson gson = new Gson();
    ProductInstanceManifest fromMeta = null;
    ProductInstanceManifest toMeta = null;

    try {
      String fromMetaStr =
          FileUtils.readFileToString(new File(fromDirStr + "/ams/version/manifest"));
      String toMetaStr = FileUtils.readFileToString(new File(toDirStr + "/ams/version/manifest"));
      fromMeta = gson.fromJson(fromMetaStr, ProductInstanceManifest.class);
      toMeta = gson.fromJson(toMetaStr, ProductInstanceManifest.class);

      if (fromMeta == null || toMeta == null || fromMeta.getComponentList() == null
          || toMeta.getComponentList() == null) {
        return null;
      }

      /** Create temp dir */
      String tempName = String.valueOf(new Date().getTime());
      String tempDirStr = AmsConstant.tempPath + tempName + "/";
      File tempDir = new File(tempDirStr);
      if (!tempDir.exists()) {
        tempDir.mkdir();
      }

      /** Prepare the incremental files */

      // FileUtils.copyDirectory(new File(toDirStr), tempDir);

      Hashtable<String, String> fromCom = new Hashtable<String, String>();
      Hashtable<String, String> toCom = new Hashtable<String, String>();

      for (ComponentInfo info : fromMeta.getComponentList()) {
        fromCom.put(info.getPathName(), info.getHash());
      }

      for (ComponentInfo info : toMeta.getComponentList()) {
        toCom.put(info.getPathName(), info.getHash());
      }

      for (String key : toCom.keySet()) {
        String toHash = toCom.get(key);
        String fromHash = fromCom.get(key);
        if (fromHash == null || !toHash.equals(fromHash)) {
          FileUtils.copyFile(new File(toDirStr + "/" + key), new File(tempDirStr + "/" + key));
        }
      }

      /** Copy manifest file */
      FileUtils.copyFile(new File(toDirStr + "/ams/version/manifest"),
                         new File(tempDirStr + "/ams/version/manifest"));

      /** Create .tar.gz file */
      String tarPath = AmsConstant.tempPath + tempName + ".tar";
      String tarGzPath = AmsConstant.tempPath + tempName + ".tar.gz";

      new TarUtils().execute(tempDirStr, tarPath, productName);

      File tarFile = new File(tarPath);
      if (!tarFile.exists()) {
        return null;
      } else {
        tarFile.delete(); // Delete the .tar file, preserve the .tar.gz
                          // file
      }

      File tarGzFile = new File(tarGzPath);
      if (!tarGzFile.exists()) {
        return null;
      }

      pkgHash = HashUtils.getMd5Hash(FileUtils.readFileToByteArray(tarGzFile));
      String destDirStr = AmsConstant.downloadPath + pkgHash + ".tar.gz";

      File destTarGz = new File(destDirStr);
      if (!destTarGz.exists()) {
        FileUtils.moveFile(tarGzFile, destTarGz);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } catch (JsonSyntaxException jse) {
      return null;
    }

    return pkgHash;
  }

  private ProductInstance findInstanceForClient(String productName, String version, int category,
                                                AmsClient client) {

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
          instanceSrv.findByNameAndVersionAndCpuAndPlatformAndOs(productName,
                                                                 version,
                                                                 client.getCpu(),
                                                                 null,
                                                                 null);
      return instance;
    }

    if (category == 4) {
      ProductInstance instance = instanceSrv.findByNameAndVersionAndCpuAndPlatformAndOs(productName,
                                                                                        version,
                                                                                        null,
                                                                                        null,
                                                                                        null);
      return instance;
    }

    /** the product is fw_app_wasm */
    if (category == 5) {
      ProductInstance instance = instanceSrv.findByNameAndVersionAndCpuAndPlatformAndOs(productName,
                                                                                        version,
                                                                                        null,
                                                                                        null,
                                                                                        null);
      return instance;
    }

    tempList =
        instanceSrv.findByNameAndVersionAndCpuAndPlatformAndOsAndSystemAndBits(productName,
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
          instanceSrv.findByNameAndVersionAndCpuAndPlatformAndOsAndSystemAndBits(productName,
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

    tempList = instanceSrv.findByNameAndVersionAndCpuAndPlatformAndOsAndBits(productName,
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
      tempList = instanceSrv.findByNameAndVersionAndCpuAndPlatformAndOsAndBits(productName,
                                                                               version,
                                                                               client.getCpu(),
                                                                               client.getPlatform(),
                                                                               client.getOs(),
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

    tempList = instanceSrv.findByNameAndVersionAndCpu(productName, version, "ANY");
    if (tempList != null) {
      return tempList.get(0);
    }

    tempList = instanceSrv.findByNameAndVersionAndCpu(productName, version, "Any");
    if (tempList != null) {
      return tempList.get(0);
    }

    tempList = instanceSrv.findByNameAndVersionAndCpu(productName, version, "any");
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

  private List<ProductDeploy> findAllDeploy(String clientUuid) {
    List<ProductDeploy> all = null;
    List<ProductDeploy> deploys = null;

    String tempName = null;
    String prjId = null;
    List<ProductDeploy> templateDeploys = null;

    AmsClient client = clientSrv.findByClientUUID(clientUuid);
    if (client == null) {
      return null;
    }

    tempName = client.getTemplateName();
    deploys = deploySrv.findByClientUUID(clientUuid);
    prjId = client.getProjectId();

    //1. find customized template
    templateDeploys = findTemplateDeployList(clientUuid, tempName);
    if(templateDeploys == null) {
      //2. find project default template
      templateDeploys = findTemplateDeployList(clientUuid,"projectId_"+ prjId +"");
    }
    if(templateDeploys == null){
      //3. find _global_ template
      templateDeploys = findTemplateDeployList(clientUuid, "_global_");
    }

    if (deploys == null && templateDeploys == null) {
      return null;
    }

    if (deploys != null && templateDeploys == null) {
      return deploys;
    }

    if (deploys == null && templateDeploys != null) {
      return templateDeploys;
    }

    all = new ArrayList<ProductDeploy>();
    all.addAll(deploys);

    for (ProductDeploy deploy : templateDeploys) {
      boolean isDup = false;
      /**
       * If it already has this product in the list, it should ignore the duplicated one
       */
      for (ProductDeploy temp : all) {
        if (temp.getProductName().equals(deploy.getProductName())) {
          isDup = true;
          break;
        }
      }
      if (!isDup) {
        all.add(deploy);
      }
    }

    return all;
  }

  private List<ProductDeploy> findTemplateDeployList(String clientUuid, String tempName) {
    if (clientUuid == null || tempName == null) {
      return null;
    }

    List<ProductDeploy> templateDeploys = null;
    AmsTemplate template = tempSrv.findByName(tempName);
    if (template == null) {
      return null;
    }

    List<TemplateItem> itemList = parseTemplateContent(template.getContent());
    if (itemList != null) {
      templateDeploys = new ArrayList<ProductDeploy>();
      for (TemplateItem item : itemList) {
        boolean isDup = false;
        /**
         * If it already has this product in the list, ignore the duplicated one
         */
        for (ProductDeploy temp : templateDeploys) {
          if (temp.getProductName().equals(item.getProductName())) {
            isDup = true;
            break;
          }
        }
        if (!isDup) {
          ProductDeploy deploy = new ProductDeploy();
          deploy.setClientUuid(clientUuid);
          deploy.setProductName(item.getProductName());
          deploy.setVersion(item.getVersion());
          templateDeploys.add(deploy);
        }
      }
    }

    return templateDeploys;
  }

  private ProductDeploy findTemplateDeploy(String clientUuid, String productName) {
    if (clientUuid == null || productName == null) {
      return null;
    }

    ProductDeploy deploy = null;
    List<ProductDeploy> DeployTmpList=null;
   
    AmsClient client = clientSrv.findByClientUUID(clientUuid);
    if (client == null) {
      return null;
    }
    /* If no product & template deploy to this client, use product in project template
     * if project default template is null, use global template
     */

      /** find customized template */
      DeployTmpList = findTemplateDeployList(clientUuid, client.getTemplateName());

      /** find this client's project template */
     if(DeployTmpList == null){
        DeployTmpList = findTemplateDeployList(clientUuid, "projectId_"+client.getProjectId()+"");
     }

      /** find this client's global template */
     if(DeployTmpList == null){
        DeployTmpList = findTemplateDeployList(clientUuid, "_global_");
     }

     if(DeployTmpList != null){
       for(ProductDeploy prtDpl : DeployTmpList){
         if(prtDpl.getProductName().equals(productName)){
            deploy=new ProductDeploy();
            deploy.setClientUuid(clientUuid);
            deploy.setProductName(prtDpl.getProductName());
            deploy.setVersion(prtDpl.getVersion());
            break;
         }
       }
     }

    return deploy;
  }

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

  private Date getCurrentTimeslot() {
    Date maxEnableTime = changeSrv.getMaxEnableTime();
    if (maxEnableTime == null) {
      Calendar temp = Calendar.getInstance();
      temp.set(Calendar.SECOND, 0);
      temp.set(Calendar.MILLISECOND, 0);
      maxEnableTime = temp.getTime();
    }
    return maxEnableTime;
  }

  private Date getTimeAddMinutes(Date time, int addMinuts) {
    Calendar temp = Calendar.getInstance();
    temp.setTime(time);
    temp.add(Calendar.MINUTE, addMinuts);

    return temp.getTime();
  }

  private int getDownladProperty() {
    int limit = 5; // default value is 5
    try {
      Properties properties = new Properties();
      InputStream in = AmsTaskHandler.class.getClassLoader()
                                           .getResourceAsStream("resources/application.properties");
      if (in == null) {
        in = AmsTaskHandler.class.getClassLoader().getResourceAsStream("application.properties");
      }
      properties.load(in);
      String value = properties.getProperty("ams.download_per_minute");
      limit = Integer.valueOf(value);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return limit;
  }

  private String createFwPkg(Product p, ProductInstance to) {

    String toDir = AmsConstant.repoPath + p.getName() + "/" + to.getVersion() + "/"
        + to.getInstanceName() + "/bin/" + p.getName() + ".fw";

    String pkgHash = null;
    String tempFwPath = AmsConstant.tempPath + String.valueOf(new Date().getTime()) + ".fw";
    File tempFw = new File(tempFwPath);
    try {
      FileUtils.copyFile(new File(toDir), tempFw);
      if (!tempFw.exists()) {
        return null;
      }
      pkgHash = HashUtils.getMd5Hash(FileUtils.readFileToByteArray(tempFw));
      String destDirStr = AmsConstant.downloadPath + pkgHash + ".fw";
      File fw = new File(destDirStr);
      if (!fw.exists()) {
        FileUtils.moveFile(tempFw, fw);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    return pkgHash;
  }

  private String createFwAppPkg(Product p, ProductInstance to, boolean isAot) {

    String postfix = null;

    if (isAot) {
      postfix = ".aot";
    } else {
      postfix = ".wasm";
    }
    String toDir = AmsConstant.repoPath + p.getName() + "/" + to.getVersion() + "/fw_app_wasm/bin/"
        + p.getName() + ".wasm";

    String pkgHash = null;
    String tempFwAppPath = AmsConstant.tempPath + String.valueOf(new Date().getTime()) + postfix;
    File tempFwApp = new File(tempFwAppPath);
    try {
      // TODO: just cp wasm file to aot file
      FileUtils.copyFile(new File(toDir), tempFwApp);
      if (!tempFwApp.exists()) {
        return null;
      }
      pkgHash = HashUtils.getMd5Hash(FileUtils.readFileToByteArray(tempFwApp));
      String destDirStr = AmsConstant.downloadPath + pkgHash + postfix;
      File fwApp = new File(destDirStr);
      if (!fwApp.exists()) {
        FileUtils.moveFile(tempFwApp, fwApp);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    return pkgHash;
  }


}

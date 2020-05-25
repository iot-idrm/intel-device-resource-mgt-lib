/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intel.iot.ams.AmsUserCloudMain;
import com.intel.iot.ams.api.requestbody.*;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.security.ApiJwtTokenUtil;
import com.intel.iot.ams.service.AmsClientService;
import com.intel.iot.ams.service.ProductInstanceService;
import com.intel.iot.ams.service.ProductService;
import com.intel.iot.ams.utils.AmsConstant;
import java.io.File;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AmsUserCloudMain.class})
@Ignore
public class AmsApiHttpClientTest {

  private boolean requireAuth = true;

  private String token = null;

  @Autowired private ApiJwtTokenUtil jwtUtil;

  @Autowired private AmsClientService clientSrv;

  @Autowired private ProductService productSrv;

  @Autowired private ProductInstanceService piSrv;

  AmsClient client1 = null;

  @Value("${jwt.header}")
  private String tokenHeader;

  @Value("${jwt.tokenHead}")
  private String tokenHead;

  @Before
  public void init() {
    // Create a client in DB
    client1 = new AmsClient();
    client1.setSerial("abcdefg001");
    client1.setClientUuid("abcdefg001");
    client1.setAmsClientVersion("1.0");
    client1.setBits("64bit");
    client1.setCpu("IA");
    client1.setDescription("test client 1");
    client1.setDeviceName("dev001");
    // client1.setDeviceType("");
    client1.setOs("Linux");
    client1.setOsVer("6.0.0");
    // client1.setPlatform("");
    client1.setProductLock(false);
    client1.setProvisionTime(new Date());
    client1.setSystem("Ubuntu");
    client1.setSysVer("16.04");
    // client1.setTemplateName("");
    clientSrv.save(client1);

    // Create a token
    if (requireAuth) {
      token = jwtUtil.generateToken(null, "apitest");
    }
  }

  @Test
  public void testProduct1() throws Exception {

    // Clear DB
    productSrv.removeByName("iagent");
    piSrv.removeByName("iagent");
    FileUtils.deleteDirectory(new File(AmsConstant.repoPath + "iagent"));

    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    CloseableHttpResponse httpResponse = null;
    RequestConfig requestConfig =
        RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000000).build();

    // 1. Upload a product package
    String classPath = this.getClass().getResource("/").getPath();
    String filePath = classPath + "iagent.zip";
    File file = new File(filePath);
    if (!file.exists()) {
      System.out.println("cannot find file: " + filePath);
      return;
    }

    String productUploadUrl = "http://localhost:8080/ams/v1/product/upload";
    HttpPost httpPost = new HttpPost(productUploadUrl);
    httpPost.setConfig(requestConfig);
    if (requireAuth) {
      httpPost.setHeader(tokenHeader, tokenHead + token);
    }
    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
    multipartEntityBuilder.addBinaryBody("file", file);
    HttpEntity httpEntity = multipartEntityBuilder.build();
    httpPost.setEntity(httpEntity);
    httpResponse = httpClient.execute(httpPost);

    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 2. Query product with product name
    String productName = "iagent";
    String queryStr = "name=" + productName;
    String productUrl = "http://localhost:8080/ams/v1/product";
    String productQueryUrl = String.format("%s?%s", productUrl, queryStr);
    HttpGet getProductInfo = new HttpGet(productQueryUrl);
    getProductInfo.setConfig(requestConfig);
    if (requireAuth) {
      getProductInfo.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(getProductInfo);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 3. Update product information
    String productUpdateUrl = "http://localhost:8080/ams/v1/product";
    UpdateProductInfo payload = new UpdateProductInfo();
    payload.setProductName("iagent");
    payload.setDescription("iagent product for AMS test!");
    HttpPost postProductInfo = new HttpPost(productUpdateUrl);
    postProductInfo.setConfig(requestConfig);
    if (requireAuth) {
      postProductInfo.setHeader(tokenHeader, tokenHead + token);
    }
    StringEntity entity = new StringEntity(new ObjectMapper().writeValueAsString(payload));
    entity.setContentType("application/json");
    postProductInfo.setEntity(entity);

    httpResponse = httpClient.execute(postProductInfo);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 4. Query product again to verify the update

    httpResponse = httpClient.execute(getProductInfo);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 5. Deploy product to client
    String productDeployUrl = "http://localhost:8080/ams/v1/product/deploy";
    AddProductForClientInfo deployPayload = new AddProductForClientInfo();
    deployPayload.setClientUuid("abcdefg001");
    deployPayload.setProductName("iagent");
    deployPayload.setVersion("1.0");
    deployPayload.setCategory("software_product");

    HttpPost postProductDeploy = new HttpPost(productDeployUrl);
    postProductDeploy.setConfig(requestConfig);
    if (requireAuth) {
      postProductDeploy.setHeader(tokenHeader, tokenHead + token);
    }

    StringEntity deployEntity =
        new StringEntity(new ObjectMapper().writeValueAsString(deployPayload));
    deployEntity.setContentType("application/json");
    postProductDeploy.setEntity(deployEntity);

    httpResponse = httpClient.execute(postProductDeploy);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 6. Query product deployment of client
    String queryProductDeployUrl =
        String.format("%s?%s", productDeployUrl, "client_uuid=abcdefg001");
    HttpGet getProductDeploy = new HttpGet(queryProductDeployUrl);
    getProductDeploy.setConfig(requestConfig);
    if (requireAuth) {
      getProductDeploy.setHeader(tokenHeader, tokenHead + token);
    }

    httpResponse = httpClient.execute(getProductDeploy);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 7. Delete product deploy
    String delProductDeployUrl =
        String.format(
            "%s?%s", productDeployUrl, "client_uuid=abcdefg001&product_name=" + productName);
    HttpDelete deleteDeploy = new HttpDelete(delProductDeployUrl);
    deleteDeploy.setConfig(requestConfig);
    if (requireAuth) {
      deleteDeploy.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(deleteDeploy);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 8. Delete product
    String delProductUrl = String.format("%s?%s", productUrl, "name=" + productName);
    HttpDelete deleteProduct = new HttpDelete(delProductUrl);
    deleteProduct.setConfig(requestConfig);
    if (requireAuth) {
      deleteProduct.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(deleteProduct);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();
    httpClient.close();
  }

  @Test
  public void testClient1() throws Exception {
    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    CloseableHttpResponse httpResponse = null;
    RequestConfig requestConfig =
        RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000000).build();

    // 1. Create a new Client
    AmsClient client = new AmsClient();
    client.setSerial("abcdefg002");
    client.setClientUuid("abcdefg002");
    client.setAmsClientVersion("1.0");
    client.setBits("64bit");
    client.setCpu("IA");
    client.setDescription("test client 2");
    client.setDeviceName("dev002");
    // client.setDeviceType("");
    client.setOs("Linux");
    client.setOsVer("6.0.0");
    // client.setPlatform("");
    client.setProductLock(false);
    client.setProvisionTime(new Date());
    client.setSystem("Ubuntu");
    client.setSysVer("16.04");
    // client.setTemplateName("");
    clientSrv.save(client);

    // 2. Query client information
    String clientUrl = "http://localhost:8080/ams/v1/ams_client";
    String queryClientUrl = String.format("%s?%s", clientUrl, "client_uuid=abcdefg002");
    HttpGet getClientInfo = new HttpGet(queryClientUrl);
    getClientInfo.setConfig(requestConfig);
    if (requireAuth) {
      getClientInfo.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(getClientInfo);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 3. Update the client information
    UpdateAmsClientInfo updateClientPayload = new UpdateAmsClientInfo();
    updateClientPayload.setClientUuid("abcdefg002");
    updateClientPayload.setDescription("test client 2 - dev002");
    HttpPost postClientInfo = new HttpPost(clientUrl);
    postClientInfo.setConfig(requestConfig);
    if (requireAuth) {
      postClientInfo.setHeader(tokenHeader, tokenHead + token);
    }
    StringEntity entity =
        new StringEntity(new ObjectMapper().writeValueAsString(updateClientPayload));
    entity.setContentType("application/json");
    postClientInfo.setEntity(entity);
    httpResponse = httpClient.execute(postClientInfo);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 4. query client information and verify the update info
    httpResponse = httpClient.execute(getClientInfo);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 5. Delete the client
    String delClientUrl = String.format("%s?%s", clientUrl, "client_uuid=abcdefg002");
    HttpDelete deleteClient = new HttpDelete(delClientUrl);
    deleteClient.setConfig(requestConfig);
    if (requireAuth) {
      deleteClient.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(deleteClient);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
    // 6. Query client to make sure the client is deleted
    httpResponse = httpClient.execute(getClientInfo);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);

    httpResponse.close();
    httpClient.close();
  }

  @Test
  public void testCfg1() throws Exception {

    // Clear DB
    productSrv.removeByName("iagent");
    piSrv.removeByName("iagent");
    FileUtils.deleteDirectory(new File(AmsConstant.repoPath + "iagent"));

    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    CloseableHttpResponse httpResponse = null;
    RequestConfig requestConfig =
        RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000000).build();

    // 1. Upload a product package
    String classPath = this.getClass().getResource("/").getPath();
    String filePath = classPath + "iagent.zip";
    File file = new File(filePath);
    if (!file.exists()) {
      System.out.println("cannot find file: " + filePath);
      return;
    }
    String productUploadUrl = "http://localhost:8080/ams/v1/product/upload";

    HttpPost httpPost = new HttpPost(productUploadUrl);
    httpPost.setConfig(requestConfig);
    if (requireAuth) {
      httpPost.setHeader(tokenHeader, tokenHead + token);
    }
    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
    multipartEntityBuilder.addBinaryBody("file", file);
    HttpEntity httpEntity = multipartEntityBuilder.build();
    httpPost.setEntity(httpEntity);
    httpResponse = httpClient.execute(httpPost);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 2. Query product with product name
    String productName = "iagent";
    String queryStr = "name=" + productName;
    String productUrl = "http://localhost:8080/ams/v1/product";
    String productQueryUrl = String.format("%s?%s", productUrl, queryStr);
    HttpGet getProductInfo = new HttpGet(productQueryUrl);
    getProductInfo.setConfig(requestConfig);
    if (requireAuth) {
      getProductInfo.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(getProductInfo);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 3. post config identifer
    String cfgIdUrl = "http://localhost:8080/ams/v1/config/identifier";
    String postCfgIdUrl =
        String.format(
            "%s?%s", cfgIdUrl, "path_name=/test&target_type=device&product_name=" + productName);
    HttpPost cfgIdPost = new HttpPost(postCfgIdUrl);
    cfgIdPost.setConfig(requestConfig);
    if (requireAuth) {
      cfgIdPost.setHeader(tokenHeader, tokenHead + token);
    }
    PostCfgIdPayload postIdPayload = new PostCfgIdPayload();
    postIdPayload.setContent("default cfg id content!");
    StringEntity cfgIdEntity =
        new StringEntity(new ObjectMapper().writeValueAsString(postIdPayload));
    cfgIdEntity.setContentType("application/json");
    cfgIdPost.setEntity(cfgIdEntity);
    httpResponse = httpClient.execute(cfgIdPost);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 4. query config id
    String queryCfgIdUrl =
        String.format(
            "%s?%s", cfgIdUrl, "path_name=/test&target_type=device&product_name=" + productName);
    HttpGet cfgIdGet = new HttpGet(queryCfgIdUrl);
    cfgIdGet.setConfig(requestConfig);
    if (requireAuth) {
      cfgIdGet.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(cfgIdGet);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 5. post config instance to client
    String cfgInstanceUrl = "http://localhost:8080/ams/v1/config/instance";
    String postCfgInstanceUrl =
        String.format(
            "%s?%s",
            cfgInstanceUrl,
            "path_name=/test&target_type=device&target_id=abcdefg001&product_name=" + productName);
    HttpPost cfgInstacePost = new HttpPost(postCfgInstanceUrl);
    cfgInstacePost.setConfig(requestConfig);
    if (requireAuth) {
      cfgInstacePost.setHeader(tokenHeader, tokenHead + token);
    }
    cfgInstacePost.setEntity(new StringEntity("test cfg instance content!"));

    httpResponse = httpClient.execute(cfgInstacePost);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 6. query config instance
    String queryCfgInstanceUrl =
        String.format(
            "%s?%s",
            cfgInstanceUrl,
            "path_name=/test&target_type=device&target_id=abcdefg001&product_name=" + productName);
    HttpGet cfgInstanceGet = new HttpGet(queryCfgInstanceUrl);
    cfgInstanceGet.setConfig(requestConfig);
    if (requireAuth) {
      cfgInstanceGet.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(cfgInstanceGet);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 7. delete config instance
    String delCfgInstanceUrl =
        String.format(
            "%s?%s",
            cfgInstanceUrl,
            "path_name=/test&target_type=device&target_id=abcdefg001&product_name=" + productName);
    HttpDelete cfgInstanceDel = new HttpDelete(delCfgInstanceUrl);
    cfgInstanceDel.setConfig(requestConfig);
    if (requireAuth) {
      cfgInstanceDel.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(cfgInstanceDel);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 8. delete config identifier
    String delCfgIdUrl =
        String.format(
            "%s?%s", cfgIdUrl, "path_name=/test&target_type=device&product_name=" + productName);
    HttpDelete cfgIdDel = new HttpDelete(delCfgIdUrl);
    cfgIdDel.setConfig(requestConfig);
    if (requireAuth) {
      cfgIdDel.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(cfgIdDel);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 9. Delete product
    String delProductUrl = String.format("%s?%s", productUrl, "name=" + productName);
    HttpDelete deleteProduct = new HttpDelete(delProductUrl);
    deleteProduct.setConfig(requestConfig);
    if (requireAuth) {
      deleteProduct.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(deleteProduct);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();
    httpClient.close();
  }

  @Test
  public void testCfg2() throws Exception {
    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    CloseableHttpResponse httpResponse = null;
    RequestConfig requestConfig =
        RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000000).build();
    // 1. create shared content
    String cfgSharedCntUrl = "http://localhost:8080/ams/v1/config/shared";
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("shared_name", "shared1");
    map.put("tag", "tag1");
    map.put("description", "shared content 1");
    map.put("content", "shared content 1");
    HttpPost cfgSharePost = new HttpPost(cfgSharedCntUrl);
    cfgSharePost.setConfig(requestConfig);
    if (requireAuth) {
      cfgSharePost.setHeader(tokenHeader, tokenHead + token);
    }
    StringEntity postShareEntity = new StringEntity(new ObjectMapper().writeValueAsString(map));
    postShareEntity.setContentType("application/json");
    cfgSharePost.setEntity(postShareEntity);
    httpResponse = httpClient.execute(cfgSharePost);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 2. query shared content
    String querySharedCntUrl = String.format("%s?%s", cfgSharedCntUrl, "shared_name=shared1");
    HttpGet cfgSharedGet = new HttpGet(querySharedCntUrl);
    cfgSharedGet.setConfig(requestConfig);
    if (requireAuth) {
      cfgSharedGet.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(cfgSharedGet);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 3. delete shared content
    String delSharedCntUrl = String.format("%s?%s", cfgSharedCntUrl, "shared_name=shared1");
    HttpDelete cfgSharedDel = new HttpDelete(delSharedCntUrl);
    cfgSharedDel.setConfig(requestConfig);
    if (requireAuth) {
      cfgSharedDel.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(cfgSharedDel);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();
    httpClient.close();
  }

  @Test
  public void testTemplate1() throws Exception {

    // Clear DB
    productSrv.removeByName("iagent");
    piSrv.removeByName("iagent");
    FileUtils.deleteDirectory(new File(AmsConstant.repoPath + "iagent"));

    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    CloseableHttpResponse httpResponse = null;
    RequestConfig requestConfig =
        RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000000).build();

    // 1. Upload a product package
    String productName = "iagent";
    String classPath = this.getClass().getResource("/").getPath();
    String filePath = classPath + "iagent.zip";
    File file = new File(filePath);
    if (!file.exists()) {
      System.out.println("cannot find file: " + filePath);
      return;
    }
    String productUploadUrl = "http://localhost:8080/ams/v1/product/upload";

    HttpPost httpPost = new HttpPost(productUploadUrl);
    httpPost.setConfig(requestConfig);
    if (requireAuth) {
      httpPost.setHeader(tokenHeader, tokenHead + token);
    }
    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
    multipartEntityBuilder.addBinaryBody("file", file);
    HttpEntity httpEntity = multipartEntityBuilder.build();
    httpPost.setEntity(httpEntity);
    httpResponse = httpClient.execute(httpPost);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 2. create template
    PostTemplate postPayload = new PostTemplate();
    postPayload.setTemplateName("template1");
    postPayload.setDescription("test template 1");
    TemplateItem item = new TemplateItem();
    item.setProductName(productName);
    item.setVersion("1.0");
    List<TemplateItem> tempList = new ArrayList<TemplateItem>();
    tempList.add(item);
    postPayload.setContent(new ObjectMapper().writeValueAsString(tempList));

    String tempUrl = "http://localhost:8080/ams/v1/template";
    HttpPost tempPost = new HttpPost(tempUrl);
    tempPost.setConfig(requestConfig);
    if (requireAuth) {
      tempPost.setHeader(tokenHeader, tokenHead + token);
    }
    StringEntity postTempEntity =
        new StringEntity(new ObjectMapper().writeValueAsString(postPayload));
    postTempEntity.setContentType("application/json");
    tempPost.setEntity(postTempEntity);
    httpResponse = httpClient.execute(tempPost);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 3. query template
    String tempGetUrl = String.format("%s?%s", tempUrl, "name=template1");
    HttpGet tempGet = new HttpGet(tempGetUrl);
    tempGet.setConfig(requestConfig);
    if (requireAuth) {
      tempGet.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(tempGet);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 4. deploy template to client
    PostTemplateDeploy postTempDeployPayload = new PostTemplateDeploy();
    postTempDeployPayload.setTemplateName("template1");
    String clientId = "abcdefg001";
    List<String> clientList = new ArrayList<String>();
    clientList.add(clientId);
    postTempDeployPayload.setClientList(clientList);

    String tempDeployUrl = "http://localhost:8080/ams/v1/template/deploy";
    HttpPost tempDeployPost = new HttpPost(tempDeployUrl);
    tempDeployPost.setConfig(requestConfig);
    if (requireAuth) {
      tempDeployPost.setHeader(tokenHeader, tokenHead + token);
    }
    StringEntity postTempDeployEntity =
        new StringEntity(new ObjectMapper().writeValueAsString(postTempDeployPayload));
    postTempDeployEntity.setContentType("application/json");
    tempDeployPost.setEntity(postTempDeployEntity);
    httpResponse = httpClient.execute(tempDeployPost);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 5. query template deploy
    String tempDeployGetUrl = String.format("%s?%s", tempDeployUrl, "name=template1");
    HttpGet tempDeployGet = new HttpGet(tempDeployGetUrl);
    tempDeployGet.setConfig(requestConfig);
    if (requireAuth) {
      tempDeployGet.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(tempDeployGet);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 6. delete template from client
    String tempDeployDelUrl = String.format("%s?%s", tempDeployUrl, "client_uuid=abcdefg001");
    HttpDelete tempDeployDel = new HttpDelete(tempDeployDelUrl);
    tempDeployDel.setConfig(requestConfig);
    if (requireAuth) {
      tempDeployDel.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(tempDeployDel);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();

    // 7. delete template
    String tempDelUrl = String.format("%s?%s", tempUrl, "name=template1");
    HttpDelete tempDel = new HttpDelete(tempDelUrl);
    tempDel.setConfig(requestConfig);
    if (requireAuth) {
      tempDel.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(tempDel);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();
    // 8. Delete product
    String productUrl = "http://localhost:8080/ams/v1/product";
    String delProductUrl = String.format("%s?%s", productUrl, "name=" + productName);
    HttpDelete deleteProduct = new HttpDelete(delProductUrl);
    deleteProduct.setConfig(requestConfig);
    if (requireAuth) {
      deleteProduct.setHeader(tokenHeader, tokenHead + token);
    }
    httpResponse = httpClient.execute(deleteProduct);
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

    httpResponse.close();
    httpClient.close();
  }

  @After
  public void teardown() {

    // Delete the client
    clientSrv.delete(client1);
  }
}

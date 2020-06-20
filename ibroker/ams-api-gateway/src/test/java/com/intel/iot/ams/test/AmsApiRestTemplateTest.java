/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intel.iot.ams.api.requestbody.*;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.repository.AmsClientDao;
import com.intel.iot.ams.security.ApiJwtTokenUtil;
import com.intel.iot.ams.service.AmsClientService;
import com.intel.iot.ams.service.ProductInstanceService;
import com.intel.iot.ams.service.ProductService;
import com.intel.iot.ams.utils.AmsConstant;
import com.openiot.cloud.base.common.model.TokenContent;
import com.openiot.cloud.base.mongo.model.help.UserRole;
import com.openiot.cloud.sdk.service.*;
import java.io.File;
import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AmsApiRestTemplateTest {

  private String token = null;

  @Mock private JmsMqClient fakeMQClient;

  @Autowired private ApiJwtTokenUtil jwtUtil;

  @Autowired private AmsClientService clientSrv;

  @Autowired private AmsClientDao amsClientDao;

  @Autowired private ProductService productSrv;

  @Autowired private ProductInstanceService piSrv;

  @Autowired private AmsConstant AmsConst;

  AmsClient client1 = null;

  @Value("${jwt.header}")
  private String tokenHeader;

  @Value("${jwt.tokenHead}")
  private String tokenHead;

  @LocalServerPort private int port;

  private String baseUrl;

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private IConnect iConnect;

  @Autowired private ObjectMapper objectMapper;

  private List<AmsClient> clientList = new LinkedList<>();

  @Before
  public void init() throws Exception {
    ReflectionTestUtils.setField(iConnect, "mqClient", fakeMQClient);

    doAnswer(
            invocationOnMock -> {
              Object[] arguments = invocationOnMock.getArguments();
              IConnectRequest request = (IConnectRequest) arguments[0];
              IConnectResponseHandler handler = (IConnectResponseHandler) arguments[1];

              if (request.getUrl().equals("/api/user/validation")) {
                TokenContent tokenContent = new TokenContent();
                tokenContent.setUser("apple");
                tokenContent.setProject(null);
                tokenContent.setRole(UserRole.USER);

                IConnectResponse response =
                    IConnectResponse.createFromRequest(
                        request,
                        HttpStatus.OK,
                        MediaType.APPLICATION_JSON,
                        objectMapper.writeValueAsBytes(tokenContent));
                handler.onResponse(response);
              } else {
                IConnectResponse response =
                    IConnectResponse.createFromRequest(
                        request,
                        HttpStatus.I_AM_A_TEAPOT,
                        MediaType.APPLICATION_JSON,
                        objectMapper.writeValueAsBytes("empty payload"));
                handler.onResponse(response);
              }

              return null;
            })
        .when(fakeMQClient)
        .send(
            isA(IConnectRequest.class),
            isA(IConnectResponseHandler.class),
            anyInt(),
            isA(TimeUnit.class));

    System.out.println("============AmsApiRestTemplateTest INIT START===============");

    // 1. Create a client in DB
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
    client1.setProjectId("fuzzy");
    // client1.setTemplateName("");

    clientList.add(client1);

    AmsClient client23 = new AmsClient();
    client23.setClientUuid("abcdefg002");
    client23.setProjectId("fuzzy");
    client23.setCpu("x86-64");
    client23.setOs("ubuntu 16.04");
    client23.setBits("64");
    client23.setSerial("abcdefg002");
    client23.setProvisionTime(Date.from(Instant.now(Clock.systemUTC())));
    clientList.add(client23);

    AmsClient client27 = new AmsClient();
    client27.setClientUuid("xyz003");
    client27.setProjectId(null);
    client27.setCpu("x86-64");
    client27.setOs("ubuntu 16.04");
    client27.setBits("64");
    client27.setSerial("xyz003");
    client27.setProvisionTime(Date.from(Instant.now(Clock.systemUTC())));
    clientList.add(client27);

    AmsClient client29 = new AmsClient();
    client29.setClientUuid("xyz005");
    client29.setProjectId(null);
    client29.setCpu("x86-64");
    client29.setOs("ubuntu 16.04");
    client29.setBits("64");
    client29.setSerial("xyz005");
    client29.setProvisionTime(Date.from(Instant.now(Clock.systemUTC())));
    clientList.add(client29);

    clientList.forEach(
        client -> {
          if (amsClientDao.findByClientUuid(client.getClientUuid()) == null) {
            clientSrv.save(client);
          }
        });

    // 2. Create a token
    token = jwtUtil.generateToken(null, "apitest");

    // 3. Create Base URL
    baseUrl = String.format("http://localhost:%d/ams_user_cloud/", port);

    System.out.println("============AmsApiRestTemplateTest INIT END===============");
  }

  @After
  public void tearDown() throws Exception {
    amsClientDao.deleteAll(clientList);
  }

  @Test
  public void testAmsClientFuzzySearch() {
    // search by efg001
    List<AmsClient> clients = clientSrv.fuzzySearch("efg001", 0, 20, "fuzzy");
    assertThat(clients).isNotNull().isNotEmpty().hasSize(1);
    assertThat(clients.get(0))
        .hasFieldOrPropertyWithValue("serial", "abcdefg001")
        .hasFieldOrPropertyWithValue("projectId", "fuzzy");

    // search by efg00
    clients = clientSrv.fuzzySearch("efg", 0, 20, "fuzzy");
    assertThat(clients).isNotNull().isNotEmpty().hasSize(2);
    assertThat(clients.get(0))
        .hasFieldOrPropertyWithValue("serial", "abcdefg001")
        .hasFieldOrPropertyWithValue("projectId", "fuzzy");
    assertThat(clients.get(1))
        .hasFieldOrPropertyWithValue("serial", "abcdefg002")
        .hasFieldOrPropertyWithValue("projectId", "fuzzy");

    // search by empty string
    clients = clientSrv.fuzzySearch("", 0, 20, "fuzzy");
    assertThat(clients).isNotNull().isNotEmpty().hasSize(2);
    assertThat(clients.get(0))
        .hasFieldOrPropertyWithValue("serial", "abcdefg001")
        .hasFieldOrPropertyWithValue("projectId", "fuzzy");
    assertThat(clients.get(1))
        .hasFieldOrPropertyWithValue("serial", "abcdefg002")
        .hasFieldOrPropertyWithValue("projectId", "fuzzy");

    // search by empty string and null ProjectID
    clients = clientSrv.fuzzySearch("", 0, 20, null);
    assertThat(clients).isNotNull().isNotEmpty().hasSize(2);
    assertThat(clients).extracting("serial").containsOnly("xyz003", "xyz005");

    clients = clientSrv.fuzzySearch("", 0, 20, "fuzzy");
    assertThat(clients).isNotNull().isNotEmpty().hasSize(2);
    assertThat(clients.get(0))
        .hasFieldOrPropertyWithValue("serial", "abcdefg001")
        .hasFieldOrPropertyWithValue("projectId", "fuzzy");
    assertThat(clients.get(1))
        .hasFieldOrPropertyWithValue("serial", "abcdefg002")
        .hasFieldOrPropertyWithValue("projectId", "fuzzy");
  }

  @Test
  public void testGetUnassignedClient() throws Exception {
    // 1. Query client information with out project information in the token
    ResponseEntity<String> response1 = null;
    HttpHeaders headers1 = new HttpHeaders();
    headers1.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity1 = new HttpEntity<String>(headers1);
    response1 =
        restTemplate.exchange(
            baseUrl + "ams/v1/ams_client?fuzz_str=", HttpMethod.GET, requestEntity1, String.class);
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response1.getBody()).isNotNull();

    ObjectMapper om = new ObjectMapper();
    Map<String, List<Map<String, Object>>> result =
        om.readValue(
            response1.getBody(), new TypeReference<Map<String, List<Map<String, Object>>>>() {});
    assertThat(result).isNotNull();
    List<Map<String, Object>> clients = result.get("client_list");

    assertThat(clients).isNotNull().isNotEmpty().hasSize(2);
    assertThat(clients.get(0).get("ams_client_uuid")).isEqualTo("xyz003");
    assertThat(clients.get(0).get("project")).isNull();
    assertThat(clients.get(1).get("ams_client_uuid")).isEqualTo("xyz005");
    assertThat(clients.get(1).get("project")).isNull();

    // 2. over write project information in the token with a query parameter
    HttpHeaders headersWithEmptyProject = new HttpHeaders();
    headersWithEmptyProject.add(tokenHeader, tokenHead + "BAD_TOKEN");
    HttpEntity<String> requestEntityEmptyProject = new HttpEntity<String>(headersWithEmptyProject);
    doAnswer(
            invocationOnMock -> {
              Object[] arguments = invocationOnMock.getArguments();
              IConnectRequest request = (IConnectRequest) arguments[0];
              IConnectResponseHandler handler = (IConnectResponseHandler) arguments[1];

              if (request.getUrl().equals("/api/user/validation")) {
                TokenContent tokenContent = new TokenContent();
                tokenContent.setUser("apple");
                tokenContent.setProject("never_seen_it_before");
                tokenContent.setRole(UserRole.USER);

                IConnectResponse response =
                    IConnectResponse.createFromRequest(
                        request,
                        HttpStatus.OK,
                        MediaType.APPLICATION_JSON,
                        objectMapper.writeValueAsBytes(tokenContent));
                handler.onResponse(response);
              } else {
                IConnectResponse response =
                    IConnectResponse.createFromRequest(
                        request,
                        HttpStatus.I_AM_A_TEAPOT,
                        MediaType.APPLICATION_JSON,
                        objectMapper.writeValueAsBytes("empty payload"));
                handler.onResponse(response);
              }

              return null;
            })
        .when(fakeMQClient)
        .send(
            isA(IConnectRequest.class),
            isA(IConnectResponseHandler.class),
            anyInt(),
            isA(TimeUnit.class));

    response1 =
        restTemplate.exchange(
            baseUrl + "ams/v1/ams_client?fuzz_str=",
            HttpMethod.GET,
            requestEntityEmptyProject,
            String.class);
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
    result =
        om.readValue(
            response1.getBody(), new TypeReference<Map<String, List<Map<String, Object>>>>() {});
    clients = result.get("client_list");
    assertThat(clients).hasSize(0);

    response1 =
        restTemplate.exchange(
            UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("ams/v1/ams_client")
                .queryParam("fuzz_str", null)
                .queryParam("project", "null")
                .build()
                .toUri(),
            HttpMethod.GET,
            requestEntity1,
            String.class);
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
    result =
        om.readValue(
            response1.getBody(), new TypeReference<Map<String, List<Map<String, Object>>>>() {});
    assertThat(result).isNotNull();
    clients = result.get("client_list");
    assertThat(clients).isNotNull().isNotEmpty().hasSize(2);

    assertThat(clients.get(0).get("ams_client_uuid")).isEqualTo("xyz003");
    assertThat(clients.get(0).get("project")).isNull();
    assertThat(clients.get(1).get("ams_client_uuid")).isEqualTo("xyz005");
    assertThat(clients.get(1).get("project")).isNull();
  }

  @Test
  public void testProduct1() throws Exception {

    // Clear DB
    productSrv.removeByName("iagent");
    piSrv.removeByName("iagent");
    FileUtils.deleteDirectory(new File(AmsConst.repoPath + "iagent"));

    // 1. Upload a product package
    String classPath = this.getClass().getResource("/").getPath();

    String filePath = classPath + "iagent.zip";
    Resource resource = new FileSystemResource(filePath);
    assertThat(resource.exists()).isTrue();
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
    map.add("file", resource);
    HttpHeaders headers1 = new HttpHeaders();
    headers1.add(tokenHeader, tokenHead + token);
    headers1.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap> requestEntity1 = new HttpEntity<MultiValueMap>(map, headers1);
    ResponseEntity<String> response1 = null;
    response1 =
        restTemplate.postForEntity(baseUrl + "ams/v1/product/upload", requestEntity1, String.class);
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 2. Query product with product name
    ResponseEntity<String> response2 = null;
    HttpHeaders headers2 = new HttpHeaders();
    headers2.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity2 = new HttpEntity<String>(headers2);
    response2 =
        restTemplate.exchange(
            baseUrl + "ams/v1/product?name=iagent", HttpMethod.GET, requestEntity2, String.class);
    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 3. Update product information
    UpdateProductInfo payload = new UpdateProductInfo();
    payload.setProductName("iagent");
    payload.setDescription("iagent product for AMS test!");
    ResponseEntity<String> response3 = null;
    HttpHeaders headers3 = new HttpHeaders();
    headers3.add(tokenHeader, tokenHead + token);
    HttpEntity<UpdateProductInfo> requestEntity3 =
        new HttpEntity<UpdateProductInfo>(payload, headers3);
    response3 =
        restTemplate.exchange(
            baseUrl + "ams/v1/product", HttpMethod.POST, requestEntity3, String.class);
    assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 4. Query product again to verify the update
    ResponseEntity<String> response4 = null;
    HttpHeaders headers4 = new HttpHeaders();
    headers4.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity4 = new HttpEntity<String>(headers4);
    response4 =
        restTemplate.exchange(
            baseUrl + "ams/v1/product?name=iagent", HttpMethod.GET, requestEntity4, String.class);
    assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 5. Deploy product to client
    AddProductForClientInfo deployPayload = new AddProductForClientInfo();
    deployPayload.setClientUuid("abcdefg001");
    deployPayload.setProductName("iagent");
    deployPayload.setVersion("1.0");
    deployPayload.setCategory("software_product");
    ResponseEntity<String> response5 = null;
    HttpHeaders headers5 = new HttpHeaders();
    headers5.add(tokenHeader, tokenHead + token);
    HttpEntity<AddProductForClientInfo> requestEntity5 =
        new HttpEntity<AddProductForClientInfo>(deployPayload, headers5);
    response5 =
        restTemplate.exchange(
            baseUrl + "ams/v1/product/deploy", HttpMethod.POST, requestEntity5, String.class);
    assertThat(response5.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 6. Query product deployment of client
    ResponseEntity<String> response6 = null;
    HttpHeaders headers6 = new HttpHeaders();
    headers6.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity6 = new HttpEntity<String>(headers6);
    response6 =
        restTemplate.exchange(
            baseUrl + "ams/v1/product/deploy?client_uuid=abcdefg001",
            HttpMethod.GET,
            requestEntity6,
            String.class);
    assertThat(response6.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 7. Delete product deploy
    ResponseEntity<String> response7 = null;
    HttpHeaders headers7 = new HttpHeaders();
    headers7.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity7 = new HttpEntity<String>(headers7);
    response7 =
        restTemplate.exchange(
            baseUrl + "ams/v1/product/deploy?client_uuid=abcdefg001&product_name=iagent",
            HttpMethod.DELETE,
            requestEntity7,
            String.class);
    assertThat(response7.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 8. Delete product
    ResponseEntity<String> response8 = null;
    HttpHeaders headers8 = new HttpHeaders();
    headers8.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity8 = new HttpEntity<String>(headers8);
    response8 =
        restTemplate.exchange(
            baseUrl + "ams/v1/product?name=iagent",
            HttpMethod.DELETE,
            requestEntity8,
            String.class);
    assertThat(response8.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void testClient1() throws Exception {
    AmsClient client = amsClientDao.findByClientUuidAndProjectId("abcdefg002", "fuzzy");
    client.setProjectId(null);
    amsClientDao.save(client);

    assertThat(amsClientDao.findByClientUuidAndProjectId("abcdefg002", null)).isNotNull();

    // 1. Query client information
    ResponseEntity<String> response1 = null;
    HttpHeaders headers1 = new HttpHeaders();
    headers1.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity1 = new HttpEntity<String>(headers1);
    response1 =
        restTemplate.exchange(
            baseUrl + "ams/v1/ams_client?client_uuid=abcdefg002",
            HttpMethod.GET,
            requestEntity1,
            String.class);
    log.info("response" + response1);
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response1.getBody()).isNotNull().isNotEmpty();

    // 2. Update the client information
    UpdateAmsClientInfo payload = new UpdateAmsClientInfo();
    payload.setClientUuid("abcdefg002");
    payload.setDescription("test client 2 - dev002");
    ResponseEntity<String> response2 = null;
    HttpHeaders headers2 = new HttpHeaders();
    headers2.add(tokenHeader, tokenHead + token);
    HttpEntity<UpdateAmsClientInfo> requestEntity2 =
        new HttpEntity<UpdateAmsClientInfo>(payload, headers2);
    response2 =
        restTemplate.exchange(
            baseUrl + "ams/v1/ams_client", HttpMethod.POST, requestEntity2, String.class);
    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 3. query client information and verify the update info
    ResponseEntity<String> response3 = null;
    HttpHeaders headers3 = new HttpHeaders();
    headers3.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity3 = new HttpEntity<String>(headers3);
    response3 =
        restTemplate.exchange(
            baseUrl + "ams/v1/ams_client?client_uuid=abcdefg002",
            HttpMethod.GET,
            requestEntity3,
            String.class);
    assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 4. Delete the client
    ResponseEntity<String> response4 = null;
    HttpHeaders headers4 = new HttpHeaders();
    headers4.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity4 = new HttpEntity<String>(headers4);
    response3 =
        restTemplate.exchange(
            baseUrl + "ams/v1/ams_client?client_uuid=abcdefg002",
            HttpMethod.DELETE,
            requestEntity4,
            String.class);
    assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 5. Query client to make sure the client is deleted
    ResponseEntity<String> response5 = null;
    HttpHeaders headers5 = new HttpHeaders();
    headers5.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity5 = new HttpEntity<String>(headers5);
    response5 =
        restTemplate.exchange(
            baseUrl + "ams/v1/ams_client?client_uuid=abcdefg002",
            HttpMethod.GET,
            requestEntity5,
            String.class);
    // assertThat(response5.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response5.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    // assertThat(response5.getBody()).isNull();
  }

  @Test
  public void testCfg1() throws Exception {

    // Clear DB
    productSrv.removeByName("iagent");
    piSrv.removeByName("iagent");
    FileUtils.deleteDirectory(new File(AmsConst.repoPath + "iagent"));

    // 1. Upload a product package
    String classPath = this.getClass().getResource("/").getPath();
    String filePath = classPath + "iagent.zip";
    Resource resource = new FileSystemResource(filePath);
    assertThat(resource.exists()).isTrue();
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
    map.add("file", resource);
    HttpHeaders headers1 = new HttpHeaders();
    headers1.add(tokenHeader, tokenHead + token);
    headers1.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap> requestEntity1 = new HttpEntity<MultiValueMap>(map, headers1);
    ResponseEntity<String> response1 = null;
    response1 =
        restTemplate.postForEntity(baseUrl + "ams/v1/product/upload", requestEntity1, String.class);
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 2. Query product with product name
    ResponseEntity<String> response2 = null;
    HttpHeaders headers2 = new HttpHeaders();
    headers2.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity2 = new HttpEntity<String>(headers2);
    response2 =
        restTemplate.exchange(
            baseUrl + "ams/v1/product?name=iagent", HttpMethod.GET, requestEntity2, String.class);
    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 3. post config identifer
    PostCfgIdPayload postIdPayload = new PostCfgIdPayload();
    postIdPayload.setContent("default cfg id content!");
    ResponseEntity<String> response3 = null;
    HttpHeaders headers3 = new HttpHeaders();
    headers3.add(tokenHeader, tokenHead + token);
    HttpEntity<PostCfgIdPayload> requestEntity3 =
        new HttpEntity<PostCfgIdPayload>(postIdPayload, headers3);
    response3 =
        restTemplate.exchange(
            baseUrl
                + "ams/v1/config/identifier?path_name=/test&target_type=device&product_name=iagent",
            HttpMethod.POST,
            requestEntity3,
            String.class);
    assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 4. query config id
    ResponseEntity<String> response4 = null;
    HttpHeaders headers4 = new HttpHeaders();
    headers4.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity4 = new HttpEntity<String>(headers4);
    response4 =
        restTemplate.exchange(
            baseUrl
                + "ams/v1/config/identifier?path_name=/test&target_type=device&product_name=iagent",
            HttpMethod.GET,
            requestEntity4,
            String.class);
    assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 5. post config instance to client
    String postInstanceBody = "test cfg instance content!";
    ResponseEntity<String> response5 = null;
    HttpHeaders headers5 = new HttpHeaders();
    headers5.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity5 = new HttpEntity<String>(postInstanceBody, headers5);
    response5 =
        restTemplate.exchange(
            baseUrl
                + "ams/v1/config/instance?path_name=/test&target_type=device&target_id=abcdefg001&product_name=iagent",
            HttpMethod.POST,
            requestEntity5,
            String.class);
    assertThat(response5.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 6. query config instance
    ResponseEntity<String> response6 = null;
    HttpHeaders headers6 = new HttpHeaders();
    headers6.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity6 = new HttpEntity<String>(headers6);
    response6 =
        restTemplate.exchange(
            baseUrl
                + "ams/v1/config/instance?path_name=/test&target_type=device&target_id=abcdefg001&product_name=iagent",
            HttpMethod.GET,
            requestEntity6,
            String.class);
    assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 7. delete config instance
    ResponseEntity<String> response7 = null;
    HttpHeaders headers7 = new HttpHeaders();
    headers7.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity7 = new HttpEntity<String>(headers7);
    response7 =
        restTemplate.exchange(
            baseUrl
                + "ams/v1/config/instance?path_name=/test&target_type=device&target_id=abcdefg001&product_name=iagent",
            HttpMethod.DELETE,
            requestEntity7,
            String.class);
    assertThat(response7.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 8. delete config identifier
    ResponseEntity<String> response8 = null;
    HttpHeaders headers8 = new HttpHeaders();
    headers8.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity8 = new HttpEntity<String>(headers8);
    response8 =
        restTemplate.exchange(
            baseUrl
                + "ams/v1/config/identifier?path_name=/test&target_type=device&product_name=iagent",
            HttpMethod.DELETE,
            requestEntity8,
            String.class);
    assertThat(response8.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 9. Delete product
    ResponseEntity<String> response9 = null;
    HttpHeaders headers9 = new HttpHeaders();
    headers9.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity9 = new HttpEntity<String>(headers9);
    response9 =
        restTemplate.exchange(
            baseUrl + "ams/v1/product?name=iagent",
            HttpMethod.DELETE,
            requestEntity9,
            String.class);
    assertThat(response9.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void testCfg2() throws Exception {
    // 1. create shared content
    Map<String, String> map = new HashMap<String, String>();
    map.put("shared_name", "shared1");
    map.put("tag", "tag1");
    map.put("description", "shared content 1");
    map.put("content_type", "1");
    map.put("content", "shared content 1");
    // String postCntBody = new ObjectMapper().writeValueAsString(map);

    ResponseEntity<String> response1 = null;
    HttpHeaders headers1 = new HttpHeaders();
    headers1.add(tokenHeader, tokenHead + token);
    HttpEntity<Map<String, String>> requestEntity1 =
        new HttpEntity<Map<String, String>>(map, headers1);
    response1 =
        restTemplate.exchange(
            baseUrl + "ams/v1/config/shared", HttpMethod.POST, requestEntity1, String.class);
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 2.1 query shared content
    ResponseEntity<String> response2_1 = null;
    HttpHeaders headers2_1 = new HttpHeaders();
    headers2_1.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity2_1 = new HttpEntity<String>(headers2_1);
    response2_1 =
        restTemplate.exchange(
            baseUrl + "ams/v1/config/shared?shared_name=shared1",
            HttpMethod.GET,
            requestEntity2_1,
            String.class);
    assertThat(response2_1.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 2.2 query shared content by tag
    HttpHeaders headers2_2 = new HttpHeaders();
    headers2_2.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity2_2 = new HttpEntity<>(headers2_2);
    ResponseEntity<String> response2_2 = null;
    response2_2 =
        restTemplate.exchange(
            baseUrl + "/ams/v1/config/shared?tag=tag1",
            HttpMethod.GET,
            requestEntity2_2,
            String.class);
    assertThat(response2_2.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 2.3 query shared content by contentType
    HttpHeaders headers2_3 = new HttpHeaders();
    headers2_3.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity2_3 = new HttpEntity<>(headers2_3);
    ResponseEntity<String> response2_3 = null;
    response2_3 =
        restTemplate.exchange(
            baseUrl + "/ams/v1/config/shared?content_type=1",
            HttpMethod.GET,
            requestEntity2_3,
            String.class);
    assertThat(response2_3.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 2.3 query shared content by fuzz_str
    HttpHeaders headers2_4 = new HttpHeaders();
    headers2_4.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity2_4 = new HttpEntity<>(headers2_4);
    ResponseEntity<String> response2_4 = null;
    /*
     * assume that fuzzy search by shard_name "hared"
     */
    response2_4 =
        restTemplate.exchange(
            baseUrl + "/ams/v1/config/shared?fuzz_str=hared",
            HttpMethod.GET,
            requestEntity2_4,
            String.class);
    assertThat(response2_4.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 3. delete shared content
    ResponseEntity<String> response3 = null;
    HttpHeaders headers3 = new HttpHeaders();
    headers3.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity3 = new HttpEntity<String>(headers3);
    response3 =
        restTemplate.exchange(
            baseUrl + "ams/v1/config/shared?shared_name=shared1",
            HttpMethod.DELETE,
            requestEntity3,
            String.class);
    assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void testTemplate1() throws Exception {

    // Clear DB
    productSrv.removeByName("iagent");
    piSrv.removeByName("iagent");
    FileUtils.deleteDirectory(new File(AmsConst.repoPath + "iagent"));

    // 1. Upload a product package
    String classPath = this.getClass().getResource("/").getPath();
    String filePath = classPath + "iagent.zip";
    Resource resource = new FileSystemResource(filePath);
    assertThat(resource.exists()).isTrue();
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
    map.add("file", resource);
    HttpHeaders headers1 = new HttpHeaders();
    headers1.add(tokenHeader, tokenHead + token);
    headers1.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap> requestEntity1 = new HttpEntity<MultiValueMap>(map, headers1);
    ResponseEntity<String> response1 = null;
    response1 =
        restTemplate.postForEntity(baseUrl + "ams/v1/product/upload", requestEntity1, String.class);
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 2. create template
    String productName = "iagent";
    PostTemplate postPayload = new PostTemplate();
    postPayload.setTemplateName("template1");
    postPayload.setDescription("test template 1");
    TemplateItem item = new TemplateItem();
    item.setProductName(productName);
    item.setVersion("1.0");
    List<TemplateItem> tempList = new ArrayList<TemplateItem>();
    tempList.add(item);
    postPayload.setContent(new ObjectMapper().writeValueAsString(tempList));
    // String postTempBody = new ObjectMapper().writeValueAsString(postPayload);

    System.out.println(postPayload.getContent());

    ResponseEntity<String> response2 = null;
    HttpHeaders headers2 = new HttpHeaders();
    headers2.add(tokenHeader, tokenHead + token);
    HttpEntity<PostTemplate> requestEntity2 = new HttpEntity<PostTemplate>(postPayload, headers2);
    response2 =
        restTemplate.exchange(
            baseUrl + "ams/v1/template", HttpMethod.POST, requestEntity2, String.class);
    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 3. query template
    ResponseEntity<String> response3 = null;
    HttpHeaders headers3 = new HttpHeaders();
    headers3.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity3 = new HttpEntity<String>(headers3);
    response3 =
        restTemplate.exchange(
            baseUrl + "ams/v1/template?name=template1",
            HttpMethod.GET,
            requestEntity3,
            String.class);
    assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 4. deploy template to client
    PostTemplateDeploy postTempDeployPayload = new PostTemplateDeploy();
    postTempDeployPayload.setTemplateName("template1");
    String clientId = "abcdefg001";
    List<String> clientList = new ArrayList<String>();
    clientList.add(clientId);
    postTempDeployPayload.setClientList(clientList);
    // String postTempDeployBody = new
    // ObjectMapper().writeValueAsString(postTempDeployPayload);

    ResponseEntity<String> response4 = null;
    HttpHeaders headers4 = new HttpHeaders();
    headers4.add(tokenHeader, tokenHead + token);
    HttpEntity<PostTemplateDeploy> requestEntity4 =
        new HttpEntity<PostTemplateDeploy>(postTempDeployPayload, headers4);
    response4 =
        restTemplate.exchange(
            baseUrl + "ams/v1/template/deploy", HttpMethod.POST, requestEntity4, String.class);
    assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 5. query template deploy
    ResponseEntity<String> response5 = null;
    HttpHeaders headers5 = new HttpHeaders();
    headers5.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity5 = new HttpEntity<String>(headers5);
    response5 =
        restTemplate.exchange(
            baseUrl + "ams/v1/template/deploy?name=template1",
            HttpMethod.GET,
            requestEntity5,
            String.class);
    assertThat(response5.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 6. delete template from client
    ResponseEntity<String> response6 = null;
    HttpHeaders headers6 = new HttpHeaders();
    headers6.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity6 = new HttpEntity<String>(headers6);
    response6 =
        restTemplate.exchange(
            baseUrl + "ams/v1/template/deploy?client_uuid=abcdefg001",
            HttpMethod.DELETE,
            requestEntity6,
            String.class);
    assertThat(response6.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 7. delete template
    ResponseEntity<String> response7 = null;
    HttpHeaders headers7 = new HttpHeaders();
    headers7.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity7 = new HttpEntity<String>(headers7);
    response7 =
        restTemplate.exchange(
            baseUrl + "ams/v1/template?name=template1",
            HttpMethod.DELETE,
            requestEntity7,
            String.class);
    assertThat(response7.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 8. Delete product
    ResponseEntity<String> response8 = null;
    HttpHeaders headers8 = new HttpHeaders();
    headers8.add(tokenHeader, tokenHead + token);
    HttpEntity<String> requestEntity8 = new HttpEntity<String>(headers8);
    response8 =
        restTemplate.exchange(
            baseUrl + "ams/v1/product?name=iagent",
            HttpMethod.DELETE,
            requestEntity8,
            String.class);
    assertThat(response8.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @After
  public void teardown() {
    // Delete the client
    clientSrv.delete(client1);
  }
}

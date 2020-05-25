/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.testCases;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.intel.iot.ams.entity.*;
import com.intel.iot.ams.requestbody.CalculateChangesProperty;
import com.intel.iot.ams.service.*;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.Date;

import java.util.List;

public class CoapTestDemo {

    //
    private AmsTaskService TaskSrv;
    private AmsClientService AmsCltSrv;

    private ClientDeviceMappingService CltDvcMapSrv;

    private AmsTemplateService AmsTmpltSrv;

    private CfgContentService CfgContSrv;

    private ProductService ProdSrv;

    private ProductInstanceService ProdInstSrv;

    private ProductDeployService ProdDeplSrv;

    private ProductDownloadPackageService ProdDwnldPkgSrv;

    private CfgIdentifierService CfgIdentSrv;

    private String base_uri="localhost:5555";

    private String prov_uri="/ams/v1/prov"; /* client provision */

    private String prod_map_uri="/ams/v1/prod_map"; /* client device map */

    private String tmplt_uri="/ams/v1/t"; /* get&set template for client */

    private String test_PostInst_uri="/ams/v1/p/installed"; /*  */

    private String test_QryProdChg_uri="/ams/v1/p/c";

    private String test_DownInstPkg_uri="/ams/v1/p/d";

    private String test_QryCfgInfo_uri="/ams/v1/c/q";

    private String test_QryDownCfgCt_uri="/ams/v1/c/d";

    private String test_AllCfgInfo_uri="/ams/v1/c/a";

    public static void main(String[] args) throws Exception{
      CoapTestDemo CoapT=new CoapTestDemo();
      CoapT.init();         //should always run in every time
      CoapT.test_prov();    //should always run in every time
      CoapT.test_SetTmplt();
      //should test before product_map & PostProdInst, removeProductChange in these 2 cases.
      //Besides, AmsTask thread will constantly query task and may removeProductChanges too.
      CoapT.test_QryProdChg();

      CoapT.test_QryDownCfgCt();

      CoapT.test_prod_map();
      CoapT.test_GetTmplt();
      CoapT.test_PostProdInst();
      CoapT.test_QryDownCfgCt();

      //CoapT.test_DownInstPkg();

      CoapT.test_PostAllCfgInfo();
      CoapT.test_QryAllCfgInfo();

      CoapT.test_QryCfgInfo();
    }

    public void init(){
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Enter the Init function\n ");
        System.out.println("------------  Init() start -----------------\n");

        //get Instance for necessary service
        AmsCltSrv =ServiceBundle.getInstance().getClientSrv();
        CltDvcMapSrv= ServiceBundle.getInstance().getMapSrv();
        AmsTmpltSrv=ServiceBundle.getInstance().getTemplateSrv();
        CfgContSrv=ServiceBundle.getInstance().getCfgContentSrv();
        ProdSrv=ServiceBundle.getInstance().getProductSrv();
        ProdInstSrv=ServiceBundle.getInstance().getProductInstanceSrv();
        ProdDeplSrv=ServiceBundle.getInstance().getProductDeploySrv();
        TaskSrv=ServiceBundle.getInstance().getTaskSrv();
        ProdDwnldPkgSrv=ServiceBundle.getInstance().getProductPkgSrv();
        CfgIdentSrv=ServiceBundle.getInstance().getCfgIdSrv();

        System.out.println("Clear DB\n ");

        //delete AmsTask of which Property equals testuuid01
        List<AmsTask> tklist=TaskSrv.findAll();
        if(tklist!=null){
            for(AmsTask tk:tklist){
                CalculateChangesProperty prop;
                Gson gson = new Gson();
                try {
                    prop = gson.fromJson(tk.getTaskProperties(), CalculateChangesProperty.class);
                } catch (JsonSyntaxException jse) {
                    return;
                }
                if (prop == null || prop.getClientUuid() == null) {
                    continue;
                }
                AmsClient client = AmsCltSrv.findByClientUUID(prop.getClientUuid());
                if (client == null) {
                    continue;
                }else{
                    if(client.getClientUuid().equals("testuuid001")){
                        TaskSrv.removeById(tk.getId());
                    }
                }
            }
        }

        //delete test client.
        AmsCltSrv.removeByClientUUID("testuuid001");
        //delete ClientDeviceMapping
        CltDvcMapSrv.removeByClientUuid("testuuid001");
        //delete AmsTemplate
        AmsTmpltSrv.removeByName("temp01");
        //delete Product
        ProdSrv.removeByUUID("aabb-ccdd-eeff-gghh-iijj-001");

        //delete ProductInstance.
        List<ProductInstance> inslst=ProdInstSrv.findAll();
        if (inslst!=null){
            for(ProductInstance ins : inslst){
                if(ins.getProductName().equals("test_Product01")){
                    ProdInstSrv.removeById(ins.getInstanceId());
                }
            }
        }

        //delete ProductDownloadPackage
        List<ProductDownloadPackage> prdwnpgkLst=ProdDwnldPkgSrv.findAll();
        if(prdwnpgkLst!=null){
            for(ProductDownloadPackage p:prdwnpgkLst){
                if(p.getProductName().equals("test_Product01")){
                    ProdDwnldPkgSrv.removeById(p.getId());
                }
            }
        }

        //delete ProductDeploy
        List<ProductDeploy> deploylst=ProdDeplSrv.findByClientUUID("testuuid001");
        if(deploylst!=null){
            for(ProductDeploy dpl:deploylst){
                ProdDeplSrv.removeById(dpl.getId());
            }
        }

        System.out.println("\n------------  Init() finish -----------------");
        System.out.println("--------------------------------------------------------------------------\n\n\n");

    }

    /* client provision */
    public void test_prov() throws Exception{
        System.out.println("\n\n\n---------------------------------------------------------------------------");
        System.out.println("Enter the test_prov function\n");

        URI uri =new URI(base_uri+prov_uri);
        CoapClient client =new CoapClient(uri);

        JSONObject JnObj=new JSONObject();
        JnObj.put("di","testuuid001");
        JnObj.put("hardware_serial","test_serial001");
        JnObj.put("bits","64");
        JnObj.put("cpu","intel");
        JnObj.put("description","descTest01");
        JnObj.put("device_name","DvcTest01");
        JnObj.put("ams_version","2019-9-10");
        JnObj.put("os","os01");
        JnObj.put("os_ver","2.0");
        JnObj.put("system","sys01");
        JnObj.put("sys_ver","7.0");
        //JnObj.put("template","temp01"); //AmsTemplate. Will be updated in setTemplt function.
        JnObj.put("wasm_enable",true);
        JnObj.put("aot_enable",true);
        String payload=JnObj.toString();

        CoapResponse response=client.post(payload,50);/* format id : 50 means Media type is Application/json */

        /* DB preparation :
        *  Add related Template record to AmsClient TABLE.
        * */
        AmsTemplate tmplt=new AmsTemplate();
        String content="[{\"product_name\":\"iagent\",\"version\":\"2019-8-12\",\"configurations\":[{\"path_name\":\"/bus.cfg\",\"cfg_type\":\"device\",\"content_name\":\"bus.cfg\"},{\"path_name\":\"/modbus.cfg\",\"cfg_type\":\"device\",\"content_name\":\"modbus_2.cfg\"}]}]";
        tmplt.setContent(content);
        tmplt.setDescription("test_desc");
        tmplt.setName("temp01"); //be same with template in AmsClient TABLE
        tmplt.setTitle("test_title01");
        AmsTmpltSrv.save(tmplt);
        /**
         * Add finished.
         * */

        if(response!=null){
            System.out.println("\n Response");
            System.out.println(Utils.prettyPrint(response));
            if(response.getCode().toString().startsWith("2")){
                System.out.println("\n Provision Success !");
            }else{
                System.out.println("\n Provision Fail ! ");
            }
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }else{
            System.out.println("\n Response is null ! ");
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }
    }

    /* client device map */
    public void test_prod_map() throws Exception{
        System.out.println("\n\n\n---------------------------------------------------------------------------");
        System.out.println("Enter the test_prod_map function\n ");

        /*
        * DB preparation:
        *   1.Add Product Related Records:
        *       1). record in Product TABLE.
        *       2). record in ProductInstance TABLE.
        *       3). record in ProductDeploy TABLE.  #### keep product_device_id same with ClientDeviceMapping.
        * */

        //1.1 Add Product item.
        Product prod=new Product();
        prod.setCategory(5);
        prod.setDefaultVersion("2019-09-27");
        prod.setName("test_Product01");
        prod.setDescription("test_desc01");
        prod.setUuid("aabb-ccdd-eeff-gghh-iijj-001");
        ProdSrv.save(prod);

        //1.2 Add ProductInstance item.
        ProductInstance ProdInst=new ProductInstance();
        ProdInst.setAotEnable(true);
        ProdInst.setProductName("test_Product01");
        ProdInst.setBits("64");
        //ProdInst.setCpu("intel");
        ProdInst.setDescription("test_desc_01");
        ProdInst.setInstanceName("test_Inst_name_01");
        ProdInst.setMetadata("{\n" +
                "\"category\": \"software_product\", \n" +
                "\"version\": \"2019-8-12\", \n" +
                "    \"vendor\": \"intel\", \n" +
                "    \"description\": \"Intel Device Resource Management Library\", \n" +
                "    \"components\": [\n" +
                "        {\n" +
                "            \"h\": \"49f94f9a2a7169409c357900f05cc52e\", \n" +
                "            \"v\": \"2019-8-12\", \n" +
                "            \"f\": \"/i-daemon\"\n" +
                "        }, \n" +
                "        {\n" +
                "            \"h\": \"5db8c0e4f0d67037aa5d56180c30b2c7\", \n" +
                "            \"v\": \"2019-8-12\", \n" +
                "            \"f\": \"/default_lwm2m_device_type.cfg\"\n" +
                "        }\n" +
                "    ], \n" +
                "    \"product_name\": \"test_Product01\"\n" +
                "}");
        //ProdInst.setOs("os01");
        //ProdInst.setOsMin("1.0");
        ProdInst.setSystem("sys01");
        ProdInst.setSysMin("5.0");
        ProdInst.setWasmEnable(true);
        ProdInst.setVersion("2019-09-27");
        ProdInst.setUploadTime(new Date());
        ProdInstSrv.save(ProdInst);

        ProductInstance ProdInst2=new ProductInstance();
        ProdInst2.setAotEnable(true);
        ProdInst2.setProductName("test_Product01");
        ProdInst2.setBits("64");
        //ProdInst.setCpu("intel");
        ProdInst2.setDescription("test_desc_01");
        ProdInst2.setInstanceName("test_Inst_name_02");
        ProdInst2.setMetadata("{\n" +
                "\"category\": \"software_product\", \n" +
                "\"version\": \"2019-8-12\", \n" +
                "    \"vendor\": \"intel\", \n" +
                "    \"description\": \"Intel Device Resource Management Library\", \n" +
                "    \"components\": [\n" +
                "        {\n" +
                "            \"h\": \"49f94f9a2a7169409c357900f05cc52e\", \n" +
                "            \"v\": \"2019-8-12\", \n" +
                "            \"f\": \"/i-daemon\"\n" +
                "        }, \n" +
                "        {\n" +
                "            \"h\": \"5db8c0e4f0d67037aa5d56180c30b2c7\", \n" +
                "            \"v\": \"2019-8-12\", \n" +
                "            \"f\": \"/default_lwm2m_device_type.cfg\"\n" +
                "        }\n" +
                "    ], \n" +
                "    \"product_name\": \"test_Product01\"\n" +
                "}");
        ProdInst2.setSystem("sys01");
        ProdInst2.setSysMin("6.0");
        ProdInst2.setWasmEnable(true);
        ProdInst2.setVersion("2019-09-30");
        ProdInst2.setUploadTime(new Date());
        ProdInstSrv.save(ProdInst2);

        //1.2.a Add ProductDownloadPackage for the productInstance
        ProductDownloadPackage prdownpkg=new ProductDownloadPackage();
        prdownpkg.setCategory(5);
        prdownpkg.setGenDate(new Date("2019/09/27"));
        prdownpkg.setLastUsedTime(new Date());
        prdownpkg.setIsAot(true);
        prdownpkg.setSize(Long.parseLong("11111"));
        prdownpkg.setToId(ProdInst2.getInstanceId());
        prdownpkg.setFormat(".aot");
        prdownpkg.setHashcode("aabbccdd11223344");
        prdownpkg.setProductName("test_Product01");

        ProdDwnldPkgSrv.save(prdownpkg);

        //1.3 Add ProductDeploy item
        /**
         * deploy product must be found in Instance TABLE.
         * */
        ProductDeploy deloy=new ProductDeploy();
        deloy.setClientUuid("testuuid001");
        deloy.setIsAot(true);
        deloy.setProductDeviceId("test_prodevice_id");
        deloy.setVersion("2019-09-30");
        deloy.setProductName("test_Product01");
        ProdDeplSrv.save(deloy);

        //Query the id of the client by clientUuid
        AmsClient clt=AmsCltSrv.findByClientUUID("testuuid001");

        URI uri =new URI(base_uri+prod_map_uri);
        CoapClient client =new CoapClient(uri);

        JSONObject JnObj=new JSONObject();
        JnObj.put("short_id",clt.getId());
        JnObj.put("product_name","test_Product01");
        JnObj.put("product_device_id","test_prodevice_id");
        String payload=JnObj.toString();
        CoapResponse response=client.post(payload,50);/* format id : 50 means Media type is Application/json */

        if(response!=null){
            System.out.println("\nResponse");
            System.out.println(Utils.prettyPrint(response));
            if(response.getCode().toString().startsWith("2")){
                System.out.println("\n Prov_map test success ! ");
            }else{
                System.out.println("\n Prov_map test Fail ! ");
            }
            System.out.println("---------------------------------------------------------------------------\n\n\n");

        }else{
            System.out.println("\n Response null ! ");
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }
    }

    /* ams/v1/t POST Set template for client
    *
    * will update template_name in AmsClient TABLE.
    * Remove client will also delete the bind relationship.
    *
    * */
    public void test_SetTmplt() throws Exception{
        System.out.println("\n\n\n---------------------------------------------------------------------------");
        System.out.println("Enter the test_SetTmplt function \n");

        //Query 1 random template
        AmsTemplate tmplt=AmsTmpltSrv.findByName("temp01");
        String tmplt_name;
        if(tmplt!=null){
            tmplt_name=tmplt.getName();
        }else {
            System.out.println("\nThere is no template available\n");
            return;
        }
        //bind template and client;
        //Query the id of the client by clientUuid
        AmsClient clt=AmsCltSrv.findByClientUUID("testuuid001");
        int cid =clt.getId();

        URI uri =new URI(base_uri+tmplt_uri+"?cid=" + cid + "&tn=" + tmplt_name + "");
        CoapClient client =new CoapClient(uri);
        String payload="";
        CoapResponse response=client.post(payload,50);

        if(response!=null){
            System.out.println("\n Response");
            System.out.println(Utils.prettyPrint(response));
            if(response.getCode().toString().startsWith("2")){
                System.out.println("\n Set Template success ! ");
            }else{
                System.out.println("\n Set Template Fail ! ");
            }
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }else{
            System.out.println("\n Response is null ! ");
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }
    }

    /*ams/v1/t GET  Get template info from Server
    *
    * will Query by template_name of the client
    *
    * */
    public void test_GetTmplt() throws Exception{
        System.out.println("\n\n\n---------------------------------------------------------------------------");
        System.out.println(" Enter the test_GetTmplt function \n");

        //Query the id of the client by clientUuid
        AmsClient clt=AmsCltSrv.findByClientUUID("testuuid001");
        int cid =clt.getId();

        URI uri=new URI(base_uri+tmplt_uri+"?cid="+cid+"");
        CoapClient client=new CoapClient(uri);
        CoapResponse response=client.get();

        if(response!=null){
            System.out.println("\n Response");
            System.out.println(Utils.prettyPrint(response));
            if(response.getCode().toString().startsWith("2")){
                System.out.println("\n Get Template success ! ");
            }else{
                System.out.println("\n Get Template Fail ! ");
            }
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }else{
            System.out.println("\n Response is null ! ");
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }
    }

    /* ams/v1/p/installed POST */
    public void test_PostProdInst() throws Exception{
        System.out.println("\n\n\n---------------------------------------------------------------------------");
        System.out.println("Enter the test_PostProdInst function \n");

        URI uri=new URI(base_uri+test_PostInst_uri);
        CoapClient client=new CoapClient(uri);

        //query client info of testuuid001.
        AmsClient clt=AmsCltSrv.findByClientUUID("testuuid001");

        JSONObject JnObj=new JSONObject();
        JnObj.put("short_id",clt.getId());
        JnObj.put("ams_version","new_amsver_0924");
        JnObj.put("device_type","apple_device");
        JnObj.put("fw_version","new_fwver_0924");

        JSONObject wasmObj=new JSONObject();
        wasmObj.put("bytecode",true);
        wasmObj.put("aot",true);
        wasmObj.put("api_version",930); //integer
        JnObj.put("wasm",wasmObj);

        JSONArray inst_pro_list=new JSONArray();
//        JSONObject prodObj=new JSONObject();
//        prodObj.put("product_name","test_Product01");
//        prodObj.put("version","2018-09-27");
        JSONObject prodObj_2=new JSONObject();
        prodObj_2.put("product_name","test_Product01");
        prodObj_2.put("version","2019-09-27"); //set it different from Deploy version
        prodObj_2.put("aot",true);
//        inst_pro_list.put(prodObj);
        inst_pro_list.put(prodObj_2);
        JnObj.put("installed_product_list",inst_pro_list);
        String payload=JnObj.toString();
        System.out.println(payload);
        CoapResponse response=client.post(payload,50);

        if(response!=null){
            System.out.println("\n Response");
            System.out.println(Utils.prettyPrint(response));
            if(response.getCode().toString().startsWith("2")){
                System.out.println("\n Post Product Installation success ! ");
            }else{
                System.out.println("\n Post Product Installation Fail ! ");
            }
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }else{
            System.out.println("\n Response is null ! ");
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }
    }

    /* ams/v1/p/c?id=xxxx GET  */
    public void test_QryProdChg() throws Exception{
        System.out.println("\n\n\n---------------------------------------------------------------------------");
        System.out.println("Enter the test_QryProdChg function \n");
        //query short id of client
        AmsClient clt=AmsCltSrv.findByClientUUID("testuuid001");

        URI uri=new URI(base_uri+test_QryProdChg_uri+"?id="+clt.getId()+"");
        CoapClient client=new CoapClient(uri);
        CoapResponse response=client.get();

        if(response!=null){
            System.out.println("\n Response");
            System.out.println(Utils.prettyPrint(response));
            if(response.getCode().toString().startsWith("2")){
                System.out.println("\n Query Product changes Success ! ");
            }else{
                System.out.println("\n Query Product changes Fail ! ");
            }
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }else{
            System.out.println("\n Response is null ! ");
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }
    }

    /* ams/v1/p/d?id=xxx&cid GET
    * need to upload productDownloadPackage in AmsUserCloud
    * */
    public void test_DownInstPkg() throws Exception{
        System.out.println("\n\n\n---------------------------------------------------------------------------");
        System.out.println("Enter the test_DownInstPkg function \n");
        //query short id of client
        AmsClient clt=AmsCltSrv.findByClientUUID("testuuid001");
        ProductDownloadPackage pgk=ProdDwnldPkgSrv.findAll().get(0);

        URI uri =new URI(base_uri+test_DownInstPkg_uri+"?cid="+clt.getId()+"&id="+pgk.getId()+"");
        CoapClient client=new CoapClient(uri);
        CoapResponse response=client.get();

        if(response!=null){
            System.out.println("\n Response");
            System.out.println(Utils.prettyPrint(response));
            if(response.getCode().toString().startsWith("2")){
                System.out.println("\n Download Installation package Success ! ");
            }else{
                System.out.println("\n Download Installation package Fail ! ");
            }
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }else{
            System.out.println("\n Response is null ! ");
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }
    }

    /* ams/v1/c/q ? pn=xxx & tt=xx & tid = xxxx & cid = xxx */
    public void test_QryCfgInfo() throws Exception{
        System.out.println("\n\n\n---------------------------------------------------------------------------");
        System.out.println("Enter the test_QryCfgInfo function \n");

        //query short id of client
        AmsClient clt=AmsCltSrv.findByClientUUID("testuuid001");

        URI uri=new URI(base_uri+test_QryCfgInfo_uri+"?pn=test_Product01" +
                "&tt=device" +
                "&tid=sensor.H-T.renke.RS-WS-N01" +
                "&cid="+clt.getId()+"");

        CoapClient client=new CoapClient(uri);
        CoapResponse response=client.get();

        if(response!=null){
            System.out.println("\n Response");
            System.out.println(Utils.prettyPrint(response));
            if(response.getCode().toString().startsWith("2")){
                System.out.println("\n Query Config Information Success ! ");
            }else{
                System.out.println("\n Query Config Information Fail ! ");
            }
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }else{
            System.out.println("\n Response is null ! ");
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }

    }

    /* ams/v1/c/d ? id=xxxx & cid = xxxx GET*/
    public void test_QryDownCfgCt() throws Exception{
        System.out.println("\n\n\n---------------------------------------------------------------------------");
        System.out.println("Enter the test_QryDownCfgCt function \n");

        //query short id of client
        AmsClient clt=AmsCltSrv.findByClientUUID("testuuid001");
        //query random cfg content.
        CfgContent cfgct= CfgContSrv.findAll().get(0);

        URI uri=new URI(base_uri+test_QryDownCfgCt_uri+"?cid="+clt.getId()+"&id="+cfgct.getId()+"");
        CoapClient client=new CoapClient(uri);
        CoapResponse response=client.get();

        if(response!=null){
            System.out.println("\n Response");
            System.out.println(Utils.prettyPrint(response));
            if(response.getCode().toString().startsWith("2")){
                System.out.println("\n Query Download Config Information Success ! ");
            }else{
                System.out.println("\n Query Download Config Information Fail ! ");
            }
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }else{
            System.out.println("\n Response is null ! ");
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }
    }

    /* ams/v1/c/a?cid=xxxx POST */
    public void test_PostAllCfgInfo() throws Exception{
        System.out.println("\n\n\n---------------------------------------------------------------------------");
        System.out.println("Enter the test_PostAllCfgInfo function \n");

        //query short id of client
        AmsClient clt=AmsCltSrv.findByClientUUID("testuuid001");
        URI uri=new URI(base_uri+test_AllCfgInfo_uri+"?cid="+clt.getId()+"");
        CoapClient client=new CoapClient(uri);

        /**
         * will post following cfg to server side. Be saved in CfgCurrent TABLE.
         * and there are no requirements of cfg.
         * */
        JSONArray JnArr=new JSONArray();
        JSONObject CfgObj_1=new JSONObject();
        CfgObj_1.put("pn","iagent");
        CfgObj_1.put("tt","modbus_type");
        CfgObj_1.put("tid","test_targetId_1");
        JSONObject CfgObj_2=new JSONObject();
        CfgObj_2.put("pn","iagent");
        CfgObj_2.put("tt","modbus_type");
        CfgObj_2.put("tid","test_targetId_2");

        JnArr.put(CfgObj_1);
        JnArr.put(CfgObj_2);

        String payload = JnArr.toString();
        CoapResponse response=client.post(payload,50);

        if(response!=null){
            System.out.println("\n Response");
            System.out.println(Utils.prettyPrint(response));
            if(response.getCode().toString().startsWith("2")){
                System.out.println("\n Post All Config Information Success ! ");
            }else{
                System.out.println("\n Post All Config Information Fail ! ");
            }
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }else{
            System.out.println("\n Response is null ! ");
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }
    }

    /* ams/v1/c/a GET */
    public void test_QryAllCfgInfo() throws Exception{
        System.out.println("\n\n\n---------------------------------------------------------------------------");
        System.out.println("Enter the test_QryAllCfgInfo function \n");

        //query short id of client
        AmsClient clt=AmsCltSrv.findByClientUUID("testuuid001");
        URI uri=new URI(base_uri+test_AllCfgInfo_uri+"?cid="+clt.getId()+"");

        CoapClient client=new CoapClient(uri);
        CoapResponse response=client.get();

        if(response!=null){
            System.out.println("\n Response");
            System.out.println(Utils.prettyPrint(response));
            if(response.getCode().toString().startsWith("2")){
                System.out.println("\n Query All Config Information Success ! ");
            }else{
                System.out.println("\n Query All Config Information Fail ! ");
            }
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }else{
            System.out.println("\n Response is null ! ");
            System.out.println("---------------------------------------------------------------------------\n\n\n");
        }
    }


}

package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.server.client.ProcessServicesClient;

public class StartProcessDBAccessTest extends TestCase {

    public void testRest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        ProcessServicesClient processClient = KieServerRestUtils.getProcessServicesClient();
        long processInstanceId = processClient.startProcess(CONTAINER_ID, "project1.helloDBAccess", params);

        System.out.println("startProcess() : processInstanceId = " + processInstanceId);



    }
}
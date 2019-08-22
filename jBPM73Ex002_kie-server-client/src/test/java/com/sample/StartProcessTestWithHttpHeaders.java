package com.sample;

import static com.sample.Constants.BASE_URL;
import static com.sample.Constants.CONTAINER_ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.server.api.KieServerConstants;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;

public class StartProcessTestWithHttpHeaders extends TestCase {

    public void testRest() throws Exception {

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(BASE_URL, "kieserver", "kieserver1!");
        List<String> capabilities = new ArrayList<String>();
        capabilities.add(KieServerConstants.CAPABILITY_BPM);
        config.setCapabilities(capabilities);
        config.setTimeout(60000L);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "kie-server Java Client");
        headers.put("More-Headers", "XXXX");
        config.setHeaders(headers);
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);
        ProcessServicesClient processServiceClient = client.getServicesClient(ProcessServicesClient.class);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processVar1", "XXX2");

        long processInstanceId = processServiceClient.startProcess(CONTAINER_ID, "project1.helloProcess", params);

        System.out.println("startProcess() : processInstanceId = " + processInstanceId);

    }
}
package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.server.client.ProcessServicesClient;

public class HelloTaskTest extends TestCase {

    public void testRest() throws Exception {

        for (int i = 0; i < 30; i++) {
            Map<String, Object> params = new HashMap<String, Object>();
            ProcessServicesClient processClient = KieServerRestUtils.getProcessServicesClient();
            long processInstanceId = processClient.startProcess("helloTask_1.0.0", "src.helloTask", params);

            System.out.println("startProcess() : processInstanceId = " + processInstanceId);

        }

    }
}
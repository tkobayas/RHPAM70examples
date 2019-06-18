package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.server.api.model.cases.CaseFile;
import org.kie.server.client.CaseServicesClient;
import org.kie.server.client.ProcessServicesClient;

public class GetCaseFileTest extends TestCase {

    public void testRest() throws Exception {

        CaseServicesClient caseClient = KieServerRestUtils.getCaseServicesClient("kieserver", "kieserver1!");
        

//        Map<String, Object> caseInstanceData = caseClient.getCaseInstanceData(CONTAINER_ID, "SUB-0000000004");
        Map<String, Object> caseInstanceData = caseClient.getCaseInstanceData(CONTAINER_ID, "HR-0000000002");

        System.out.println("caseInstanceData = " + caseInstanceData);



    }
}
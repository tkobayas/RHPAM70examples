package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.server.api.model.cases.CaseFile;
import org.kie.server.client.CaseServicesClient;
import org.kie.server.client.ProcessServicesClient;

public class TriggerSubCaseTest extends TestCase {

    public void testRest() throws Exception {

        CaseServicesClient caseClient = KieServerRestUtils.getCaseServicesClient("kieserver", "kieserver1!");
        

        caseClient.triggerAdHocFragment(CONTAINER_ID, "SUB-0000000004", "Sub Case", null);

        System.out.println("triggerAdHocFragment() : done");



    }
}
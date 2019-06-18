package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.server.api.model.cases.CaseFile;
import org.kie.server.client.CaseServicesClient;
import org.kie.server.client.ProcessServicesClient;

public class StartCaseTest extends TestCase {

    public void testRest() throws Exception {

        CaseServicesClient caseClient = KieServerRestUtils.getCaseServicesClient("kieserver", "kieserver1!");
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "john");
        CaseFile caseFile = CaseFile.builder()
                .addUserAssignments("owner", "kieserver")
                .data(data)
                .build();
        
        String caseId = caseClient.startCase(CONTAINER_ID, "CaseWithSubCase", caseFile);
//        String caseId = caseClient.startCase(CONTAINER_ID, "CaseWithSubCase");

        System.out.println("startCase() : caseId = " + caseId);

    }
}
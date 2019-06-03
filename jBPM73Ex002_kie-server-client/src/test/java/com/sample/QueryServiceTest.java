package com.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.client.QueryServicesClient;

public class QueryServiceTest extends TestCase {

    public void testRest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();
        
        List<ProcessDefinition> processes = queryClient.findProcesses(0, 10);
        System.out.println("Available processes: " + processes);

    }
}
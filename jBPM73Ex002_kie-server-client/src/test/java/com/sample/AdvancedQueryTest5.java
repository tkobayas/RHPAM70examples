package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;

public class AdvancedQueryTest5 extends TestCase {

    public void testRest() throws Exception {

        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("maxProcessInstanceId", 3);
        params.put("status", 2);

        List<ProcessInstance> processInstances = queryClient.query
                ("getAllProcessInstances", "ProcessInstances", "test", params, 0, 10, ProcessInstance.class);

              System.out.println(processInstances);

    }
}
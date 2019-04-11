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

public class AdvancedQueryTest3 extends TestCase {

    public void testRest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();
        

        List<ProcessInstance> processInstances = queryClient.query
                ("getAllProcessInstances", "ProcessInstances", 0, 10, ProcessInstance.class);

              System.out.println(processInstances);

    }
}
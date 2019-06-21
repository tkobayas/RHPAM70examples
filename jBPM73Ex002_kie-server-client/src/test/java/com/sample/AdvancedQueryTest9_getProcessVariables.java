package com.sample;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.util.QueryFilterSpecBuilder;
import org.kie.server.client.QueryServicesClient;

public class AdvancedQueryTest9_getProcessVariables extends TestCase {

    public void testRest() throws Exception {

        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();
        
        QueryFilterSpec filterSpec = new QueryFilterSpecBuilder().in("processInstanceId", Arrays.asList(1, 3)).get();
       
        List<ProcessInstance> processInstances = queryClient.query("jbpmProcessInstancesWithVariables", QueryServicesClient.QUERY_MAP_PI_WITH_VARS, filterSpec, 0, 100, ProcessInstance.class);
        for (ProcessInstance processInstance : processInstances) {
            System.out.println(processInstance);
            System.out.println(processInstance.getVariables());
        }
    }
}
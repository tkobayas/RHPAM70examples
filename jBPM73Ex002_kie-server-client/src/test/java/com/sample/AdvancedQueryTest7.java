package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.model.instance.TaskWithProcessDescription;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;

public class AdvancedQueryTest7 extends TestCase {

    public void testRest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();
        
        List<TaskWithProcessDescription> query = queryClient.query("getAllTaskInstancesWithVariables", QueryServicesClient.QUERY_MAP_TASK_WITH_VARS, 0, 100, TaskWithProcessDescription.class);
        for (TaskWithProcessDescription obj : query) {
            System.out.println(obj);
        }

    }
}
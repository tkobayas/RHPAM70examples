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

public class AdvancedQueryTest6 extends TestCase {

    public void testRest() throws Exception {

        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();
        

        QueryDefinition queryDefinition = QueryDefinition.builder().name("getTask1")
                .expression("select t.* from Task t, VariableInstanceLog v where t.processInstanceId = v.processInstanceId")
                .source("java:jboss/MySqlDS")
                .target("TASK").build();
        //queryClient.unregisterQuery("getTask1");
        queryClient.registerQuery(queryDefinition);
        List<TaskInstance> query = queryClient.query("getTask1", QueryServicesClient.QUERY_MAP_TASK_WITH_VARS, 0, 100, TaskInstance.class);
        for (TaskInstance taskInstance : query) {
            System.out.println(taskInstance);
        }


    }
}
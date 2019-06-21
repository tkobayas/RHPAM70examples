package com.sample;

import java.util.List;

import junit.framework.TestCase;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.QueryServicesClient;

public class AdvancedQueryTest8_getTasksWithProcessVariables extends TestCase {

    public void testRest() throws Exception {

        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();
        
        queryClient.unregisterQuery("getTasksWithProcessVariables");

        // Abusing TaskInstance.inputData (TVNAME/TVVALUE) for process variables
        QueryDefinition queryDefinition = QueryDefinition.builder().name("getTasksWithProcessVariables")
                .expression("select t.*, v.variableId TVNAME, v.value TVVALUE from AuditTaskImpl t, VariableInstanceLog v where t.processInstanceId = v.processInstanceId")
                .source("java:jboss/MySqlDS")
                .target("CUSTOM").build();
        queryClient.registerQuery(queryDefinition);
        
        List<TaskInstance> query = queryClient.query("getTasksWithProcessVariables", QueryServicesClient.QUERY_MAP_TASK_WITH_VARS, 0, 100, TaskInstance.class);
        for (TaskInstance taskInstance : query) {
            System.out.println(taskInstance);
            System.out.println(taskInstance.getInputData());
        }
    }
}
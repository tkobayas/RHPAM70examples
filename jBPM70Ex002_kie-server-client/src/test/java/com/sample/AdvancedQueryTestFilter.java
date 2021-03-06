package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.server.api.model.definition.ProcessInstanceField;
import org.kie.server.api.model.definition.ProcessInstanceQueryFilterSpec;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.definition.TaskField;
import org.kie.server.api.model.definition.TaskQueryFilterSpec;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.util.ProcessInstanceQueryFilterSpecBuilder;
import org.kie.server.api.util.TaskQueryFilterSpecBuilder;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;

public class AdvancedQueryTestFilter extends TestCase {

    public void testRest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();
        
//        List<QueryDefinition> queryDefs = queryClient.getQueries(0, 10);
//        System.out.println(queryDefs);
        
        TaskQueryFilterSpec filterSpec = new TaskQueryFilterSpecBuilder().equalsTo(TaskField.ID, "1").equalsTo(TaskField.ID, "2").get();


        List<TaskInstance> tasks = queryClient.query
                ("getAllTaskInstancesA", "UserTasks", filterSpec, 0, 10, TaskInstance.class);

              System.out.println(tasks);

    }
}
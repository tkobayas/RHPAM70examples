package com.sample;

import static com.sample.Constants.BASE_URL;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import junit.framework.TestCase;
import org.kie.server.api.KieServerConstants;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.util.QueryFilterSpecBuilder;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.UserTaskServicesClient;

public class GetTasksAndVariblesTest2 extends TestCase {

    public void testRest() throws Exception {

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(BASE_URL, "rhpamAdmin", "password1!");
        List<String> capabilities = new ArrayList<String>();
        capabilities.add(KieServerConstants.CAPABILITY_BPM);
        config.setCapabilities(capabilities);
        config.setTimeout(600000);
        KieServicesClient kieServicesClient = KieServicesFactory.newKieServicesClient(config);

        System.out.println("==== get Tasks assigned to rhpamAdmin");
        UserTaskServicesClient userTaskServicesClient = kieServicesClient.getServicesClient(UserTaskServicesClient.class);
        List<org.kie.server.api.model.instance.TaskSummary> taskList;
        taskList = userTaskServicesClient.findTasksAssignedAsPotentialOwner("rhpamAdmin", 0, 100);
        for (org.kie.server.api.model.instance.TaskSummary taskSummary : taskList) {
            System.out.println("taskSummary = " + taskSummary);
        }

        // Collect process instance IDs of the tasks
        List<Long> processInstanceIds = taskList.stream().map(t -> t.getProcessInstanceId()).distinct().collect(Collectors.toList());
        System.out.println("processInstanceIds = " + processInstanceIds);

        System.out.println("==== get Variables of the process instances");
        QueryServicesClient queryClient = kieServicesClient.getServicesClient(QueryServicesClient.class);
        
        QueryFilterSpec filterSpec = new QueryFilterSpecBuilder().in("processInstanceId", processInstanceIds).get();
       
        List<ProcessInstance> processInstances = queryClient.query("jbpmProcessInstancesWithVariables", QueryServicesClient.QUERY_MAP_PI_WITH_VARS, filterSpec, 0, 100, ProcessInstance.class);
        for (ProcessInstance processInstance : processInstances) {
            System.out.println(processInstance);
            System.out.println(processInstance.getVariables());
        }
    }
}
package com.sample;

import static com.sample.Constants.BASE_URL;
import static com.sample.Constants.CONTAINER_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import junit.framework.TestCase;
import org.kie.server.api.KieServerConstants;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UserTaskServicesClient;

public class GetTasksAndVariblesTest extends TestCase {

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
        ProcessServicesClient processServiceClient = kieServicesClient.getServicesClient(ProcessServicesClient.class);
        
        for (Long processInstanceId : processInstanceIds) {
            Map<String, Object> varMap = processServiceClient.getProcessInstanceVariables(CONTAINER_ID, processInstanceId);
            System.out.println("processInstance[" + processInstanceId + "] : variables = " + varMap);
        }
    }
}
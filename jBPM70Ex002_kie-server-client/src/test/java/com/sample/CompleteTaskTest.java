package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.api.task.model.Status;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UserTaskServicesClient;

public class CompleteTaskTest extends TestCase {

    public void testRest() throws Exception {

        UserTaskServicesClient taskClient = KieServerRestUtils.getUserTaskServicesClient("rhpamAdmin", "password1!");

        List<org.kie.server.api.model.instance.TaskSummary> taskList;
        taskList = taskClient.findTasksAssignedAsPotentialOwner("rhpamAdmin", 0, 100);
        for (org.kie.server.api.model.instance.TaskSummary taskSummary : taskList) {
            System.out.println("taskSummary.getId() = " + taskSummary.getId() + ", staus = " + taskSummary.getStatus());
            long taskId = taskSummary.getId();
            if (taskSummary.getStatus().equals("Reserved")) {
                taskClient.startTask(CONTAINER_ID, taskId, "rhpamAdmin");
            }
            taskClient.completeTask(CONTAINER_ID, taskId, "rhpamAdmin", null);
        }

    }
}
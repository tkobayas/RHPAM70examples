package com.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.server.api.model.instance.TaskEventInstance;
import org.kie.server.client.UserTaskServicesClient;

public class TaskEventTest extends TestCase {

    public void testRest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        UserTaskServicesClient taskClient = KieServerRestUtils.getUserTaskServicesClient();
        
        List<TaskEventInstance> taskEvents = taskClient.findTaskEvents(1L, 0, 10);
        for (TaskEventInstance taskEventInstance : taskEvents) {
            System.out.println(taskEventInstance);
        }
    }
}
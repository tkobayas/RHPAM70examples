package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.client.ProcessServicesClient;

public class GetProcessDefinition extends TestCase {

    public void testRest() throws Exception {

        ProcessServicesClient processServiceClient = KieServerRestUtils.getProcessServicesClient();
        
        ProcessDefinition processDefinition = processServiceClient.getProcessDefinition(CONTAINER_ID, "project1.helloTimer");
        System.out.println(new XStream().toXML(processDefinition));
    }
}
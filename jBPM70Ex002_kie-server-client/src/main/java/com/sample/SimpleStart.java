package com.sample;

import static com.sample.Constants.BASE_URL;
import static com.sample.Constants.CONTAINER_ID;

import java.util.HashSet;

import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;

public class SimpleStart {

    public static void main(String[] args) {

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(BASE_URL, "kieserver", "kieserver1!");
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        //        classes.add(MyPojo.class);
        config.addJaxbClasses(classes);
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);

        ProcessServicesClient proessClient = client.getServicesClient(ProcessServicesClient.class);

        Long startProcess = proessClient.startProcess(CONTAINER_ID, "project1.helloProcess");

        System.out.println(startProcess);

    }
}

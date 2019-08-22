package com.sample;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.definition.QueryParam;
import org.kie.server.api.util.QueryFilterSpecBuilder;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.QueryServicesClient;

public class AdvancedQueryTest12Count2 extends TestCase {

    private static final String QUERY_NAME = "jbpmProcessInstances";
    private static final String STATUS_COLUMN = "status";

    public void testRest() throws Exception {

        String serverUrl = "http://localhost:8080/kie-server/services/rest/server";
        String user = "kieserver";
        String password = "kieserver1!";

        KieServicesConfiguration configuration = KieServicesFactory.newRestConfiguration(serverUrl, user, password);

        configuration.setMarshallingFormat(MarshallingFormat.JAXB);
        KieServicesClient kieServicesClient = KieServicesFactory.newKieServicesClient(configuration);

        QueryServicesClient queryClient = kieServicesClient.getServicesClient(QueryServicesClient.class);

        QueryFilterSpec spec = new QueryFilterSpecBuilder().get();
        QueryParam[] params = new QueryParam[2];
        params[0] = new QueryParam(STATUS_COLUMN, "EQUALS_TO", Arrays.asList(1)); // filter only active processInstance (status = 1)
        QueryParam count = new QueryParam("processInstanceId", "COUNT", Arrays.asList("processInstanceId")); // This filter *count* the number of rows
        params[1] = count;
        spec.setParameters(params);

        List<?> result = queryClient.query(QUERY_NAME, QueryServicesClient.QUERY_MAP_RAW, spec, 0, 10, List.class);

        System.out.println("result = " + result);
    }

}
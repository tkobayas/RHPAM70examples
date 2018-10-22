package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;

public class AdvancedQueryRegisterTest extends TestCase {

    public void testRest() throws Exception {

        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();
        



        QueryDefinition query = new QueryDefinition();
        query.setName("getAllTaskInstancesA");
        query.setSource("java:jboss/MySqlDS");

        query.setExpression("select ti.* " +
          "from AuditTaskImpl ti");

        query.setTarget("CUSTOM");

        queryClient.registerQuery(query);

        List<QueryDefinition> queryDefs = queryClient.getQueries(0, 10);
        System.out.println(queryDefs);
    }
}
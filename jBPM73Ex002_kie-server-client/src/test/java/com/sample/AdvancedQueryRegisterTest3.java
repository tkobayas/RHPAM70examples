package com.sample;

import java.util.List;

import junit.framework.TestCase;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.client.QueryServicesClient;

public class AdvancedQueryRegisterTest3 extends TestCase {

    public void testRest() throws Exception {

        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();
        

        QueryDefinition query = new QueryDefinition();
        query.setName("getAllProcessInstances");
        query.setSource("java:jboss/MySqlDS");

        query.setExpression("select * from processinstancelog");

        query.setTarget("CUSTOM");

        queryClient.registerQuery(query);

//        List<QueryDefinition> queryDefs = queryClient.getQueries(0, 10);
//        System.out.println(queryDefs);
    }
}
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

public class AdvancedQueryRegisterTest2 extends TestCase {

    public void testRest() throws Exception {

        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();
        



        QueryDefinition query = new QueryDefinition();
        query.setName("getAllTaskInstancesWithVariables");
        query.setSource("java:jboss/MySqlDS");

        query.setExpression("select ti.*, tv.name tvname, tv.value tvvalue from AuditTaskImpl ti " +
                "inner join (select tv.taskId, tv.name, tv.value from TaskVariableImpl tv where tv.type = 0 ) tv "+
                "on (tv.taskId = ti.taskId)");

        query.setTarget("CUSTOM");

        queryClient.registerQuery(query);

        List<QueryDefinition> queryDefs = queryClient.getQueries(0, 10);
        System.out.println(queryDefs);
    }
}
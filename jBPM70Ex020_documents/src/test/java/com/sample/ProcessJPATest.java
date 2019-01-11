package com.sample;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.h2.tools.Server;
import org.jbpm.document.Document;
import org.jbpm.document.Documents;
import org.jbpm.document.marshalling.DocumentMarshallingStrategy;
import org.jbpm.document.marshalling.DocumentsMarshallingStrategy;
import org.jbpm.document.service.impl.DocumentImpl;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JBPMHelper;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

/**
 * This is a sample file to launch a process.
 */
public class ProcessJPATest {

    private static final boolean H2 = false;

    private static EntityManagerFactory emf;

    private static Server h2Server;
    private static PoolingDataSource ds;

    @Before
    public void setup() {

        if (H2) {
            // for H2 datasource
            h2Server = JBPMHelper.startH2Server();
            ds = JBPMHelper.setupDataSource();
        } else {
            // for external database datasource
            ds = setupDataSource();
        }

        Map configOverrides = new HashMap();
        configOverrides.put("hibernate.hbm2ddl.auto", "create"); // comment out if you don't want to clean up tables
        if (H2) {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        } else {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect"); // Change for other DB
        }
        emf = Persistence.createEntityManagerFactory("org.jbpm.domain", configOverrides);
    }

    @After
    public void teardown() {
        if (ds != null) {
            ds.close();
        }
        if (h2Server != null) {
            h2Server.shutdown();
        }
    }

    @Test
    public void testProcess() throws Exception {

        try {

            RuntimeManager manager = getRuntimeManager("helloDocuments.bpmn");
            RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
            KieSession ksession = runtime.getKieSession();

            // start a new process instance
            Map<String, Object> params = new HashMap<String, Object>();
            ProcessInstance pi = ksession.startProcess("helloDocument.helloDocuments", params);
            System.out.println("A process instance started : pid = " + pi.getId());

            TaskService taskService = runtime.getTaskService();

            // ------------
            {
                List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("rhpamAdmin", "en-UK");
                for (TaskSummary taskSummary : list) {
                    System.out.println("rhpamAdmin starts a task : taskId = " + taskSummary.getId());
                    taskService.start(taskSummary.getId(), "rhpamAdmin");

                    List<Document> documents = getDocuments();
                    Documents docs = new Documents(documents);
                    HashMap<String, Object> result = new HashMap<String, Object>();
                    result.put("outDocs", docs);

                    taskService.complete(taskSummary.getId(), "rhpamAdmin", result);
                }
            }

            // -----------
//            {
//                List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("rhpamAdmin", "en-UK");
//                for (TaskSummary taskSummary : list) {
//                    System.out.println("rhpamAdmin starts a task : taskId = " + taskSummary.getId());
//                    taskService.start(taskSummary.getId(), "rhpamAdmin");
//                    taskService.complete(taskSummary.getId(), "rhpamAdmin", null);
//                }
//            }

            // -----------
            manager.disposeRuntimeEngine(runtime);

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private Document getDocument(String documentName) {
        Document documentOne = new DocumentImpl();
        documentOne.setIdentifier(documentName);
        documentOne.setLastModified(new Date());
        documentOne.setLink("http://" + documentName);
        documentOne.setName(documentName + "-Name");
        documentOne.setSize(1);
        documentOne.setContent(documentName.getBytes());
        return documentOne;
    }

    private List<Document> getDocuments() {
        
        List<Document> documents = new ArrayList<>();
        
        documents.add(getDocument("documentOne"));
        documents.add(getDocument("documentTwo"));
        
        return documents;
    }
    
    private static RuntimeManager getRuntimeManager(String process) {
        Properties properties = new Properties();
        properties.setProperty("krisv", "");
        properties.setProperty("mary", "");
        properties.setProperty("rhpamAdmin", "");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault().persistence(true)
                .entityManagerFactory(emf).userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2).get();
        
        ((SimpleRuntimeEnvironment)environment).getEnvironmentTemplate().set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{
                new DocumentsMarshallingStrategy(new DocumentMarshallingStrategy()),
                new SerializablePlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT  )
                 });
        
        return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);

    }

    public static PoolingDataSource setupDataSource() {
        // Please edit here when you want to use your database
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
        pds.getDriverProperties().put("user", "mysql");
        pds.getDriverProperties().put("password", "mysql");
        pds.getDriverProperties().put("url", "jdbc:mysql://localhost:3306/testrhpam700?pinGlobalTxToPhysicalConnection=true");
        pds.getDriverProperties().put("driverClassName", "com.mysql.jdbc.Driver");
        pds.init();
        return pds;
    }
}
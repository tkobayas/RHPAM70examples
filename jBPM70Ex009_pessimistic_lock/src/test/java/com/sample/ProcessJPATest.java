package com.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.h2.tools.Server;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JBPMHelper;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a sample file to launch a process.
 */
public class ProcessJPATest {

    private Logger logger = LoggerFactory.getLogger(ProcessJPATest.class);

    private static final boolean H2 = false;

    private static EntityManagerFactory emf;

    private static Server h2Server;
    private static PoolingDataSource ds;

    @Before
    public void setup() {
        
        System.setProperty("org.jbpm.runtime.manager.ppi.lock", "false");

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
        configOverrides.put("javax.persistence.lock.timeout", "10000");

        if (H2) {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        } else {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"); // Change for other DB
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

            RuntimeManager manager = getRuntimeManager("sample.bpmn");
            RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
            KieSession ksession = runtime.getKieSession();

            // start a new process instance
            Map<String, Object> params = new HashMap<String, Object>();
            ProcessInstance pi = ksession.startProcess("com.sample.bpmn.hello", params);
            System.out.println("A process instance started : pid = " + pi.getId());

            TaskService taskService = runtime.getTaskService();

            // ------------
            {
                List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
                for (TaskSummary taskSummary : list) {
                    System.out.println("john starts a task : taskId = " + taskSummary.getId());
                    taskService.start(taskSummary.getId(), "john");
                    taskService.complete(taskSummary.getId(), "john", null);
                }
            }

            // -----------
            manager.disposeRuntimeEngine(runtime);

            ExecutorService executor = Executors.newFixedThreadPool(2);

            executor.execute(new Runnable() {

                public void run() {
                    logger.info("run");

                    RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(1L));
                    KieSession ksession = runtime.getKieSession();
                    ProcessInstance processInstance = ksession.getProcessInstance(1L);
                    logger.info(processInstance.toString());
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    logger.info("sleep finished");
                }
            });
            
            executor.execute(new Runnable() {

                public void run() {
                    logger.info("run");

                    
                    RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(1L));
                    KieSession ksession = runtime.getKieSession();
                    ProcessInstance processInstance = ksession.getProcessInstance(1L);
                    logger.info(processInstance.toString());
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    logger.info("sleep finished");
                }
            });

            logger.info("----------------------------------");
            
            executor.shutdown();
            executor.awaitTermination(300, TimeUnit.SECONDS);

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static RuntimeManager getRuntimeManager(String process) {
        Properties properties = new Properties();
        properties.setProperty("krisv", "");
        properties.setProperty("mary", "");
        properties.setProperty("john", "");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault()
                .persistence(true)
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2)
                .get();
        
        ((org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment)environment).getEnvironmentTemplate().set("org.kie.api.persistence.pessimistic", true);
        
        return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);

    }

    public static PoolingDataSource setupDataSource() {
        // Please edit here when you want to use your database
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("org.postgresql.xa.PGXADataSource");
        pds.getDriverProperties().put("user", "postgres");
        pds.getDriverProperties().put("password", "postgres");
        pds.getDriverProperties().put("url", "jdbc:postgresql://localhost:5432/testrhpam700");
        pds.getDriverProperties().put("driverClassName", "org.postgresql.Driver");
        pds.init();
        return pds;
    }
}
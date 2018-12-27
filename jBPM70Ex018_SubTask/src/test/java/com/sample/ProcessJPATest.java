package com.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.EventService;
import org.kie.internal.task.api.InternalTaskService;

/**
 * This is a sample file to launch a process.
 */
public class ProcessJPATest {

    private static final boolean H2 = true;

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
//            ds = setupDataSource();
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

            RuntimeManager runtimeManager = getRuntimeManager("com.myspace.myproj3.helloSubTask.v1.0.bpmn2");
            RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get());
            KieSession ksession = runtimeEngine.getKieSession();
            TaskService taskService = runtimeEngine.getTaskService();
            
            // Adding a custom TaskLifeCycleEventListener
            // For kie-server, you can do it in kie-deployment-descriptor.xml (using Business Central GUI)
            ((EventService<TaskLifeCycleEventListener>)taskService).registerTaskEventListener(new MyTaskLifeCycleEventListener(runtimeManager));
            
            // start a new process instance
            Map<String, Object> params = new HashMap<String, Object>();
            ProcessInstance pi = ksession.startProcess("src.helloSubTask", params);
            long processInstanceId = pi.getId();
            System.out.println("A process instance started : processInstanceId = " + processInstanceId); // ParentTask is created here
            
            // Continue the process
            ksession.signalEvent("Signal1", null, processInstanceId); // SubTasks are created here

            {
                // Start a ParentTask by john (not yet complete)
                List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
                for (TaskSummary taskSummary : list) {
                    long taskId = taskSummary.getId();
                    System.out.println("john starts a task : taskId = " + taskId);
                    taskService.start(taskId, "john");
                    
                    System.out.println("How many pending subtasks for the ParentTask? = " + ((InternalTaskService)taskService).getPendingSubTasksByParent(taskId));
                    
                    // Not complete yet
                    //taskService.complete(taskSummary.getId(), "rhpamAdmin", null);
                }
            }
            
            // ------------
            {
                // Start and complete SubTasks by mary
                List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
                for (TaskSummary taskSummary : list) {
                    long taskId = taskSummary.getId();
                    System.out.println("mary starts and completes a task : taskId = " + taskId);
                    taskService.start(taskId, "mary");
                    taskService.complete(taskId, "mary", null);
                }
            }
            
            // You will see ParentTask is automatically completed upon all SubTasks completion

            // -----------
            runtimeManager.disposeRuntimeEngine(runtimeEngine);

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static RuntimeManager getRuntimeManager(String process) {
        Properties properties = new Properties();
        properties.setProperty("Administrator", "Administrators");
        properties.setProperty("john", "");
        properties.setProperty("mary", "");

        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault().persistence(true)
                .entityManagerFactory(emf).userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2).get();
        return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);

    }

//    public static PoolingDataSource setupDataSource() {
//        // Please edit here when you want to use your database
//        PoolingDataSource pds = new PoolingDataSource();
//        pds.setUniqueName("jdbc/jbpm-ds");
//        pds.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
//        pds.getDriverProperties().put("user", "mysql");
//        pds.getDriverProperties().put("password", "mysql");
//        pds.getDriverProperties().put("url", "jdbc:mysql://localhost:3306/testrhpam700?pinGlobalTxToPhysicalConnection=true");
//        pds.getDriverProperties().put("driverClassName", "com.mysql.jdbc.Driver");
//        pds.init();
//        return pds;
//    }
}
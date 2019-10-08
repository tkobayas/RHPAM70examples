package com.sample;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
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
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.test.util.db.DataSourceFactory;
import org.kie.test.util.db.PoolingDataSourceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a sample file to launch a process.
 */
public class ProcessJPATest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessJPATest.class);

    private static EntityManagerFactory emf;

    private static PoolingDataSourceWrapper ds;

    @Before
    public void setup() {

        ds = setupPoolingDataSource();

        Map configOverrides = new HashMap();
        configOverrides.put("hibernate.hbm2ddl.auto", "create"); // comment out if you don't want to clean up tables
        configOverrides.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");

        emf = Persistence.createEntityManagerFactory("org.jbpm.domain", configOverrides);
    }

    @After
    public void teardown() {
        if (ds != null) {
            ds.close();
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
            params.put("Property_1", "john");
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

            //----
            {
                Task task = taskService.getTaskById(2);
                List<OrganizationalEntity> excludedOwners = ((InternalPeopleAssignments)task.getPeopleAssignments()).getExcludedOwners();
                System.out.println("excludedOwners = " + excludedOwners);
            }
            
            // -----------
            {
                List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
                if (list.isEmpty()) {
                    System.out.println("john cannot claim Task2 because john is set to ExcludedOwnerId");
                }
                for (TaskSummary taskSummary : list) {
                    System.out.println("john starts a task : taskId = " + taskSummary.getId());
                    taskService.start(taskSummary.getId(), "john");
                    taskService.complete(taskSummary.getId(), "john", null);
                }
            }

            // -----------
            manager.disposeRuntimeEngine(runtime);

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static RuntimeManager getRuntimeManager(String process) {
        Properties properties = new Properties();
        properties.setProperty("krisv", "group1");
        properties.setProperty("mary", "group1");
        properties.setProperty("john", "group1");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault().persistence(true).entityManagerFactory(emf).userGroupCallback(userGroupCallback).addAsset(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2).get();
        return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);

    }

    public static PoolingDataSourceWrapper setupPoolingDataSource() {
        // Please edit here when you want to use your database
        Properties driverProperties = new Properties();
        driverProperties.put("user", "mysql");
        driverProperties.put("password", "mysql");
        driverProperties.put("databaseName", "testrhpam740");
        driverProperties.put("portNumber", "3306");
        driverProperties.put("serverName", "localhost");
        driverProperties.put("url", "jdbc:mysql://localhost:3306/testrhpam730?pinGlobalTxToPhysicalConnection=true");
        driverProperties.put("driverClassName", "com.mysql.jdbc.Driver");
        driverProperties.put("className", "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");

        PoolingDataSourceWrapper pds = null;
        try {
            pds = DataSourceFactory.setupPoolingDataSource("jdbc/jbpm-ds", driverProperties);
        } catch (Exception e) {
            e.printStackTrace();
            
//            logger.warn("DBPOOL_MGR:Looks like there is an issue with creating db pool because of " + e.getMessage() + " cleaing up...");
//            logger.debug("DBPOOL_MGR: attempting to create db pool again...");
//            pds = DataSourceFactory.setupPoolingDataSource("jdbc/jbpm-ds", driverProperties);
//            logger.debug("DBPOOL_MGR:Pool created after cleanup of leftover resources");
        }
        return pds;
    }

}
package com.sample;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.core.command.assertion.AssertEquals;
import org.h2.tools.Server;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.core.timer.BusinessCalendarImpl;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JBPMHelper;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.executor.ExecutorService;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.AuditService;
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
    private ExecutorService executorService;

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

        executorService = ExecutorServiceFactory.newExecutorService(emf);

        executorService.init();

    }

    @After
    public void teardown() {
        executorService.destroy();
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
            long piid = -1;

            {
                RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
                KieSession ksession = runtime.getKieSession();

                // start a new process instance
                Map<String, Object> params = new HashMap<String, Object>();
                ProcessInstance pi = ksession.startProcess("com.sample.bpmn.hello", params);
                System.out.println("A process instance started : pid = " + pi.getId());
                piid = pi.getId();

                // -----------
                manager.disposeRuntimeEngine(runtime);
            }
            
            System.out.println("--- sleep start");

            Thread.sleep(10000);
            
            System.out.println("--- sleep end");


            {
                RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(piid));
                AuditService auditService = runtime.getAuditService();
                ProcessInstanceLog processInstanceLog = (ProcessInstanceLog) auditService.findProcessInstance(piid);

                processInstanceLog.getSlaCompliance();
                if (ProcessInstance.SLA_VIOLATED == processInstanceLog.getSlaCompliance()) {
                    System.out.println("SLA_VIOLATED !");
                } else {
                    System.out.println("slaCompliance = " + processInstanceLog.getSlaCompliance());
                }
            }

            System.out.println("finish");
            
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
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(new MyProcessEventListener());
                        return listeners;
                    }
                })
                .get();
        
        ((org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment)environment).getEnvironmentTemplate().set("jbpm.business.calendar", new BusinessCalendarImpl());

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
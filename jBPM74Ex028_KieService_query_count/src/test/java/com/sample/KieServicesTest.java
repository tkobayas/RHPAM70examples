package com.sample;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.query.SqlQueryDefinition;
import org.jbpm.kie.services.impl.query.mapper.ProcessInstanceQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.RawListQueryMapper;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.query.QueryService;
import org.jbpm.services.api.query.model.QueryParam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.query.QueryFilter;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
public class KieServicesTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(KieServicesTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "domain-services.jar")                
                .addPackage("org.jbpm.services.task")
                .addPackage("org.jbpm.services.task.wih") // work items org.jbpm.services.task.wih
                .addPackage("org.jbpm.services.task.annotations")
                .addPackage("org.jbpm.services.task.api")
                .addPackage("org.jbpm.services.task.impl")
                .addPackage("org.jbpm.services.task.events")
                .addPackage("org.jbpm.services.task.exception")
                .addPackage("org.jbpm.services.task.identity")
                .addPackage("org.jbpm.services.task.factories")
                .addPackage("org.jbpm.services.task.internals")
                .addPackage("org.jbpm.services.task.internals.lifecycle")
                .addPackage("org.jbpm.services.task.lifecycle.listeners")
                .addPackage("org.jbpm.services.task.query")
                .addPackage("org.jbpm.services.task.util")
                .addPackage("org.jbpm.services.task.commands") // This should not be required here
                .addPackage("org.jbpm.services.task.deadlines") // deadlines
                .addPackage("org.jbpm.services.task.deadlines.notifications.impl")
                .addPackage("org.jbpm.services.task.subtask")
                .addPackage("org.jbpm.services.task.rule")
                .addPackage("org.jbpm.services.task.rule.impl")
                .addPackage("org.jbpm.services.task.audit.service")

                .addPackage("org.kie.internal.runtime.manager")
                .addPackage("org.kie.internal.runtime.manager.context")
                .addPackage("org.kie.internal.runtime.manager.cdi.qualifier")
                
                .addPackage("org.jbpm.runtime.manager.impl")
                .addPackage("org.jbpm.runtime.manager.impl.cdi")                               
                .addPackage("org.jbpm.runtime.manager.impl.factory")
                .addPackage("org.jbpm.runtime.manager.impl.jpa")
                .addPackage("org.jbpm.runtime.manager.impl.manager")
                .addPackage("org.jbpm.runtime.manager.impl.task")
                .addPackage("org.jbpm.runtime.manager.impl.tx")
                
                .addPackage("org.jbpm.shared.services.api")
                .addPackage("org.jbpm.shared.services.impl")
                .addPackage("org.jbpm.shared.services.impl.tx")
                
                .addPackage("org.jbpm.kie.services.api")
                .addPackage("org.jbpm.kie.services.impl")                
                .addPackage("org.jbpm.kie.services.api.bpmn2")
                .addPackage("org.jbpm.kie.services.impl.bpmn2")
                .addPackage("org.jbpm.kie.services.impl.event.listeners")
                .addPackage("org.jbpm.kie.services.impl.audit")
                .addPackage("org.jbpm.kie.services.impl.form")
                .addPackage("org.jbpm.kie.services.impl.form.provider")
                .addPackage("org.jbpm.kie.services.impl.query")  
                .addPackage("org.jbpm.kie.services.impl.query.mapper")  
                .addPackage("org.jbpm.kie.services.impl.query.persistence")  
                .addPackage("org.jbpm.kie.services.impl.query.preprocessor")  
                
                .addPackage("org.jbpm.services.cdi")
                .addPackage("org.jbpm.services.cdi.impl")
                .addPackage("org.jbpm.services.cdi.impl.form")
                .addPackage("org.jbpm.services.cdi.impl.manager")
                .addPackage("org.jbpm.services.cdi.producer")
                .addPackage("org.jbpm.services.cdi.impl.security")
                .addPackage("org.jbpm.services.cdi.impl.query")
                
                .addPackage("org.jbpm.test.util")
                .addPackage("org.jbpm.kie.services.test")
                .addPackage("org.jbpm.services.cdi.test") // Identity Provider Test Impl here
                .addClass("org.jbpm.services.cdi.test.util.CDITestHelperNoTaskService")
                .addClass("org.jbpm.services.cdi.test.util.CountDownDeploymentListenerCDIImpl")
                .addClass("org.jbpm.kie.services.test.objects.CoundDownDeploymentListener")
                .addAsResource("jndi.properties", "jndi.properties")
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

    }
    
    @Override
    protected void close() {
        // do nothing here and let CDI close resources
    }

    @Override
    protected void configureServices() {
        // do nothing here and let CDI configure services 
    }

    @Inject 
    @Override
    public void setDeploymentService(DeploymentService deploymentService) {
        
        super.setDeploymentService(deploymentService);
    }

    @Inject
    @Override
    public void setBpmn2Service(DefinitionService bpmn2Service) {
        
        super.setBpmn2Service(bpmn2Service);
    }

    @Inject
    @Override
    public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
        
        super.setRuntimeDataService(runtimeDataService);
    }

    @Inject
    @Override
    public void setProcessService(ProcessService processService) {
        
        super.setProcessService(processService);
    }

    @Inject
    @Override
    public void setUserTaskService(UserTaskService userTaskService) {
        
        super.setUserTaskService(userTaskService);
    }
    
    @Inject
    @Override
    public void setQueryService(QueryService queryService) {
        
        super.setQueryService(queryService);
    }
    
    @Before
    public void prepare() {
        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("sample.bpmn");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = KieMavenRepository.getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);

        ReleaseId releaseId3 = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, "1.1.0");

        InternalKieModule kJar3 = createKieJar(ks, releaseId3, processes);
        File pom3 = new File("target/kmodule3", "pom.xml");
        pom3.getParentFile().mkdirs();
        try {
            FileOutputStream fs = new FileOutputStream(pom3);
            fs.write(getPom(releaseId3).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        repository = KieMavenRepository.getKieMavenRepository();
        repository.deployArtifact(releaseId3, kJar3, pom3);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                try {
                deploymentService.undeploy(unit);
                } catch (Exception e) {
                    // do nothing in case of some failed tests to avoid next test to fail as well
                }
            }
            units.clear();
        }
        close();
    }

    @Test
    public void testQuery() {
        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        assertNotNull(processService);

        for (int i = 0; i < 5; i++) {
            processService.startProcess(deploymentUnit.getIdentifier(), "com.sample.bpmn.hello");
        }

        SqlQueryDefinition query = new SqlQueryDefinition("getAllProcessInstances", "jdbc/testDS1");
        query.setExpression("select * from processinstancelog");
        queryService.registerQuery(query);
        
        QueryParam count = new QueryParam("processInstanceId", "COUNT", Arrays.asList("processInstanceId"));
        
        List<?> results = queryService.query("getAllProcessInstances", RawListQueryMapper.get(), new QueryFilter(0, 100), count);
        //logger.info("results.size() = " + results.size());
        for (Object obj : results) {
            System.out.println("obj = " + obj);
        }
        
        SqlQueryDefinition query2 = new SqlQueryDefinition("getTasksAssignedAsPotentialOwner", "jdbc/testDS1");
        query2.setExpression("select * from peopleassignments_potowners where entity_id = 'john'");
        queryService.registerQuery(query2);
        
        QueryParam count2 = new QueryParam("task_id", "COUNT", Arrays.asList("task_id"));
        
        List<?> results2 = queryService.query("getTasksAssignedAsPotentialOwner", RawListQueryMapper.get(), new QueryFilter(0, 100), count2);
        //logger.info("results2.size() = " + results2.size());
        for (Object obj : results2) {
            System.out.println("obj = " + obj);
        }
    }
}
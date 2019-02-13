package com.sample;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.appformer.maven.integration.MavenRepository;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.eclipse.aether.repository.RemoteRepository;
import org.h2.tools.Server;
import org.jbpm.kie.services.impl.FormManagerServiceImpl;
import org.jbpm.kie.services.impl.KModuleDeploymentService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.ProcessServiceImpl;
import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.kie.services.impl.UserTaskServiceImpl;
import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl;
import org.jbpm.kie.services.impl.query.QueryServiceImpl;
import org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.query.QueryService;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.TaskAuditServiceFactory;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.test.JBPMHelper;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.LoggerFactory;

/**
 * This is a sample file to launch a process.
 */
public class JbpmKieServicesTest {
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JbpmKieServicesTest.class);


    private static final boolean H2 = true;

    private static EntityManagerFactory emf;

    private static Server h2Server;
    private static PoolingDataSource ds;

    protected static final String ARTIFACT_ID = "test-module";
    protected static final String GROUP_ID = "org.jbpm.test";
    protected static final String VERSION = "1.0.0-SNAPSHOT";

    protected DeploymentService deploymentService;
    protected DefinitionService bpmn2Service;
    protected RuntimeDataService runtimeDataService;
    protected ProcessService processService;
    protected UserTaskService userTaskService;
    protected QueryService queryService;

    protected TestIdentityProvider identityProvider;

    private KModuleDeploymentUnit deploymentUnit = null;

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
        configOverrides.put("hibernate.hbm2ddl.auto", "create"); // Uncomment if you don't want to clean up tables
        if (H2) {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        } else {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect"); // Change for other DB
        }
        emf = Persistence.createEntityManagerFactory("org.jbpm.domain", configOverrides);
        EntityManagerFactoryManager.get().addEntityManagerFactory("org.jbpm.domain", emf);

        //        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.domain");
        identityProvider = new TestIdentityProvider();

        // build definition service
        bpmn2Service = new BPMN2DataServiceImpl();

        queryService = new QueryServiceImpl();
        ((QueryServiceImpl) queryService).setIdentityProvider(identityProvider);
        ((QueryServiceImpl) queryService).setCommandService(new TransactionalCommandService(emf));
        ((QueryServiceImpl) queryService).init();

        // build deployment service
        deploymentService = new KModuleDeploymentService();
        ((KModuleDeploymentService) deploymentService).setBpmn2Service(bpmn2Service);
        ((KModuleDeploymentService) deploymentService).setEmf(emf);
        ((KModuleDeploymentService) deploymentService).setIdentityProvider(identityProvider);
        ((KModuleDeploymentService) deploymentService).setManagerFactory(new RuntimeManagerFactoryImpl());
        ((KModuleDeploymentService) deploymentService).setFormManagerService(new FormManagerServiceImpl());

        TaskService taskService = HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf).getTaskService();

        // build runtime data service
        runtimeDataService = new RuntimeDataServiceImpl();
        ((RuntimeDataServiceImpl) runtimeDataService).setCommandService(new TransactionalCommandService(emf));
        ((RuntimeDataServiceImpl) runtimeDataService).setIdentityProvider(identityProvider);
        ((RuntimeDataServiceImpl) runtimeDataService).setTaskService(taskService);
        ((RuntimeDataServiceImpl) runtimeDataService).setTaskAuditService(TaskAuditServiceFactory.newTaskAuditServiceConfigurator().setTaskService(taskService).getTaskAuditService());
        ((KModuleDeploymentService) deploymentService).setRuntimeDataService(runtimeDataService);

        // set runtime data service as listener on deployment service
        ((KModuleDeploymentService) deploymentService).addListener(((RuntimeDataServiceImpl) runtimeDataService));
        ((KModuleDeploymentService) deploymentService).addListener(((BPMN2DataServiceImpl) bpmn2Service));

        // build process service
        processService = new ProcessServiceImpl();
        ((ProcessServiceImpl) processService).setDataService(runtimeDataService);
        ((ProcessServiceImpl) processService).setDeploymentService(deploymentService);

        // build user task service
        userTaskService = new UserTaskServiceImpl();
        ((UserTaskServiceImpl) userTaskService).setDataService(runtimeDataService);
        ((UserTaskServiceImpl) userTaskService).setDeploymentService(deploymentService);

        // Create KJAR
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

        assertNotNull(deploymentService);

        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentUnit.setStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);

        deploymentService.deploy(deploymentUnit);
        assertNotNull(processService);
    }

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources) {
        return createKieJar(ks, releaseId, resources, null);
    }

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources, Map<String, String> extraResources) {

        KieFileSystem kfs = createKieFileSystemWithKProject(ks);
        kfs.writePomXML(getPom(releaseId));

        for (String resource : resources) {
            kfs.write("src/main/resources/KBase-test/" + resource, ResourceFactory.newClassPathResource(resource));
        }
        if (extraResources != null) {
            for (Map.Entry<String, String> entry : extraResources.entrySet()) {
                kfs.write(entry.getKey(), ResourceFactory.newByteArrayResource(entry.getValue().getBytes()));
            }
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        if (!kieBuilder.buildAll().getResults().getMessages().isEmpty()) {
            for (Message message : kieBuilder.buildAll().getResults().getMessages()) {
                logger.error("Error Message: ({}) {}", message.getPath(), message.getText());
            }
            throw new RuntimeException("There are errors builing the package, please check your knowledge assets!");
        }

        return (InternalKieModule) kieBuilder.getKieModule();
    }

    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test").setDefault(true).addPackage("*")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );


        kieBaseModel1.newKieSessionModel("ksession-test").setDefault(true)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") )
                .newWorkItemHandlerModel("Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()");

        kieBaseModel1.newKieSessionModel("ksession-test-2").setDefault(false)
        .setType(KieSessionModel.KieSessionType.STATEFUL)
        .setClockType( ClockTypeOption.get("realtime") )
        .newWorkItemHandlerModel("Log", "new org.jbpm.kie.services.test.objects.KieConteinerSystemOutWorkItemHandler(kieContainer)");

        kieBaseModel1.newKieSessionModel("ksession-test2").setDefault(false)
        .setType(KieSessionModel.KieSessionType.STATEFUL)
        .setClockType( ClockTypeOption.get("realtime") );

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }
    
    protected String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                "  <version>" + releaseId.getVersion() + "</version>\n" +
                "\n";
        if (dependencies != null && dependencies.length > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += "  <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += "  <version>" + dep.getVersion() + "</version>\n";
                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        pom += "</project>";
        return pom;
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

            long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "com.sample.bpmn.hello");
            System.out.println("process instance started: processInstanceId = " + processInstanceId);
            
            {
                List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
                for (TaskSummary taskSummary : tasks) {
                    userTaskService.start(taskSummary.getId(), "john");
                    userTaskService.complete(taskSummary.getId(), "john", null);
                }
            }
            
            {
                List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
                for (TaskSummary taskSummary : tasks) {
                    userTaskService.start(taskSummary.getId(), "mary");
                    userTaskService.complete(taskSummary.getId(), "mary", null);
                }
            }

        } catch (Throwable th) {
            th.printStackTrace();
        }
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
    
    private static Path generateSettingsXml( Path m2Folder ) throws IOException {
        String settingsXml =
                "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"\n" +
                "      xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "      xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0\n" +
                "                          http://maven.apache.org/xsd/settings-1.0.0.xsd\">\n" +
                "  <profiles>\n" +
                "    <profile>\n" +
                "      <id>repos</id>\n" +
                "      <activation>\n" +
                "        <activeByDefault>true</activeByDefault>\n" +
                "      </activation>\n" +
                "      <repositories>\n" +
                "        <repository>\n" +
                "          <id>myTestRepo</id>\n" +
                "          <name>My Test Repo</name>\n" +
                "          <url>" + m2Folder.toUri().toURL().toExternalForm() + "</url>\n" +
                "          <releases><enabled>true</enabled></releases>\n" +
                "          <snapshots><enabled>true</enabled></snapshots>\n" +
                "        </repository>\n" +
                "    </repositories>\n" +
                "    </profile>\n" +
                "  </profiles>\n" +
                "</settings>\n";

        Path settingsXmlPath = Files.createTempFile( m2Folder, "settings", ".xml" );
        Files.write( settingsXmlPath, settingsXml.getBytes() );
        return settingsXmlPath;
    }

    private static RemoteRepository createRemoteRepository(Path m2Folder) throws MalformedURLException {
        String localRepositoryUrl = m2Folder.toUri().toURL().toExternalForm();
        return new RemoteRepository.Builder( "myTestRepo", "default", localRepositoryUrl ).build();
    }
}
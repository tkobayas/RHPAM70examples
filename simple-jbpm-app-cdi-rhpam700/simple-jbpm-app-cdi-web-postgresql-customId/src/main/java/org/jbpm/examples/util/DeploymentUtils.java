package org.jbpm.examples.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.cdi.Kjar;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;

@ApplicationScoped
public class DeploymentUtils {

    public static final String GROUP_ID = "com.sample";
    public static final String ARTIFACT_ID = "simple-jbpm-kjar";
    public static final String VERSION = "1.0.0-SNAPSHOT";
    public static final String DEPLOYMENT_ID = GROUP_ID + ":" + ARTIFACT_ID + ":" + VERSION;

    public static final String CONTAINER_ID = "proj2_1.0.0"; // Use to match with Process Server

    @Inject
    @Kjar
    DeploymentService deploymentService;
    
    @Inject
    private ProcessService processService;
    
    @Inject
    private RuntimeDataService runtimeDataService;

    public void init() {
        //doUndeploy();
        System.out.println("DeploymentUtils.init()");
        //        System.setProperty("org.jbpm.ht.callback", "custom");
        //        System.setProperty("org.jbpm.ht.custom.callback", "org.jbpm.examples.util.RewardsUserGroupCallback");

        DeploymentUnit deploymentUnit = new CustomIdKmoduleDeploymentUnit(CONTAINER_ID, GROUP_ID, ARTIFACT_ID, VERSION);
        if (deploymentService.isDeployed(deploymentUnit.getIdentifier())) {
            System.out.println(deploymentUnit.getIdentifier() + " is already deployed");
        } else {
            deploymentService.deploy(deploymentUnit);
        }
    }

    // convenient method for cleanup
    public void doUndeploy() {
        System.out.println("DeploymentUtils.doUndeploy()");

        DeploymentUnit deploymentUnit = new CustomIdKmoduleDeploymentUnit(DEPLOYMENT_ID, GROUP_ID, ARTIFACT_ID, VERSION);
        if (deploymentService.isDeployed(deploymentUnit.getIdentifier())) {
            System.out.println("Undeploy : " + deploymentUnit.getIdentifier());
            List<Integer> states = new ArrayList<>();
            states.add(ProcessInstance.STATE_ACTIVE);
            Collection<ProcessInstanceDesc> processInstances = runtimeDataService.getProcessInstancesByDeploymentId(DEPLOYMENT_ID, states, new QueryContext());
            for (ProcessInstanceDesc processInstanceDesc : processInstances) {
                processService.abortProcessInstance(processInstanceDesc.getId());
            }
            
            deploymentService.undeploy(deploymentUnit);
        }
    }

    @Produces
    public DeploymentService getDeploymentService() {
        return deploymentService;
    }
}

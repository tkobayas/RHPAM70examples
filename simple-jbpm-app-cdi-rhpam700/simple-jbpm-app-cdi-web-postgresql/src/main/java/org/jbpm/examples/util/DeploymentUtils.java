package org.jbpm.examples.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.cdi.Kjar;

@ApplicationScoped
public class DeploymentUtils {

    public static final String GROUP_ID = "com.sample";
    public static final String ARTIFACT_ID = "simple-jbpm-kjar";
    public static final String VERSION = "1.0.0-SNAPSHOT";
    public static final String DEPLOYMENT_ID = GROUP_ID + ":" + ARTIFACT_ID + ":" + VERSION;

    @Inject
    @Kjar
    DeploymentService deploymentService;

    public void init() {
        System.out.println("DeploymentUtils.init()");
        //        System.setProperty("org.jbpm.ht.callback", "custom");
        //        System.setProperty("org.jbpm.ht.custom.callback", "org.jbpm.examples.util.RewardsUserGroupCallback");

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        if (deploymentService.isDeployed(deploymentUnit.getIdentifier())) {
            System.out.println(deploymentUnit.getIdentifier() + " is already deployed");
        } else {
            deploymentService.deploy(deploymentUnit);
        }

    }

    @Produces
    public DeploymentService getDeploymentService() {
        return deploymentService;
    }
}

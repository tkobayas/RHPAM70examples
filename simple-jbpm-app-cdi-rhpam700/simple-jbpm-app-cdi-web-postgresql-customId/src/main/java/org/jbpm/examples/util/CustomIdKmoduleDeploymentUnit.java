package org.jbpm.examples.util;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;

public class CustomIdKmoduleDeploymentUnit extends KModuleDeploymentUnit {

    private static final long serialVersionUID = 1L;
    private String id;

    public CustomIdKmoduleDeploymentUnit(String id, String groupId, String artifactId, String version) {
        super(groupId, artifactId, version);
        this.id = id;
    }

    @Override
    public String getIdentifier() {
        return this.id;
    }
}
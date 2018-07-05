package org.jbpm.examples.util;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kie.internal.identity.IdentityProvider;

@ApplicationScoped
public class CustomIdentityProvider implements IdentityProvider {

    private List<String> roles = new ArrayList<String>();

    @PostConstruct
    public void init() {
        roles.add("PM");
    }

    public String getName() {
        return "John";
    }

    public List<String> getRoles() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

}

package org.jbpm.examples.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

@ApplicationScoped
public class EnvironmentProducer {

    private EntityManagerFactory emf;

    @PersistenceUnit(unitName = "org.jbpm.domain")
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        //System.out.println("** setEntityManagerFactory ** : " + emf);
        this.emf = emf;
    }

    @ApplicationScoped
    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        //System.out.println("** getEntityManagerFactory **");
        if (this.emf == null) {
            this.emf = Persistence.createEntityManagerFactory("org.jbpm.domain");
        }
        return this.emf;
    }

}

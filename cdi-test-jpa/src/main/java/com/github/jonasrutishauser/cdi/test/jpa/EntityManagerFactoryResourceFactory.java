package com.github.jonasrutishauser.cdi.test.jpa;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManagerFactory;

import org.jboss.logging.Logger;
import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

public class EntityManagerFactoryResourceFactory implements ResourceReferenceFactory<EntityManagerFactory> {
    private static final Logger LOG = Logger.getLogger(EntityManagerFactoryResourceFactory.class);

    private TestEntityResources testEntityResources;
    private final String persistenceUnit;

    public EntityManagerFactoryResourceFactory(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }

    @Override
    public ResourceReference<EntityManagerFactory> createResource() {
        if (testEntityResources == null) {
            testEntityResources = CDI.current().select(TestEntityResources.class).get();
        }
        return new ResourceReference<EntityManagerFactory>() {
            EntityManagerFactory emf;

            @Override
            public EntityManagerFactory getInstance() {
                emf = testEntityResources.resolveEntityManagerFactory(persistenceUnit);
                return emf;
            }

            @Override
            public void release() {
                LOG.trace("closing EntityManagerFactory" + emf);
                emf.close();
            }
        };
    }
}

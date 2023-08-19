package com.github.jonasrutishauser.cdi.test.jpa;

import org.jboss.logging.Logger;
import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.EntityManagerFactory;

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
        return new ResourceReference<>() {
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

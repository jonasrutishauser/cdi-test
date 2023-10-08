package com.github.jonasrutishauser.cdi.test.jpa;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManager;

import org.jboss.logging.Logger;
import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

public class EntityManagerResourceFactory implements ResourceReferenceFactory<EntityManager>, InvocationHandler {
    private static final Logger LOG = Logger.getLogger(EntityManagerResourceFactory.class);

    private final String persistenceUnit;
    private TestEntityResources testEntityResources;


    public EntityManagerResourceFactory(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }

    @Override
    public ResourceReference<EntityManager> createResource() {
        if (testEntityResources == null) {
            testEntityResources = CDI.current().select(TestEntityResources.class).get();
        }
        return new ResourceReference<EntityManager>() {
            @Override
            public EntityManager getInstance() {
                return (EntityManager) Proxy.newProxyInstance(EntityManager.class.getClassLoader(),
                        new Class<?>[] {EntityManager.class}, EntityManagerResourceFactory.this);
            }

            @Override
            public void release() {
                LOG.trace("closing EntityManager will be done by TestEntityResources");
            }
        };
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(testEntityResources.resolveEntityManager(persistenceUnit), args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}

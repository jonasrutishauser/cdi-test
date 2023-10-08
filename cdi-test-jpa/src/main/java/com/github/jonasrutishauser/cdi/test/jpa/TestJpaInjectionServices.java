package com.github.jonasrutishauser.cdi.test.jpa;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceProperty;
import javax.persistence.PersistenceUnit;

import org.jboss.weld.injection.spi.JpaInjectionServices;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

public class TestJpaInjectionServices implements JpaInjectionServices {
    public static final String DEFAULT_TEST_PERSISTENCE_UNIT = "cdi-test";

    @Override
    public ResourceReferenceFactory<EntityManager> registerPersistenceContextInjectionPoint(
            InjectionPoint injectionPoint) {
        PersistenceContext persistenceContext = injectionPoint.getAnnotated().getAnnotation(PersistenceContext.class);
        if (persistenceContext == null) {
            throw new IllegalStateException(
                    "no @PersistenceContext annotation found on injection point " + injectionPoint);
        }
        String persistenceUnit = persistenceContext.unitName().trim().isEmpty() ? DEFAULT_TEST_PERSISTENCE_UNIT
                : persistenceContext.unitName();
        TestEntityResources.addAdditionalProperties(persistenceUnit, convert(persistenceContext.properties()));
        return new EntityManagerResourceFactory(persistenceUnit);
    }

    private Map<String, String> convert(PersistenceProperty[] properties) {
        return Arrays.stream(properties)
                .collect(Collectors.toMap(PersistenceProperty::name, PersistenceProperty::value));
    }

    @Override
    public ResourceReferenceFactory<EntityManagerFactory> registerPersistenceUnitInjectionPoint(
            InjectionPoint injectionPoint) {
        PersistenceUnit persistenceUnit = injectionPoint.getAnnotated().getAnnotation(PersistenceUnit.class);
        if (persistenceUnit == null) {
            throw new IllegalStateException(
                    "no @PersistenceUnit annotation found on injection point " + injectionPoint);
        }
        return new EntityManagerFactoryResourceFactory(
                persistenceUnit.unitName().trim().isEmpty() ? DEFAULT_TEST_PERSISTENCE_UNIT : persistenceUnit.unitName());
    }

    @Override
    public void cleanup() {
        // nothing
    }

}

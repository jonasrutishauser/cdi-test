package com.github.jonasrutishauser.cdi.test.jpa;

import static java.util.Collections.emptyMap;
import static org.eclipse.persistence.config.PersistenceUnitProperties.CDI_BEANMANAGER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TARGET_SERVER;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.transaction.JTA11TransactionController;
import org.eclipse.persistence.transaction.JTATransactionController;

import com.arjuna.ats.jta.TransactionManager;
import com.arjuna.ats.jta.common.jtaPropertyManager;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@TestScoped
public class TestEntityResources {

    static {
        try {
            EclipselinkSupport.install();
        } catch (Throwable t) {
            // ignore
        }
    }

    private static Map<String, Map<String, String>> additionalProperties = new ConcurrentHashMap<>();

    static void addAdditionalProperties(String persistenceUnit, Map<String, String> properties) {
        if (!properties.isEmpty()) {
            additionalProperties.computeIfAbsent(persistenceUnit, key -> new ConcurrentHashMap<>()).putAll(properties);
        }
    }

    private final Map<String, EntityManager> entityManagers = new ConcurrentHashMap<>();
    private final Map<String, EntityManagerFactory> entityManagerFactories = new ConcurrentHashMap<>();

    private final BeanManager beanManager;

    TestEntityResources() {
        this(null);
    }

    @Inject
    TestEntityResources(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    public EntityManager resolveEntityManager(String name) {
        return entityManagers.computeIfAbsent(name, this::createEntityManager);
    }

    public EntityManagerFactory resolveEntityManagerFactory(String name) {
        return entityManagerFactories.computeIfAbsent(name, this::createEntityManagerFactory);
    }

    private EntityManager createEntityManager(String name) {
        return resolveEntityManagerFactory(name).createEntityManager();
    }

    private EntityManagerFactory createEntityManagerFactory(String persistenceUnit) {
        Map<String, Object> properties = new HashMap<>();
        properties.putAll(additionalProperties.getOrDefault(persistenceUnit, emptyMap()));
        properties.put(CDI_BEANMANAGER, beanManager);
        properties.put(TARGET_SERVER, "org.eclipse.persistence.transaction.JTA11TransactionController");
        return Persistence.createEntityManagerFactory(persistenceUnit, properties);
    }

    @PreDestroy
    void closeResources() {
        entityManagers.values().forEach(EntityManager::close);
        entityManagers.clear();
        entityManagerFactories.values().forEach(EntityManagerFactory::close);
        entityManagerFactories.clear();
    }

    private static class EclipselinkSupport {
        private static void install() {
            JTATransactionController.setDefaultTransactionManager(TransactionManager.transactionManager());
            JTA11TransactionController.setDefaultTransactionSynchronizationRegistry(
                    jtaPropertyManager.getJTAEnvironmentBean().getTransactionSynchronizationRegistry());
        }
    }
}

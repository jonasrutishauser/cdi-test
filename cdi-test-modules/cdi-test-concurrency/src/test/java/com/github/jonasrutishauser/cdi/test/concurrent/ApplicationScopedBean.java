package com.github.jonasrutishauser.cdi.test.concurrent;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.concurrent.ManagedScheduledExecutorService;
import jakarta.enterprise.concurrent.ManagedThreadFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class ApplicationScopedBean {
    @Inject
    private ManagedExecutorService executorService;

    @Inject
    private ManagedScheduledExecutorService scheduledExecutorService;

    @Inject
    private ManagedThreadFactory threadFactory;

    void eagerInit(@Observes @Initialized(ApplicationScoped.class) Object event) {
        assertNotNull(executorService);
        assertNotNull(scheduledExecutorService);
        assertNotNull(threadFactory);
    }
}

package com.github.jonasrutishauser.cdi.test.concurrent;

import static java.util.concurrent.TimeUnit.SECONDS;

import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.glassfish.enterprise.concurrent.AbstractManagedExecutorService.RejectPolicy;
import org.glassfish.enterprise.concurrent.ManagedScheduledExecutorServiceImpl;
import org.glassfish.enterprise.concurrent.ManagedThreadFactoryImpl;

import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

@ApplicationScoped
class DefaultResources {

    @Produces
    @TestScoped
    ManagedScheduledExecutorService createDefaultService() {
        return new ManagedScheduledExecutorServiceImpl("default", null, 0, false, 10, 0, SECONDS, 0, null,
                RejectPolicy.ABORT);
    }

    void stopDefaultService(@Disposes ManagedScheduledExecutorService executorService) {
        executorService.shutdownNow();
    }

    @Produces
    @TestScoped
    ManagedThreadFactory createThreadFactory() {
        return new ManagedThreadFactoryImpl("default");
    }

    void stopDefaultService(@Disposes ManagedThreadFactory threadFactory) {
        if (threadFactory instanceof ManagedThreadFactoryImpl) {
            ((ManagedThreadFactoryImpl) threadFactory).stop();
        }
    }
}

package com.github.jonasrutishauser.cdi.test.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;
import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Test and demo lifecycle events.
 */
@ExtendWith(CdiTestJunitExtension.class)
class NotificationLifecyleTest {

    @Inject
    private NotificationLifecyleSupportBean supportBean;

    @Test
    void notifyInitialized() {
        assertNotNull(supportBean.initializedEvent);
    }

    @Test
    void notifyDestroyed() {
        assertNotNull(supportBean.destroyedEvent);
    }

    @Singleton
    public static class NotificationLifecyleSupportBean {
        TestInfo initializedEvent;
        TestInfo destroyedEvent;

        protected void observeInitialized(@Observes @Initialized(TestScoped.class) TestInfo testEvent) {
            this.initializedEvent = testEvent;
        }

        protected void observeDestroyed(@Observes @Destroyed(TestScoped.class) TestInfo testEvent) {
            this.destroyedEvent = testEvent;
        }
    }
}

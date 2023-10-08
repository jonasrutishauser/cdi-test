package com.github.jonasrutishauser.cdi.test.core.context;

import java.util.HashMap;

import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundSessionContext;
import org.jboss.weld.context.bound.MutableBoundRequest;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.BeforeDestroyed;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.context.control.RequestContextController;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

@ApplicationScoped
public class ContextControl {

    private final TestContext testContext;
    private final Event<TestInfo> event;

    private final BoundSessionContext sessionContext;
    private final BoundConversationContext conversationContext;
    private final RequestContextController requestContextController;

    ContextControl() {
        this(null, null, null, null, null);
    }
    
    @Inject
    ContextControl(BeanManager beanManager, Event<TestInfo> event, BoundSessionContext sessionContext,
            BoundConversationContext conversationContext, RequestContextController requestContextController) {
        this.testContext = (TestContext) beanManager.getContext(TestScoped.class);
        this.event = event;
        this.sessionContext = sessionContext;
        this.conversationContext = conversationContext;
        this.requestContextController = requestContextController;
    }

    public void start(TestInfo testInfo) {
        testContext.setTestInfo(testInfo);
        event.select(Initialized.Literal.of(TestScoped.class)).fire(testInfo);
        HashMap<String, Object> sessionStore = new HashMap<>();
        sessionContext.associate(sessionStore);
        sessionContext.activate();
        conversationContext.associate(new MutableBoundRequest(new HashMap<>(), sessionStore));
        conversationContext.activate();
        requestContextController.activate();
    }

    public void preStop() {
        event.select(BeforeDestroyed.Literal.of(TestScoped.class)).fire(testContext.getTestInfo());
    }

    public TestInfo stop() {
        requestContextController.deactivate();
        conversationContext.deactivate();
        sessionContext.invalidate();
        sessionContext.deactivate();
        sessionContext.dissociate(null);
        TestInfo testInfo = testContext.clearTestInfo();
        event.select(Destroyed.Literal.of(TestScoped.class)).fire(testInfo);
        return testInfo;
    }

}

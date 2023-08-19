package com.github.jonasrutishauser.cdi.test.core.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.annotation.Annotation;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.core.context.beans.ApplicationScopedBean;
import com.github.jonasrutishauser.cdi.test.core.context.beans.ConversationScopedBean;
import com.github.jonasrutishauser.cdi.test.core.context.beans.RequestScopedBean;
import com.github.jonasrutishauser.cdi.test.core.context.beans.ScopedBean;
import com.github.jonasrutishauser.cdi.test.core.context.beans.SessionScopedBean;
import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.context.control.RequestContextController;
import jakarta.inject.Inject;

@ExtendWith(CdiTestJunitExtension.class)
class ContextControlTest {

    @Inject
    private RequestScopedBean requestScopedBean;
    @Inject
    private ApplicationScopedBean applicationScopedBean;
    @Inject
    private SessionScopedBean sessionScopedBean;
    @Inject
    private ConversationScopedBean conversationScopedBean;
    @Inject
    private ContextControl contextControl;

    @Inject
    private RequestContextController requestContextController;

    @Test
    void restartRequestStopAll() {
        runTestStopAll(requestScopedBean);
    }

    @Test
    void restartSessionStopAll() {
        runTestStopAll(sessionScopedBean);
    }

    @Test
    void restartConversationStopAll() {
        requestContextController.deactivate();
        requestContextController.activate();
        UUID uuid = conversationScopedBean.getUuid();
        requestContextController.deactivate();
        requestContextController.activate();
        UUID uuid2 = conversationScopedBean.getUuid();
        assertEquals(uuid, uuid2);

        runTestStopAll(conversationScopedBean);
    }

    @Test
    void restartApplicationStopAll() {
        TestInfo testInfo = contextControl.stop();
        contextControl.start(testInfo);
        UUID uuid = applicationScopedBean.getUuid();
        contextControl.stop();
        contextControl.start(testInfo);
        UUID uuid2 = applicationScopedBean.getUuid();
        assertEquals(uuid, uuid2);
    }

    private void runTestStopAll(ScopedBean scopedBean) {
        TestInfo testInfo = contextControl.stop();
        contextControl.start(testInfo);
        UUID uuid = scopedBean.getUuid();
        contextControl.stop();
        contextControl.start(testInfo);
        UUID uuid2 = scopedBean.getUuid();
        assertNotEquals(uuid, uuid2);
    }
}

package com.github.jonasrutishauser.cdi.test.core.context;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.core.context.beans.ApplicationScopedBean;
import com.github.jonasrutishauser.cdi.test.core.context.beans.ApplicationToTestScopedBean;
import com.github.jonasrutishauser.cdi.test.core.context.beans.ConversationScopedBean;
import com.github.jonasrutishauser.cdi.test.core.context.beans.DependentScopedBean;
import com.github.jonasrutishauser.cdi.test.core.context.beans.RequestScopedBean;
import com.github.jonasrutishauser.cdi.test.core.context.beans.ScopedBean;
import com.github.jonasrutishauser.cdi.test.core.context.beans.SessionScopedBean;
import com.github.jonasrutishauser.cdi.test.core.context.beans.TestScopedBean;
import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

@ExtendWith(CdiTestJunitExtension.class)
class ScopeContextTest {

    private static Map<Type, UUID> uuidMap;

    @Any
    @Inject
    private Instance<Object> instance;

    @BeforeAll
    static void reset() {
        uuidMap = new HashMap<>();
    }

    @Test
    void resolveInvocationTargetManagerA() {
        assertAllTypes();
    }

    @Test
    void resolveInvocationTargetManagerB() {
        assertAllTypes();
    }

    @Test
    void testScopedInitialization() {
        Object initializationObject = instance.select(ApplicationToTestScopedBean.class).get()
                .getInitializationObject();
        assertInstanceOf(TestInfo.class, initializationObject);
    }

    void assertAllTypes() {
        resolveOrAssert(ApplicationScopedBean.class, Assertions::assertEquals);
        resolveOrAssert(ApplicationToTestScopedBean.class, Assertions::assertNotEquals);
        resolveOrAssert(TestScopedBean.class, Assertions::assertNotEquals);
        resolveOrAssert(RequestScopedBean.class, Assertions::assertNotEquals);
        resolveOrAssert(ConversationScopedBean.class, Assertions::assertNotEquals);
        resolveOrAssert(SessionScopedBean.class, Assertions::assertNotEquals);
        resolveOrAssert(DependentScopedBean.class, Assertions::assertNotEquals);
    }

    private <T extends ScopedBean> void resolveOrAssert(Class<T> type, BiConsumer<UUID, UUID> assertion) {
        final T resolvedObject = instance.select(type).get();
        if (!uuidMap.containsKey(type)) {
            uuidMap.put(type, resolvedObject.getUuid());
        } else {
            assertion.accept(uuidMap.get(type), resolvedObject.getUuid());
        }
    }

}

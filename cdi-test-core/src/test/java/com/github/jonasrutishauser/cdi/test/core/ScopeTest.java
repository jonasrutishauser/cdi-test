package com.github.jonasrutishauser.cdi.test.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.beans.Request;
import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

import jakarta.inject.Inject;

@ExtendWith(CdiTestJunitExtension.class)
class ScopeTest {

    @Inject
    private Request request;
    @Inject
    private ScopeTest testBean;

    private UUID value;

    private static UUID lastIdentifier;

    @Test
    void injection() {
        assertNotNull(request);
    }

    @Test
    void sameValueTwice() {
        assertEquals(request.getIdentifier(), request.getIdentifier());
    }

    @Test
    void testInstanceInjection() {
        value = UUID.randomUUID();
        assertEquals(value, testBean.getValue());
    }

    @RepeatedTest(10)
    void notAlwaysTheSame() {
        assertNotEqual();
    }

    public UUID getValue() {
        return value;
    }

    private void assertNotEqual() {
        if (lastIdentifier == null) {
            lastIdentifier = request.getIdentifier();
        } else {
            assertNotEquals(lastIdentifier, request.getIdentifier());
        }
    }
}

package com.github.jonasrutishauser.cdi.test.ejb;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;

@ExtendWith(CdiTestJunitExtension.class)
class EjbTest {

    @Inject
    private SingletonBean singleton;

    @EJB
    private StatelessBean stateless;

    @Test
    void testInject() {
        assertNotNull(singleton);
        assertNotNull(stateless);
    }

    @Test
    void testStartup() {
        assertTrue(singleton.isStarted());
        assertTrue(stateless.isStarted());
    }

}

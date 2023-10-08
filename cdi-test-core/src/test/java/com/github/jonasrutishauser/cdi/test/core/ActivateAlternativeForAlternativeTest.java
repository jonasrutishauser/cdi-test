package com.github.jonasrutishauser.cdi.test.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.jonasrutishauser.cdi.test.core.beans.Person;
import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;
import com.github.jonasrutishauser.cdi.test.core.service.BackendService;
import com.github.jonasrutishauser.cdi.test.core.service.OverriddenService;
import com.github.jonasrutishauser.cdi.test.core.service.TestActivatedOverridenService;

/**
 * Demo and test test activation for alternatives.
 */
@Tag("mockito")
@ExtendWith(CdiTestJunitExtension.class)
@ExtendWith(MockitoExtension.class)
class ActivateAlternativeForAlternativeTest {
    @Inject
    private TestActivatedOverridenService testOverride;
    @Inject
    private OverriddenService overriddenService;
    @Inject
    private BackendService backendService;

    @Test
    void callTestActivatedService() {
        backendService.storePerson(new Person());
        backendService.storePerson(new Person());
        assertEquals(2, testOverride.getInvocationCounter());
    }

    @Test
    void callTestActivatedServiceIndependently() {
        backendService.storePerson(new Person());
        backendService.storePerson(new Person());
        assertEquals(2, testOverride.getInvocationCounter());
    }

    @Test
    void callOverridenServiceDirectly() {
        overriddenService.serviceMethod();
        overriddenService.serviceMethod();
        overriddenService.serviceMethod();
        assertEquals(3, testOverride.getInvocationCounter());
    }

    @Test
    void callOverridenServiceMixed() {
        overriddenService.serviceMethod();
        overriddenService.serviceMethod();
        overriddenService.serviceMethod();
        backendService.storePerson(new Person());
        assertEquals(4, testOverride.getInvocationCounter());
    }
}

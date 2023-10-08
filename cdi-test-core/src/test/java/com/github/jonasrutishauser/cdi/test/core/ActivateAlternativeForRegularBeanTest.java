package com.github.jonasrutishauser.cdi.test.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.beans.Person;
import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;
import com.github.jonasrutishauser.cdi.test.core.service.BackendServiceException;
import com.github.jonasrutishauser.cdi.test.core.service.BackendServiceTestImplementation;
import com.github.jonasrutishauser.cdi.test.core.service.SampleService;

/**
 * Demo and test auto-wiring of {@link Inject}ed test implementations.
 */
@ExtendWith(CdiTestJunitExtension.class)
class ActivateAlternativeForRegularBeanTest {
    @Inject
    private SampleService sampleService;
    @Inject
    private BackendServiceTestImplementation testBackendService;

    @Test
    void callTestActivatedService() {
        sampleService.storePerson(new Person());
        assertEquals(1, testBackendService.getInvocations());
    }

    @Test
    @BackendServiceException(RuntimeException.class)
    void callTestActivatedServiceWithBackendException() {
        Person person = new Person();
        Assertions.assertThrows(RuntimeException.class, () -> sampleService.storePerson(person));
    }

}

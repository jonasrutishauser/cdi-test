package com.github.jonasrutishauser.cdi.test.core;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import javax.inject.Inject;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.jonasrutishauser.cdi.test.core.beans.Person;
import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;
import com.github.jonasrutishauser.cdi.test.core.service.BackendService;
import com.github.jonasrutishauser.cdi.test.core.service.SampleService;

/**
 * Deep bean replacement with mocks.
 */
@Tag("mockito")
@ExtendWith(CdiTestJunitExtension.class)
@ExtendWith(MockitoExtension.class)
class MockProxyPartialDisabledTest {

    @Mock
    private BackendService backendService;

    @Inject
    private SampleService sampleService;

    @Test
    void createPerson() {
        Person person = new Person();
        sampleService.storePerson(person);
        verify(backendService).storePerson(person);
    }

    @Test
    void doNothing() {
        verifyNoInteractions(backendService);
    }

}

package com.github.jonasrutishauser.cdi.test.core;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.jonasrutishauser.cdi.test.core.beans.Person;
import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;
import com.github.jonasrutishauser.cdi.test.core.service.BackendService;
import com.github.jonasrutishauser.cdi.test.core.service.SampleService;

import jakarta.inject.Inject;

/**
 * Demo and test {@link de.hilling.junit.cdi.scope.TestScoped} and separation of test cases.
 */
@Tag("mockito")
@ExtendWith(CdiTestJunitExtension.class)
@ExtendWith(MockitoExtension.class)
class MultipleInvocationsTest {

    @Inject
    private SampleService sampleService;

    @Mock
    private BackendService backendService;

    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person();
    }

    @Test
    void createPersonWithMockBackendA() {
        sampleService.storePerson(person);
        verify(backendService).storePerson(person);
    }

    @Test
    void createPersonWithMockBackendB() {
        sampleService.storePerson(person);
        verify(backendService).storePerson(person);
    }

}
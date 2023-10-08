package com.github.jonasrutishauser.cdi.test.core;

import java.util.UUID;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.jonasrutishauser.cdi.test.core.beans.Person;
import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;
import com.github.jonasrutishauser.cdi.test.core.service.BackendServiceTestImplementation;
import com.github.jonasrutishauser.cdi.test.core.service.SampleService;

/**
 * Demo for running lots of (parameterized) junit 5 tests _fast_.
 */
@ExtendWith(CdiTestJunitExtension.class)
class ParameterizedTestLifecycleTest {

    @Inject
    private SampleService service;

    @Inject
    private BackendServiceTestImplementation backendService;

    static Stream<String> createPersonNames() {
        return Stream.
        generate(UUID::randomUUID).
        map(UUID::toString).
        limit(5);
    }

    @ParameterizedTest
    @MethodSource("createPersonNames")
    void storePerson(String name) {
        Person testPerson = new Person(name);
        service.storePerson(testPerson);
        Assertions.assertEquals(1, backendService.getInvocations());
    }
}
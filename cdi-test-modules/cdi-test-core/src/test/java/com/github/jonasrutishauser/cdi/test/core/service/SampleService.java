package com.github.jonasrutishauser.cdi.test.core.service;

import com.github.jonasrutishauser.cdi.test.core.beans.Person;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class SampleService {

    @Inject
    private BackendService backendService;

    public void storePerson(Person person) {
        backendService.storePerson(person);
    }
}

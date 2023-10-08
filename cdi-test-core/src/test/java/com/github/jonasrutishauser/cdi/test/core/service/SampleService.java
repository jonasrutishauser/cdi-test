package com.github.jonasrutishauser.cdi.test.core.service;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.jonasrutishauser.cdi.test.core.beans.Person;

@Dependent
public class SampleService {

    @Inject
    private BackendService backendService;

    public void storePerson(Person person) {
        backendService.storePerson(person);
    }
}

package com.github.jonasrutishauser.cdi.test.core.service;

import com.github.jonasrutishauser.cdi.test.core.beans.Person;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class BackendService implements Service {

    @Inject
    private OverriddenService sampleService;

    @TestQualifier
    @Inject
    private OverriddenService qualifiedSampleService;

    @Override
    public String storePerson(Person person) {
        return sampleService.serviceMethod();
    }

    public String storePersonQualified() {
        return qualifiedSampleService.serviceMethod();
    }

}

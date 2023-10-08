package com.github.jonasrutishauser.cdi.test.core.service;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.jonasrutishauser.cdi.test.core.beans.Person;

@Dependent
public class BackendService {

    @Inject
    private OverriddenService sampleService;

    @TestQualifier
    @Inject
    private OverriddenService qualifiedSampleService;

    public String storePerson(Person person) {
        return sampleService.serviceMethod();
    }

    public String storePersonQualified(Person person) {
        return qualifiedSampleService.serviceMethod();
    }

}

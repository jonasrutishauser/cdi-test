package com.github.jonasrutishauser.cdi.test.core.beans;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class ConstructorInjected {

    private final Person person;
    private final Request request;

    @Inject
    public ConstructorInjected(Person person, Request request) {
        this.person = person;
        this.request = request;
    }

    public Person getPerson() {
        return person;
    }

    public Request getRequest() {
        return request;
    }

}

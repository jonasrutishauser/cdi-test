package com.github.jonasrutishauser.cdi.test.core.beans;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@Dependent
public class CustomPersonProducer {
    @Produces
    @CustomPerson
    public Person createCustomPerson(InjectionPoint injectionPoint) {
        Person person = new Person();
        String beanName = injectionPoint.getBean().getName();
        person.setName(beanName);
        return person;
    }
}

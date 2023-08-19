package com.github.jonasrutishauser.cdi.test.core.beans;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

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

package com.github.jonasrutishauser.cdi.testing;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

@RequestScoped
public class ProducedBeanProducer {

    @Produces
    public ProducedBean createProducedBean() {
        return new ProducedBean("hello");
    }
}

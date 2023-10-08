package com.github.jonasrutishauser.cdi.jpa;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

@Dependent
public class CounterEntityListener {

    @Inject
    private InvocationCounter invocationCounter;

    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeAnyUpdate(UserEntity user) {
        invocationCounter.inc();
    }
}

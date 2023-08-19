package com.github.jonasrutishauser.cdi.jpa;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

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

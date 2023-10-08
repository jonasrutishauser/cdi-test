package com.github.jonasrutishauser.cdi.test.jpa;

import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class TestEntityListener {
    @Inject
    private UpdateCounter updateCounter;

    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeAnyUpdate(Object o) {
        updateCounter.inc();
    }
}

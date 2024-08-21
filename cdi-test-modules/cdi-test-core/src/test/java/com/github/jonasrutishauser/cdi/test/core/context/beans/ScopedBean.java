package com.github.jonasrutishauser.cdi.test.core.context.beans;

import java.util.UUID;

public class ScopedBean {

    protected UUID uuid;

    public ScopedBean() {
        uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }

}

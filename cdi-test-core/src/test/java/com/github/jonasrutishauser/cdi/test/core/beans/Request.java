package com.github.jonasrutishauser.cdi.test.core.beans;

import java.util.UUID;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class Request {

    private UUID identifier = UUID.randomUUID();

    public UUID getIdentifier() {
        return identifier;
    }

}

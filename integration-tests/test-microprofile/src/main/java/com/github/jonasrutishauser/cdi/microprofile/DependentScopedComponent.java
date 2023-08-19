package com.github.jonasrutishauser.cdi.microprofile;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class DependentScopedComponent {

    @Inject
    @ConfigProperty(name = "some.string.property")
    private String stringProperty;

    public String getStringProperty() {
        return stringProperty;
    }

}

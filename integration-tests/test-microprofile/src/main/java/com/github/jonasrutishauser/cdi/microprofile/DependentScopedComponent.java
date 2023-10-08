package com.github.jonasrutishauser.cdi.microprofile;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Dependent
public class DependentScopedComponent {

    @Inject
    @ConfigProperty(name = "some.string.property")
    private String stringProperty;

    public String getStringProperty() {
        return stringProperty;
    }

}

package com.github.jonasrutishauser.cdi.microprofile;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Singleton
public class EjbController {

    @Inject
    @ConfigProperty(name = "some.string.property")
    private String stringProperty;

    @Inject
    @ConfigProperty(name = "some.integer.property")
    private Integer intProperty;

    @Inject
    @ConfigProperty(name = "some.boolean.property")
    private Boolean boolProperty;

    @Inject
    @ConfigProperty(name = "some.long.property")
    private Long longProperty;

    @Inject
    @ConfigProperty(name = "some.horse.property")
    private Horse horseProperty;

    public String getStringProperty() {
        return stringProperty;
    }

    public int getIntegerProperty() {
        return intProperty;
    }

    public boolean getBoolProperty() {
        return boolProperty;
    }

    public long getLongProperty() {
        return longProperty;
    }

    public String getHorseProperty() {
        return horseProperty.getName();
    }

}

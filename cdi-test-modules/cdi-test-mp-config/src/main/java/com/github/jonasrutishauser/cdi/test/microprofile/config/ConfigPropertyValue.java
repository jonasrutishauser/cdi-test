package com.github.jonasrutishauser.cdi.test.microprofile.config;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines an overriding property value used in cdi unit tests.
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE})
@Repeatable(ConfigPropertyValues.class)
public @interface ConfigPropertyValue {

    String name();

    String value();

}

package com.github.jonasrutishauser.cdi.test.microprofile.config;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface ConfigPropertyValues {
    ConfigPropertyValue[] value();
}

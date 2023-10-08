package com.github.jonasrutishauser.cdi.test.jndi;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.annotation.Resource;

/**
 * Defines an environment entry (can be injected with
 * <code>@{@link Resource}</code>).
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Repeatable(EnvEntries.class)
public @interface EnvEntry {

    static String UNSPECIFIED = "#unspecified";

    boolean compEnv() default false;

    String name();

    Class<?> type() default String.class;

    String typeAsString() default UNSPECIFIED;

    String value();

}

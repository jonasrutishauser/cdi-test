package com.github.jonasrutishauser.cdi.test.jndi;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.sql.Driver;

@Retention(RUNTIME)
@Target({TYPE})
@Repeatable(DataSourceEntries.class)
public @interface DataSourceEntry {

    static String UNSPECIFIED = "#unspecified";

    boolean compEnv() default false;

    String name();

    String url();

    Class<? extends Driver> driver() default Driver.class;

    String driverAsString() default UNSPECIFIED;

    String user();

    String password();

}

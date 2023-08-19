package com.github.jonasrutishauser.cdi.test.core.interceptor;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.inject.Stereotype;

@Stereotype
@Replaceable
@Retention(RUNTIME)
@Target({TYPE, FIELD, METHOD})
public @interface ReplaceableStereotype {}

package com.github.jonasrutishauser.cdi.test.api.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import jakarta.enterprise.inject.Stereotype;

/**
 * Use this annotation to mark Alternatives that can be enabled per test class.
 */
@TestScoped
@Stereotype
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface ActivatableTestImplementation {
    /**
     * Enumerates the classes and/or interfaces that should be replaced by the
     * injected bean.
     * 
     * @return activatable beans.
     */
    Class<?>[] value() default {};
}

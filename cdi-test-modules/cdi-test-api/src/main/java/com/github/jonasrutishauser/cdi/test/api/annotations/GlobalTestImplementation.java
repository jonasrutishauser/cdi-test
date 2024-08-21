package com.github.jonasrutishauser.cdi.test.api.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Stereotype;

/**
 * Use this annotation to mark Alternatives that should globally replace
 * production implementations.
 */
@Alternative
@TestScoped
@Stereotype
@Documented
@Retention(RUNTIME)
@Target({TYPE, FIELD, METHOD})
public @interface GlobalTestImplementation {

}

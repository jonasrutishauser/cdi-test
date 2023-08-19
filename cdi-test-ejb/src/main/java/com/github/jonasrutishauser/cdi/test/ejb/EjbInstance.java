package com.github.jonasrutishauser.cdi.test.ejb;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

@Qualifier
@Retention(RUNTIME)
@Target({TYPE})
@interface EjbInstance {
    static class Literal extends AnnotationLiteral<EjbInstance> implements EjbInstance {
        public static EjbInstance INSTANCE = new Literal();

        private Literal() {
        }
    }
}

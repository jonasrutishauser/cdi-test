package com.github.jonasrutishauser.cdi.test.api.context;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.context.NormalScope;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Scope;

@Scope
@NormalScope
@Inherited
@Documented
@Retention(RUNTIME)
@Target({TYPE, FIELD, METHOD})
public @interface TestScoped {
    public static class Literal extends AnnotationLiteral<TestScoped> implements TestScoped {
        public static final TestScoped INSTANCE = new Literal();

        private Literal() {
        }
    }
}

package com.github.jonasrutishauser.cdi.test.core.beans;

import jakarta.enterprise.context.Dependent;

@Dependent
public class Person {
    private String name;

    public Person() {
        this(null);
    }

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

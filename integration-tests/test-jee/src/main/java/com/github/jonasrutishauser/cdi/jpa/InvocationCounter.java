package com.github.jonasrutishauser.cdi.jpa;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InvocationCounter {

    private AtomicInteger counter = new AtomicInteger();

    public void inc() {
        counter.incrementAndGet();
    }

    public int get() {
        return counter.get();
    }

}

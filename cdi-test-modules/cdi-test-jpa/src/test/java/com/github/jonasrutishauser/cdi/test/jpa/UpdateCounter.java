package com.github.jonasrutishauser.cdi.test.jpa;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

@TestScoped
public class UpdateCounter {

    private final AtomicInteger counter = new AtomicInteger();

    public void inc() {
        counter.incrementAndGet();
    }

    public int get() {
        return counter.get();
    }
}

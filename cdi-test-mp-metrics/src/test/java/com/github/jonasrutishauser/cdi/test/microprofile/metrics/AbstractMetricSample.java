package com.github.jonasrutishauser.cdi.test.microprofile.metrics;

import org.eclipse.microprofile.metrics.Counter;

abstract class AbstractMetricSample {

    protected final Counter counter;

    protected AbstractMetricSample(Counter counter) {
        this.counter = counter;
    }

    public void increase() {
        counter.inc();
    }
    
    abstract long count();

}

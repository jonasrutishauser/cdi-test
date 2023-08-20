package com.github.jonasrutishauser.cdi.test.microprofile.metrics;

import static org.eclipse.microprofile.metrics.MetricUnits.NONE;

import org.eclipse.microprofile.metrics.annotation.Gauge;

import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

@TestScoped
public class TestGauge {

    long value = 0;

    @Gauge(name="testGauge", unit = NONE, absolute = true)
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

}

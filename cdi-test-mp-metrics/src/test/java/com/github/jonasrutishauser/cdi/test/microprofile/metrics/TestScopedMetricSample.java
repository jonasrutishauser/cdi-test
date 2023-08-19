package com.github.jonasrutishauser.cdi.test.microprofile.metrics;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;

import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import jakarta.enterprise.inject.TransientReference;
import jakarta.inject.Inject;

@TestScoped
public class TestScopedMetricSample extends AbstractMetricSample {

    @Inject
    public TestScopedMetricSample(@Metric(name = "counter") Counter counter, @TransientReference MetricRegistry registry) {
        super(counter);
        registry.gauge("other.gauge", counter::getCount);
    }
    
    @Timed
    @Override
    public void increase() {
        super.increase();
    }

    @Gauge(unit = MetricUnits.NONE)
    long count() {
        return counter.getCount();
    }

}

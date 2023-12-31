package com.github.jonasrutishauser.cdi.test.microprofile.metrics;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.TransientReference;
import jakarta.inject.Inject;

@ApplicationScoped
public class ApplicationScopedMetricSample extends AbstractMetricSample {

    @Inject
    public ApplicationScopedMetricSample(@Metric(name = "counter") Counter counter, @TransientReference MetricRegistry registry) {
        super(counter);
        registry.gauge("some.gauge", counter::getCount);
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

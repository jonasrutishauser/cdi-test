package com.github.jonasrutishauser.cdi.test.microprofile.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

import jakarta.inject.Inject;

@ExtendWith(CdiTestJunitExtension.class)
class MetricTest {

    @Inject
    private ApplicationScopedMetricSample applicationScopedSample;

    @Inject
    private TestScopedMetricSample testScopedSample;

    @Inject
    private ApplicationToTestScopedMetricSample applicationToTestScopedSample;

    @Inject
    private MetricRegistry registry;

    @Test
    void testInjection() {
        assertNotNull(applicationScopedSample);
        assertNotNull(testScopedSample);
        assertNotNull(registry);
    }

    private ApplicationScopedMetricSample getApplicationScopedSample() {
        return applicationScopedSample;
    }

    private ApplicationToTestScopedMetricSample getApplicationToTestScopedSample() {
        return applicationToTestScopedSample;
    }

    private TestScopedMetricSample getTestScopedSample() {
        return testScopedSample;
    }

    static Stream<Arguments> samples() {
        return Stream.of(
                Arguments.of((Function<MetricTest, AbstractMetricSample>) MetricTest::getApplicationScopedSample,
                        ApplicationScopedMetricSample.class, "applicationScopedSample"),
                Arguments.of((Function<MetricTest, AbstractMetricSample>) MetricTest::getApplicationToTestScopedSample,
                        ApplicationToTestScopedMetricSample.class, "applicationToTestScopedSample"),
                Arguments.of((Function<MetricTest, AbstractMetricSample>) MetricTest::getTestScopedSample,
                        TestScopedMetricSample.class, "testScopedSample")) //
                .flatMap(args -> IntStream.range(1, 3)
                        .mapToObj(i -> Arguments.of(args.get()[0], args.get()[1], args.get()[2] + " iteration " + i)));
    }

    @MethodSource("samples")
    @ParameterizedTest(name = "{2}")
    void testCounter(Function<MetricTest, AbstractMetricSample> sampleProvider,
            Class<? extends AbstractMetricSample> type, String $) {
        AbstractMetricSample sample = sampleProvider.apply(this);
        assertEquals(0, sample.count());

        sample.increase();

        assertEquals(1, sample.count());
        assertEquals(1, registry.counter(type.getName() + ".counter").getCount());
    }

    @MethodSource("samples")
    @ParameterizedTest(name = "{2}")
    void testGauge(Function<MetricTest, AbstractMetricSample> sampleProvider,
            Class<? extends AbstractMetricSample> type, String $) {
        AbstractMetricSample sample = sampleProvider.apply(this);
        sample.count();

        @SuppressWarnings("unchecked")
        Gauge<Long> gauge = (Gauge<Long>) registry.getGauge(new MetricID(type.getName() + ".count"));
        assertEquals(0, gauge.getValue());

        sample.increase();

        assertEquals(1, gauge.getValue());
    }

}

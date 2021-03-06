package org.basaki.metrics.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.basaki.metrics.filter.CustomMetricFilter;
import org.basaki.metrics.filter.MetricFilterProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@code JmxMetricsConfiguration} is the configuration class for registering
 * Jmx reporter.
 * <p>
 *
 * @author Indra Basak
 * @since 04/28/17
 */
@Configuration
@ConditionalOnProperty(name = {"metrics.report.jmx"}, matchIfMissing = true)
@AutoConfigureAfter({CoreMetricsConfiguration.class})
@Slf4j
public class JmxMetricsConfiguration {

    /**
     * Registers a JMX reporter which listens for new metrics and exposes them
     * as namespaced MBeans.
     *
     * @param registry previously initialized metric registry
     * @return a JMX reporter
     */
    @Bean
    public JmxReporter registerJmxReporter(
            @Qualifier("registry") MetricRegistry registry,
            MetricFilterProperties properties) {
        CustomMetricFilter filter = new CustomMetricFilter(properties);

        final JmxReporter reporter =
                JmxReporter.forRegistry(registry).convertRatesTo(
                        TimeUnit.SECONDS).convertDurationsTo(
                        TimeUnit.MILLISECONDS)
                        .filter(filter)
                        .build();
        reporter.start();
        log.debug("Registered JMX metrics reporter.");

        return reporter;
    }
}

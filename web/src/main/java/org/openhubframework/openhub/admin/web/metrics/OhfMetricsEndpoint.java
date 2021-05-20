/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.admin.web.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *  A custom {@link WebEndpoint} for exposing the metrics held by a MeterRegistry.
 *
 * @author Jiri Hankovec
 * @since 2.3
 */
@WebEndpoint(id = "ohfmetrics")
public class OhfMetricsEndpoint {

    private final MeterRegistry meterRegistry;

    public OhfMetricsEndpoint(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * GET /mgmt/ohfmetrics
     * <p>
     * Give metrics displayed on Dashboard
     *
     * @return a Map with a String defining a category of metrics as Key and
     * Number as Value
     */
    @ReadOperation
    public Map<String, Number> ohfCustomMetrics() {

        Map<String, Number> results = new HashMap<>();

        double memoryMax = this.getMetricWithTagValue("jvm.memory.max", "area", "nonheap");
        double memoryFree = memoryMax - this.getMetricWithTagValue("jvm.memory.used", "area", "nonheap");
        results.put("mem", memoryMax / 1024); // Divide by 1024 - return in kB
        results.put("mem.free", memoryFree / 1024); // Divide by 1024 - return in kB

        double heapMax = this.getMetricWithTagValue("jvm.memory.max", "area", "heap");
        double heapFree = heapMax - this.getMetricWithTagValue("jvm.memory.used", "area", "heap");
        results.put("heap", heapMax / 1024); // Divide by 1024 - return in kB
        results.put("heap.used", heapFree / 1024); // Divide by 1024 - return in kB

        results.put("processors", this.getMetricValue("system.cpu.count", MetricType.GAUGE));
        results.put("uptime", this.getMetricValue("process.uptime", MetricType.TIME_GAUGE));

        double classesLoaded = this.getMetricValue("jvm.classes.loaded", MetricType.GAUGE);
        double classesUnloaded = this.getMetricValue("jvm.classes.unloaded", MetricType.COUNTER);
        results.put("classes.loaded", classesLoaded);
        results.put("classes.unloaded", classesUnloaded);
        results.put("classes", classesLoaded - classesUnloaded);

        return results;
    }

    private Double getMetricValue(String metricName, MetricType type) {
        double measuredValue = 0;
        switch (type) {
            case GAUGE:
                Collection<Gauge> gauges = this.meterRegistry.find(metricName).gauges();
                measuredValue = gauges.stream().map(Gauge::value).reduce(Double::sum).orElse(0D);
                break;
            case TIME_GAUGE:
                Collection<TimeGauge> timeGauges = this.meterRegistry.find(metricName).timeGauges();
                measuredValue =
                        timeGauges.stream().map(timeGauge -> timeGauge.value(TimeUnit.MILLISECONDS)).reduce(Double::sum).orElse(0D);
                break;
            case COUNTER:
                Collection<FunctionCounter> counters = this.meterRegistry.find(metricName).functionCounters();
                measuredValue = counters.stream().map(FunctionCounter::count).reduce(Double::sum).orElse(0D);
                break;
        }
        return measuredValue;
    }

    private Double getMetricWithTagValue(String metricName, String tagKey, String tagValue) {
        Collection<Gauge> gauges = this.meterRegistry.find(metricName).tag(tagKey, tagValue).gauges();
        return gauges.stream().map(Gauge::value).reduce(Double::sum).orElse(0D);
    }

    private enum MetricType {
        GAUGE,
        TIME_GAUGE,
        COUNTER
    }
}

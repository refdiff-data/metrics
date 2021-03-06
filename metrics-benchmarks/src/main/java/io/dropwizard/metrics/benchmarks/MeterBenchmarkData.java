package io.dropwizard.metrics.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import io.dropwizard.metrics.Meter;

@State(Scope.Benchmark)
public class MeterBenchmarkData {

    private final Meter meter = new Meter();

    // It's intentionally not declared as final to avoid constant folding
    private long nextValue = 0xFBFBABBA;

    @Benchmark
    public Object perfMark() {
        meter.mark(nextValue);
        return meter;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + MeterBenchmarkData.class.getSimpleName() + ".*")
                .warmupIterations(3)
                .measurementIterations(5)
                .threads(4)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}

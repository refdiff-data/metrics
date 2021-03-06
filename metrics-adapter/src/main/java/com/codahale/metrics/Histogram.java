package com.codahale.metrics;

import com.codahale.metrics.health.Snapshot;

import io.dropwizard.metrics.Counting;
import io.dropwizard.metrics.Reservoir;
import io.dropwizard.metrics.Sampling;

@Deprecated
public class Histogram implements Metric, Sampling, Counting {
	final io.dropwizard.metrics.Histogram hist;

	public Histogram(io.dropwizard.metrics.Histogram hist) {
		this.hist = hist;
	}
	public Histogram(Reservoir reservoir) {
		this.hist = new io.dropwizard.metrics.Histogram(reservoir);
    }

	public void update(int value) {
		hist.update(value);
	}

	public void update(long value) {
		hist.update(value);
	}

	@Override
	public long getCount() {
		return hist.getCount();
	}

	@Override
	public Snapshot getSnapshot() {
		return new Snapshot(hist.getSnapshot());
	}
}

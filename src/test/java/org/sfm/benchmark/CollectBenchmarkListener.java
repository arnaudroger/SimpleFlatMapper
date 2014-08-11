package org.sfm.benchmark;

import org.HdrHistogram.Histogram;

public class CollectBenchmarkListener implements BenchmarkListener {
	
	Histogram histogram = new Histogram(1000000000, 3);
	@Override
	public void results(long nb, long elapsed) {
		histogram.recordValue(elapsed);
	}
	
	public Histogram getHistogram() {
		return histogram;
	}
	
}

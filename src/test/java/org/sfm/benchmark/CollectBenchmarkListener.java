package org.sfm.benchmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CollectBenchmarkListener implements BenchmarkListener {
	public static class Result {
		final long nb;
		final long elapsed;
		final double timePerObject;
		final double tps;
		public Result(long nb, long elapsed) {
			this.nb = nb;
			this.elapsed = elapsed;
			this.timePerObject = elapsed/(double)nb;
			this.tps =  (nb * 1000000000.0) / elapsed ;
		}
		
	}
	
	List<Result> results = new ArrayList<>();
	
	@Override
	public void results(long nb, long elapsed) {
		results.add(new Result(nb, elapsed));
	}
	
	public Result min() {
		Result min  = null;
		for(Result r : results) {
			if (min == null || r.timePerObject < min.timePerObject) {
				min = r;
			}
		}
		return min;
	}
	public Result max() {
		Result max  = null;
		for(Result r : results) {
			if (max == null || r.timePerObject > max.timePerObject) {
				max = r;
			}
		}
		return max;
	}
	public Result median() {
		List<Result> sorted = new ArrayList<>(results);
		Collections.sort(sorted, new Comparator<Result>() {
			@Override
			public int compare(Result o1, Result o2) {
				return (int) (o1.timePerObject - o2.timePerObject);
			}
		});
		if (sorted.isEmpty()) {
			return null;
		} else {
			return sorted.get(sorted.size()/2);
		}
	}
	
	public Result avg() {
		long nb = 0, elapsed = 0;
		
		for(Result r : results) {
			nb += r.nb;
			elapsed += r.elapsed;
		}
		
		return new Result(nb, elapsed);
	}
}

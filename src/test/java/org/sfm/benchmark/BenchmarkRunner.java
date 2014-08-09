package org.sfm.benchmark;

import org.sfm.beans.DbObject;

public class BenchmarkRunner {
	private static final class ForEachListenerImpl implements ForEachListener {
		
		private BenchmarkListener bl;
		private long counter;
		private long startTime;
		
		public ForEachListenerImpl(BenchmarkListener bl) {
			this.bl = bl;
		}

		@Override
		public void start() {
			startTime = System.nanoTime();
			counter = 0;
		}

		@Override
		public void object(DbObject o) {
			counter++;
		}

		@Override
		public void end() {
			if (bl != null) {
				long elapsed = System.nanoTime() - startTime;
				bl.results(counter, elapsed);
			}
		}
	}

	private final int limit;
	private final int iteration = 20;
	private final int innerIteration;
	private final QueryExecutor exec;
	private int warmup;
	
	public BenchmarkRunner(int limit, QueryExecutor exec) {
		this.limit = limit;
		if (limit > 0) {
			this.innerIteration = 20000000/(limit * iteration);
			this.warmup = 1000000/limit;
		} else {
			this.innerIteration = 1;
			this.warmup = 1;
		}
		this.exec = exec;
	}

	public void run(BenchmarkListener bl) throws Exception {
		// warm up
		exec.prepareQuery(limit);
		ForEachListener listener = new ForEachListenerImpl(null);
		for(int i = 0; i < warmup; i++) {
			exec(listener);
		}
		
		listener = new ForEachListenerImpl(bl);
		for(int i = 0; i < iteration; i++) {
			listener.start();
			for(int j = 0; j < innerIteration; j++) {
				exec(listener);
			}
			listener.end();
		}
	}

	private void exec(ForEachListener listener) throws Exception {
		exec.executeQuery();
		exec.forEach(listener);
	}
}

package org.sfm.benchmark;


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
		public void object(Object o) {
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
	private final int iteration;
	private final int innerIteration;
	private final QueryExecutor exec;
	private final int warmup;
	public BenchmarkRunner(int limit, QueryExecutor exec) {
		this(limit, exec, true);
	}
	public BenchmarkRunner(int limit, QueryExecutor exec, boolean innerIteration) {
		this(limit, 20, 1000000, exec, innerIteration);
	}
	public BenchmarkRunner(int limit, int iteration, int nbobjects, QueryExecutor exec, boolean innerIteration) {
		this.limit = limit;
		if (innerIteration) {
			this.iteration = iteration;
			if (limit > 0) {
				this.innerIteration = nbobjects/(limit);
				this.warmup = nbobjects/limit;
			} else {
				this.innerIteration = 1;
				this.warmup = 1;
			}
		} else {
			if (limit > 0) {
				this.iteration = iteration *  nbobjects/(limit);
				this.innerIteration = 1;
				this.warmup = nbobjects/limit;
			} else {
				this.iteration = iteration;
				this.innerIteration = 1;
				this.warmup = 1;
			}
		}
		this.exec = exec;
	}

	public void run(BenchmarkListener bl) throws Exception {
		warmup();
		benchmark(bl);
	}

	private void benchmark(BenchmarkListener bl) throws Exception {
		ForEachListener listener = new ForEachListenerImpl(bl);
		for(int i = 0; i < iteration; i++) {
			if (innerIteration == 1) {
				listener.start();
				exec.forEach(listener, limit);
				listener.end();
			} else {
				iteration(listener);
			}
		}
	}

	private void iteration(ForEachListener listener) throws Exception {
		listener.start();
		for(int j = 0; j < innerIteration; j++) {
			exec.forEach(listener, limit);
		}
		listener.end();
	}

	private void warmup() throws Exception {
		ForEachListener listener = new ForEachListenerImpl(null);
		for(int i = 0; i < warmup; i++) {
			exec.forEach(listener, limit);
		}
	}

}

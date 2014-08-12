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
	private final QueryExecutor exec;
	private final int warmup;
	public BenchmarkRunner(int iteration, int limit, QueryExecutor exec) {
		this.limit = limit;
		this.iteration = iteration;
		this.warmup = iteration;
		this.exec = exec;
	}

	public void run(BenchmarkListener bl) throws Exception {
		warmup();
		benchmark(bl);
	}

	private void benchmark(BenchmarkListener bl) throws Exception {
		ForEachListener listener = new ForEachListenerImpl(bl);
		for(int i = 0; i < iteration; i++) {
			listener.start();
			exec.forEach(listener, limit);
			listener.end();
		}
	}

	private void warmup() throws Exception {
		ForEachListener listener = new ForEachListenerImpl(null);
		for(int i = 0; i < warmup; i++) {
			exec.forEach(listener, limit);
		}
	}
}

package org.sfm.benchmark;


public final class SysOutBenchmarkListener implements
		BenchmarkListener {
	private String name;

	public SysOutBenchmarkListener(Class<?> clazz, String name) {
		this.name = clazz.getSimpleName() + "," + name;
	}

	@Override
	public void results(long nb, long elapsed) {
		System.out.print(name);
		System.out.print(",");
		System.out.print(Long.toString(nb));
		System.out.print(",");
		System.out.print(Long.toString(elapsed));
		System.out.print(",");
		System.out.print(Long.toString(elapsed/nb));
		System.out.println();
	}
}
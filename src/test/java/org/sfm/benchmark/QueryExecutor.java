package org.sfm.benchmark;



public interface QueryExecutor {
	public void forEach(ForEachListener ql, int limit)  throws Exception;
}

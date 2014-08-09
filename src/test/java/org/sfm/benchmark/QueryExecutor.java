package org.sfm.benchmark;



public interface QueryExecutor {
	public void executeQuery() throws Exception;
	public void prepareQuery(int limit) throws Exception;
	public void forEach(ForEachListener ql)  throws Exception;
	public void close() throws Exception;
}

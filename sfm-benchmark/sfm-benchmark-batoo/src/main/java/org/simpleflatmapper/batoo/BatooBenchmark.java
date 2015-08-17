package org.simpleflatmapper.batoo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.simpleflatmapper.db.ConnectionParam;
import org.simpleflatmapper.db.DbTarget;
import org.simpleflatmapper.param.LimitParam;

@State(Scope.Benchmark)
/**
 * batoo has a dependency on a old library of asm.
 * batoo benchmark cannot run at the sane time as sfm asm.
 * needs to exclude asm from the pom to run batoo.
 * 
 *
 */

/*
Benchmark                 (db)  (limit)   Mode  Cnt       Score      Error  Units
BatooBenchmark._04Fields    H2        1  thrpt   20  424105.769 ± 7998.794  ops/s
BatooBenchmark._04Fields    H2       10  thrpt   20  184095.607 ± 2349.584  ops/s
BatooBenchmark._04Fields    H2      100  thrpt   20   26205.799 ±  371.146  ops/s
BatooBenchmark._04Fields    H2     1000  thrpt   20    2667.718 ±   56.348  ops/s
BatooBenchmark._16Fields    H2        1  thrpt   20  172638.194 ± 3858.660  ops/s
BatooBenchmark._16Fields    H2       10  thrpt   20   64638.056 ± 4692.277  ops/s
BatooBenchmark._16Fields    H2      100  thrpt   20    8910.878 ±  731.142  ops/s
BatooBenchmark._16Fields    H2     1000  thrpt   20     916.999 ±   40.375  ops/s

 */
public class BatooBenchmark {

	private EntityManagerFactory sf;
	
	@Param(value="H2")
	DbTarget db;


	@Setup
	public void init() throws Exception  {
		ConnectionParamWithJndi connParam = new ConnectionParamWithJndi();
		connParam.db = db;
		connParam.init();
		
		
		sf = Persistence.createEntityManagerFactory("batoo" );
	}

	@Benchmark
	public void _04Fields(LimitParam limit, final Blackhole blackhole) throws Exception {
		EntityManager session = sf.createEntityManager();
		try {
			Query query = session.createQuery("select s from MappedObject4 s");
			query.setMaxResults(limit.limit);
			List<?> sr = query.getResultList();
			for (Object o : sr) {
				blackhole.consume(o);
			}
		} finally {
			session.close();
		}
	}

	@Benchmark
	public void _16Fields(LimitParam limit, final Blackhole blackhole) throws Exception {
		EntityManager session = sf.createEntityManager();
		try {
			Query query = session.createQuery("select s from MappedObject16 s");
			query.setMaxResults(limit.limit);
			List<?> sr = query.getResultList();
			for (Object o : sr) {
				blackhole.consume(o);
			}
		} finally {
			session.close();
		}
	}
	
	public static void main(String[] args) throws Exception {
		ConnectionParamWithJndi connParam = new ConnectionParamWithJndi();
		connParam.db = DbTarget.HSQLDB;
		connParam.init();
		
		Map props = new HashMap();
		props.put("DATA_SOURCE", connParam.dataSource);
		
		EntityManagerFactory sf = Persistence.createEntityManagerFactory("batoo", props );
		EntityManager session = sf.createEntityManager();
		try {
			Query query = session.createQuery("select s from MappedObject4 s");
			query.setMaxResults(2);
			List<?> sr = query.getResultList();
			for (Object o : sr) {
				System.out.println( o.toString());
			}
		} finally {
			session.close();
		}
	}

}

package org.sfm.benchmark;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;

/*
init db
init db done
Jul 28, 2014 11:34:25 PM org.hibernate.annotations.common.reflection.java.JavaReflectionManager <clinit>
INFO: HCANN000001: Hibernate Commons Annotations {4.0.5.Final}
Jul 28, 2014 11:34:25 PM org.hibernate.Version logVersion
INFO: HHH000412: Hibernate Core {4.3.6.Final}
Jul 28, 2014 11:34:25 PM org.hibernate.cfg.Environment <clinit>
INFO: HHH000206: hibernate.properties not found
Jul 28, 2014 11:34:25 PM org.hibernate.cfg.Environment buildBytecodeProvider
INFO: HHH000021: Bytecode provider name : javassist
Jul 28, 2014 11:34:25 PM org.hibernate.cfg.Configuration addResource
INFO: HHH000221: Reading mappings from resource: db_object.hbm.xml
Jul 28, 2014 11:34:25 PM org.hibernate.internal.util.xml.DTDEntityResolver resolveEntity
WARN: HHH000223: Recognized obsolete hibernate namespace http://hibernate.sourceforge.net/. Use namespace http://www.hibernate.org/dtd/ instead. Refer to Hibernate 3.6 Migration Guide!
Jul 28, 2014 11:34:25 PM org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl configure
WARN: HHH000402: Using Hibernate built-in connection pool (not for production use!)
Jul 28, 2014 11:34:25 PM org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl buildCreator
INFO: HHH000401: using driver [null] at URL [jdbc:hsqldb:mem:benchmarkdb]
Jul 28, 2014 11:34:25 PM org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl buildCreator
INFO: HHH000046: Connection properties: {user=SA, password=****}
Jul 28, 2014 11:34:25 PM org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl buildCreator
INFO: HHH000006: Autocommit mode: false
Jul 28, 2014 11:34:25 PM org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl configure
INFO: HHH000115: Hibernate connection pool size: 20 (min=1)
Jul 28, 2014 11:34:25 PM org.hibernate.dialect.Dialect <init>
INFO: HHH000400: Using dialect: org.hibernate.dialect.HSQLDialect
Jul 28, 2014 11:34:25 PM org.hibernate.engine.transaction.internal.TransactionFactoryInitiator initiateService
INFO: HHH000399: Using default transaction strategy (direct JDBC transactions)
Jul 28, 2014 11:34:25 PM org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory <init>
INFO: HHH000397: Using ASTQueryTranslatorFactory
BigSelect elapsed 6351006000 1000000 6351
BigSelect elapsed 2660492000 1000000 2660
BigSelect elapsed 2373552000 1000000 2373
BigSelect elapsed 2344360000 1000000 2344
BigSelect elapsed 2354149000 1000000 2354
BigSelect elapsed 2439017000 1000000 2439
BigSelect elapsed 2358768000 1000000 2358
BigSelect elapsed 2366511000 1000000 2366
BigSelect elapsed 2392709000 1000000 2392
BigSelect elapsed 2360734000 1000000 2360
BigSelect elapsed 2365885000 1000000 2365
BigSelect elapsed 2386840000 1000000 2386
BigSelect elapsed 2423904000 1000000 2423
BigSelect elapsed 2450812000 1000000 2450
BigSelect elapsed 2448381000 1000000 2448
BigSelect elapsed 2360545000 1000000 2360
BigSelect elapsed 2336985000 1000000 2336
BigSelect elapsed 2355553000 1000000 2355
BigSelect elapsed 2342186000 1000000 2342
BigSelect elapsed 2317084000 1000000 2317
SmallSelect elapsed 35677038000 1000000 35677
SmallSelect elapsed 33044847000 1000000 33044
SmallSelect elapsed 33893664000 1000000 33893

 */
public class HibernateStatelessBenchmark {



	private SessionFactory sessionFactory;
	public HibernateStatelessBenchmark(SessionFactory sessionFactory)  {
		this.sessionFactory = sessionFactory;
	}
	
	private void runBigSelect() throws Exception {
		

		
		ValidateHandler handler = new ValidateHandler();
		
		Query createQuery = sessionFactory.openStatelessSession().createQuery("from DbObject ");
		
		long start = System.nanoTime();
		ScrollableResults sr = createQuery.scroll();
		
		while(sr.next()) {
			DbObject o = (DbObject) sr.get(0);
			handler.handle(o);
		}
		sr.close();
		long elapsed = System.nanoTime() - start;
		
		long c= handler.c;
		
		System.out.println("BigSelect elapsed " + elapsed + " " + c + " " + (elapsed / c));
		
	}
	
	
	private void runSmallSelect() throws Exception {
		
		Query query = sessionFactory.openStatelessSession().createQuery("from DbObject ").setMaxResults(1);
		
		ValidateHandler handler = new ValidateHandler();

		
		long start = System.nanoTime();

		for(int i = 0; i < 1000000; i++) {
			handler.handle((DbObject)query.uniqueResult());
		}
		long elapsed = System.nanoTime() - start;
		
		System.out.println("SmallSelect elapsed " + elapsed + " " + handler.c + " " + (elapsed / handler.c));
		
	}
	
	public static void main(String[] args) throws Exception {
		DbHelper.benchmarkDb();
		
		  // Create the SessionFactory from hibernate.cfg.xml
        Configuration configuration = new Configuration();
        
        configuration.addResource("db_object.hbm.xml");

        configuration.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:benchmarkdb");
        configuration.setProperty("hibernate.connection.username", "SA");
        configuration.setProperty("hibernate.connection.password", "");

        SessionFactory sf = configuration.buildSessionFactory(new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry());
		
		HibernateStatelessBenchmark benchmark = new HibernateStatelessBenchmark(sf);
		
		for(int i = 0; i < 20; i++) {
			benchmark.runBigSelect();
		}
		
		for(int i = 0; i < 20; i++) {
			benchmark.runSmallSelect();
		}
	}


}

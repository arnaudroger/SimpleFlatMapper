package org.sfm.benchmark;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.DbHelper;

/*

 */
public class HibernateBenchmark {



	private SessionFactory sessionFactory;
	public HibernateBenchmark(SessionFactory sessionFactory)  {
		this.sessionFactory = sessionFactory;
	}
	
	private void runBigSelect() throws Exception {
		
		ScrollableResults sr = sessionFactory.openSession().createQuery("from DbObject ").scroll();

		
		ValidateHandler handler = new ValidateHandler();
		
		
		long start = System.nanoTime();
		
		while(sr.next()) {
			DbObject o = (DbObject) sr.get(0);
			handler.handle(o);
		}
		
		long elapsed = System.nanoTime() - start;
		
		long c= handler.c;
		
		System.out.println("BigSelect elapsed " + elapsed + " " + c + " " + (elapsed / c));
		
	}
	
	
	private void runSmallSelect() throws Exception {
		
		Query query = sessionFactory.openSession().createQuery("from DbObject ").setMaxResults(1);
		
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
		
		HibernateBenchmark benchmark = new HibernateBenchmark(sf);
		
		for(int i = 0; i < 20; i++) {
			benchmark.runBigSelect();
		}
		
		for(int i = 0; i < 20; i++) {
			benchmark.runSmallSelect();
		}
	}


}

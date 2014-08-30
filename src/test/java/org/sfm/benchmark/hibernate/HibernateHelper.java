package org.sfm.benchmark.hibernate;

import java.sql.Connection;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.sfm.jdbc.mockdb.MockConnection;

@SuppressWarnings("deprecation")
public class HibernateHelper {

	
	public static SessionFactory getSessionFactory(Connection conn, boolean enableCache) {
		  // Create the SessionFactory from hibernate.cfg.xml
        Configuration configuration = new Configuration();
        
        configuration.addResource("db_object.hbm.xml");
        configuration.addResource("small_benchmark_object.hbm.xml");
        
        if (conn instanceof MockConnection) {
        	configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        }
        
        MyConnectionProviderImpl.setConnection(conn);

        configuration.setProperty(Environment.CONNECTION_PROVIDER, MyConnectionProviderImpl.class.getName());
        if (enableCache) {
            configuration.setProperty("hibernate.cache.use_query_cache", "true");
            configuration.setProperty("hibernate.cache.use_second_level_cache", "true");
            configuration.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        } else {
            configuration.setProperty("hibernate.cache.use_second_level_cache", "false");
            configuration.setProperty("hibernate.cache.use_query_cache", "false");
        }

        ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        
		return configuration.buildSessionFactory(sr);

	}
}

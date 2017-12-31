package com.gosun.hibernate.server;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionFactorySingleton {
	private static final Logger LOGGER=LoggerFactory.getLogger(SessionFactory.class);
	private static SessionFactory sessionFactory;
	
	public static synchronized SessionFactory getInstance(){
		if(sessionFactory!=null){
			return sessionFactory;
		}
		
		LOGGER.debug("开始创建SessionFactory");
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			// The registry would be destroyed by the SessionFactory, but we had
			// trouble building the SessionFactory
			// so destroy it manually.
			LOGGER.error("hibernate SessionFactory创建失败",e);
			StandardServiceRegistryBuilder.destroy(registry);
		}
		return sessionFactory;
	}
}

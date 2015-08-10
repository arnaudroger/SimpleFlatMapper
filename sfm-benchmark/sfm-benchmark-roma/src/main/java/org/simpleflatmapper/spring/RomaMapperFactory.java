package org.simpleflatmapper.spring;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.roma.impl.service.RowMapperService;

public class RomaMapperFactory {

	public static RowMapperService getRowMapperService() {
		GenericApplicationContext appContext = new GenericApplicationContext();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(appContext);
		reader.setValidating(false);
		reader.loadBeanDefinitions(new ClassPathResource("roma-context.xml"));
		appContext.refresh();
		return appContext.getBean(RowMapperService.class);
	}
}

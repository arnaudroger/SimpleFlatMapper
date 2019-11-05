package org.simpleflatmapper.reflect.test.meta;

import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMatchingScore;
import org.simpleflatmapper.reflect.meta.PropertyMeta;

public class TestPropertyFinderProbe implements PropertyFinder.PropertyFinderProbe {

	public static final TestPropertyFinderProbe INSTANCE = new TestPropertyFinderProbe();
	@Override
	public void found(PropertyMeta propertyMeta, PropertyMatchingScore score) {
		System.out.println(" found propertyMeta = " + propertyMeta.getPath() + " score " + score);
	}

	@Override
	public void select(PropertyMeta propertyMeta) {
		System.out.println(" select propertyMeta = " + propertyMeta.getPath());

	}
}
package org.sfm.map.impl;

public interface FieldKey<K> {
	String getName();
	int getIndex();
    K alias(String alias); 
}

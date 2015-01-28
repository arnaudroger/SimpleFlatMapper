package org.sfm.map;

public interface FieldKey<K> {
	String getName();
	int getIndex();
    K alias(String alias); 
}

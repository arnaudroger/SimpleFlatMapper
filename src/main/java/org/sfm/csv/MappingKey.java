package org.sfm.csv;

public interface MappingKey<K> {
	String getName();
	int getIndex();
    K alias(String alias); 
}

package org.sfm.map.impl;



public class KeyFieldMapperCouple<S, T, K> {
	
	private final K key;
	private final FieldMapper<S, T> fieldMapper;
	
	public KeyFieldMapperCouple(K key, FieldMapper<S, T> fieldMapper) {
		this.key = key;
		this.fieldMapper = fieldMapper;
	}

	public K getKey() {
		return key;
	}

	public FieldMapper<S, T> getFieldMapper() {
		return fieldMapper;
	}
	
	
}
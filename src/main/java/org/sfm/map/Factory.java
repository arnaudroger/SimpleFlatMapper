package org.sfm.map;

public interface Factory<G, K> {
	G newInstance(K key);
}

package org.sfm.utils;

public interface Handler<T> {
	void handle(T t) throws Exception;
}

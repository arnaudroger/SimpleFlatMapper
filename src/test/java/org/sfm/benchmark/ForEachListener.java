package org.sfm.benchmark;

import org.sfm.beans.DbObject;

public interface ForEachListener {

	void start();
	void object(DbObject o);
	void end();
	
}

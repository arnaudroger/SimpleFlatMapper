package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbObject;
import org.sfm.reflect.Getter;

public class DbObjectStringGetter implements Getter<DbObject, String> {

    @Override
    public String get(DbObject target) throws Exception {
        return target.getName();
    }
}

package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.test.beans.DbObject;

public class DbObjectStringGetter implements Getter<DbObject, String> {

    @Override
    public String get(DbObject target) throws Exception {
        return target.getName();
    }
}

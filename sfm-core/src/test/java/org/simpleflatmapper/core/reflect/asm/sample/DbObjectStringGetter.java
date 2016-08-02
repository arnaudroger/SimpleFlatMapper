package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.core.reflect.Getter;

public class DbObjectStringGetter implements Getter<DbObject, String> {

    @Override
    public String get(DbObject target) throws Exception {
        return target.getName();
    }
}

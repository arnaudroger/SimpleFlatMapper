package org.sfm.datastax.impl;

import com.datastax.driver.mapping.annotations.Column;
import org.sfm.datastax.impl.mapping.AnnotationDatastaxMapping;
import org.sfm.datastax.impl.mapping.DatastaxMapping;
import org.sfm.datastax.impl.mapping.DefaultDatastaxMapping;

public class DatastaxMappingFactory {



    private static boolean _isDatastaxMappingPresent() {
        try {
            return Column.class != null;
        } catch(Throwable e) {
            return false;
        }
    }


    private static final DatastaxMapping datastaxMapping;

    static {
        if (_isDatastaxMappingPresent()) {
            datastaxMapping = new AnnotationDatastaxMapping();
        } else {
            datastaxMapping = new DefaultDatastaxMapping();
        }
    }

    public static DatastaxMapping getDatastaxMapping() {
        return datastaxMapping;
    }


}

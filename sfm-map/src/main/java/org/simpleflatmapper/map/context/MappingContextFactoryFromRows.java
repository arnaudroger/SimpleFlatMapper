package org.simpleflatmapper.map.context;

import org.simpleflatmapper.map.MappingContext;

public interface MappingContextFactoryFromRows<ROW, ROWS, EX extends Throwable> {
    MappingContext<? super ROW> newMappingContext(ROWS rows) throws EX;
}

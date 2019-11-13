package org.simpleflatmapper.jooq;

import org.jooq.DSLContext;

public interface DSLContextProvider {

    DSLContext provide();

}

package org.sfm.datastax.impl.mapping;

import org.sfm.datastax.impl.KeyspaceTable;
import org.sfm.reflect.meta.AliasProvider;

public interface DatastaxMapping extends AliasProvider {
    KeyspaceTable lookForKeySpaceTable(Class<?> target);
}

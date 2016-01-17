package org.sfm.datastax.impl.mapping;

import org.sfm.datastax.impl.KeyspaceTable;

public interface DatastaxMapping {
    KeyspaceTable lookForKeySpaceTable(Class<?> target);
}

package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.BoundStatement;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.LongSetter;

public class LongBoundStatementSetter implements Setter<BoundStatement, Long>, LongSetter<BoundStatement> {
    private final int index;

    public LongBoundStatementSetter(int index) {
        this.index = index;
    }

    @Override
    public void setLong(BoundStatement target, long value) throws Exception {
        target.setLong(index, value);
    }

    @Override
    public void set(BoundStatement target, Long value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setLong(index, value);
        }
    }
}

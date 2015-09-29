package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.BoundStatement;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.LongSetter;

public class StringBoundStatementSetter implements Setter<BoundStatement, String> {
    private final int index;

    public StringBoundStatementSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(BoundStatement target, String value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setString(index, value);
        }
    }
}

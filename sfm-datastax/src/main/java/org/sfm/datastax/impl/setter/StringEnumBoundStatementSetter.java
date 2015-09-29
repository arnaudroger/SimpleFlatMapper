package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.BoundStatement;
import org.sfm.reflect.Setter;

import java.util.Date;

public class StringEnumBoundStatementSetter implements Setter<BoundStatement, Enum<?>> {
    private final int index;

    public StringEnumBoundStatementSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(BoundStatement target, Enum<?> value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setString(index, value.name());
        }
    }
}

package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.BoundStatement;
import org.sfm.reflect.Setter;

import java.util.Date;

public class DateBoundStatementSetter implements Setter<BoundStatement, Date> {
    private final int index;

    public DateBoundStatementSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(BoundStatement target, Date value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setDate(index, value);
        }
    }
}

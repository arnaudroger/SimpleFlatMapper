package org.sfm.datastax;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.LocalDate;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class DataTypeHelperTest {


    @Test
    public void testDate() {
        assertEquals(LocalDate.class, DataTypeHelper.asJavaClass(DataType.date()));
    }
    @Test
    public void testTinyInt() {
        assertEquals(Byte.class, DataTypeHelper.asJavaClass(DataType.tinyint()));
    }

    @Test
    public void testSmallInt() {
        assertEquals(Short.class, DataTypeHelper.asJavaClass(DataType.smallint()));
    }

    @Test
    public void testTime() {
        assertEquals(Long.class, DataTypeHelper.asJavaClass(DataType.time()));
    }

}

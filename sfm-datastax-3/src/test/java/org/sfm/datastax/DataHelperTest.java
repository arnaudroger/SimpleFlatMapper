package org.sfm.datastax;

import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.SettableByIndexData;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataHelperTest {

    @Test
    public void getTimestamp() throws Exception {
        GettableByIndexData data = mock(GettableByIndexData.class);
        Date now = new Date();
        when(data.getTimestamp(1)).thenReturn(now);
        assertEquals(now, DataHelper.getTimestamp(1, data));
    }

    @Test
    public void setTimestamp() throws Exception {
        SettableByIndexData data = mock(SettableByIndexData.class);
        Date now = new Date();
        DataHelper.setTimestamp(1, now, data);

        verify(data).setTimestamp(1, now);

    }
}

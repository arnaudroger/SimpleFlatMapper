package org.simpleflatmapper.converter.protobuf.test;

import com.google.protobuf.Timestamp;
import org.junit.Test;
import org.simpleflatmapper.converter.test.ConverterServiceTestHelper;


import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.simpleflatmapper.converter.test.ConverterServiceTestHelper.testConverter;

public class ProtobufConverterServiceTest {


    @Test
    public void testDateToTimestamp() throws Exception {
        Calendar cal = Calendar.getInstance();
        ConverterServiceTestHelper
                .testConverter(cal.getTime(), 
                        Timestamp.newBuilder().setSeconds(cal.getTime().getTime()/1000).setNanos((int)TimeUnit.MILLISECONDS.toNanos(cal.get(Calendar.MILLISECOND))).build());
    }

    @Test
    public void testLongToTimestamp() throws Exception {
        Calendar cal = Calendar.getInstance();
        ConverterServiceTestHelper
                .testConverter(cal.getTime().getTime(),
                        Timestamp.newBuilder().setSeconds(cal.getTime().getTime()/1000).setNanos((int)TimeUnit.MILLISECONDS.toNanos(cal.get(Calendar.MILLISECOND))).build());
    }
}
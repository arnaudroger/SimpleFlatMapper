package org.sfm.reflect.asm;


import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ShardingHelperTest {


    @Test
    public void testShardingWhenNoNeedToShard() {
        ShardingHelper.ShardCallBack callBack = mock(ShardingHelper.ShardCallBack.class);
        ShardingHelper.shard(5, 8, callBack);
        verify(callBack).leafDispatch("", 0, 5);

    }

    @Test
    public void testShardingWhen1LevelSharding() {
        ShardingHelper.ShardCallBack callBack = mock(ShardingHelper.ShardCallBack.class);
        ShardingHelper.shard(32, 8, callBack);

        verify(callBack).nodeDispatch("", 8, 0, 32);

        verify(callBack).leafDispatch("l0t7", 0, 7);
        verify(callBack).leafDispatch("l8t15", 8, 15);
        verify(callBack).leafDispatch("l16t23", 16, 23);
        verify(callBack).leafDispatch("l24t31", 24, 31);
        verify(callBack).leafDispatch("l32t32", 32, 32);
    }

    @Test
    public void testShardingWhen2LevelSharding() {
        ShardingHelper.ShardCallBack callBack = mock(ShardingHelper.ShardCallBack.class);
        ShardingHelper.shard(80, 8, callBack);

        verify(callBack).leafDispatch("l0t7", 0, 7);
        verify(callBack).leafDispatch("l8t15", 8, 15);
        verify(callBack).leafDispatch("l16t23", 16, 23);
        verify(callBack).leafDispatch("l24t31", 24, 31);
        verify(callBack).leafDispatch("l32t39", 32, 39);
        verify(callBack).leafDispatch("l40t47", 40, 47);
        verify(callBack).leafDispatch("l48t55", 48, 55);
        verify(callBack).leafDispatch("l56t63", 56, 63);

        verify(callBack).leafDispatch("l64t71", 64, 71);
        verify(callBack).leafDispatch("l72t79", 72, 79);
        verify(callBack).leafDispatch("l80t80", 80, 80);

        verify(callBack).nodeDispatch("n0t63", 8, 0, 63);
        verify(callBack).nodeDispatch("n64t80", 8, 64, 80);

        verify(callBack).nodeDispatch("", 64, 0, 80);




    }
}

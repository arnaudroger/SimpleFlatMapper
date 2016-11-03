package org.simpleflatmapper.reflect.test.asm;


import org.junit.Test;
import org.simpleflatmapper.reflect.asm.ShardingHelper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ShardingHelperTest {

    @Test
    public void testShardingWhenWhenEmpty() {
        ShardingHelper.ShardCallBack callBack = mock(ShardingHelper.ShardCallBack.class);
        ShardingHelper.shard(0, 8, callBack);
        verify(callBack).leafDispatch("", 0, 0);
    }

    @Test
    public void testShardingWhenNoNeedToShard() {
        ShardingHelper.ShardCallBack callBack = mock(ShardingHelper.ShardCallBack.class);
        ShardingHelper.shard(5, 8, callBack);
        verify(callBack).leafDispatch("", 0, 5);

    }

    @Test
    public void testShardingWhen4_2() {
        ShardingHelper.ShardCallBack callBack = mock(ShardingHelper.ShardCallBack.class);
        ShardingHelper.shard(4, 2, callBack);
        verify(callBack).leafDispatch("1n0t2", 0, 2);
        verify(callBack).leafDispatch("1n2t4", 2, 4);

        verify(callBack).nodeDispatch("", 2, 0, 4);
    }


    @Test
    public void testShardingWhen16() {
        ShardingHelper.ShardCallBack callBack = mock(ShardingHelper.ShardCallBack.class);
        ShardingHelper.shard(16, 16, callBack);

        verify(callBack).leafDispatch("", 0, 16);
    }


    @Test
    public void testShardingWhen5_2() {
        ShardingHelper.ShardCallBack callBack = mock(ShardingHelper.ShardCallBack.class);
        ShardingHelper.shard(5, 2, callBack);
        verify(callBack).leafDispatch("1n0t2", 0, 2);
        verify(callBack).leafDispatch("1n2t4", 2, 4);
        verify(callBack).leafDispatch("1n4t5", 4, 5);

        verify(callBack).nodeDispatch("2n0t4", 2, 0, 4);
        verify(callBack).nodeDispatch("2n4t5", 2, 4, 5);

        verify(callBack).nodeDispatch("", 4, 0, 5);
    }
    @Test
    public void testShardingWhenNoNeedToShard72() {
        ShardingHelper.ShardCallBack callBack = mock(ShardingHelper.ShardCallBack.class);
        ShardingHelper.shard(7, 2, callBack);

        verify(callBack).leafDispatch("1n0t2", 0, 2);
        verify(callBack).leafDispatch("1n2t4", 2, 4);
        verify(callBack).leafDispatch("1n4t6", 4, 6);
        verify(callBack).leafDispatch("1n6t7", 6, 7);


        verify(callBack).nodeDispatch("2n0t4", 2, 0, 4); // 4 / 4 = 1 , 3/4 = 0
        verify(callBack).nodeDispatch("2n4t7", 2, 4, 7);

        verify(callBack).nodeDispatch("", 4, 0, 7);


        /*
        n0t2 0 2
        n2t4 2 4
        n4t6 4 6
        n6t7 6 7

        n0t4/2 0 4
        n2t6/2 2 6
        n4t7/2 4 7
        n6t7/2 6 7

        /4 0 7
        /4 2 7
        /4 4 7
         /4 6 7

         */
    }

    @Test
    public void testShardingWhen1LevelSharding() {
        ShardingHelper.ShardCallBack callBack = mock(ShardingHelper.ShardCallBack.class);
        ShardingHelper.shard(33, 8, callBack);


        verify(callBack).leafDispatch("1n0t8", 0, 8);
        verify(callBack).leafDispatch("1n8t16", 8, 16);
        verify(callBack).leafDispatch("1n16t24", 16, 24);
        verify(callBack).leafDispatch("1n24t32", 24, 32);
        verify(callBack).leafDispatch("1n32t33", 32, 33);
        verify(callBack).nodeDispatch("", 8, 0, 33);
    }

    @Test
    public void testShardingWhen2LevelSharding() {
        ShardingHelper.ShardCallBack callBack = mock(ShardingHelper.ShardCallBack.class);
        ShardingHelper.shard(81, 8, callBack);

        verify(callBack).leafDispatch("1n0t8", 0, 8);
        verify(callBack).leafDispatch("1n8t16", 8, 16);
        verify(callBack).leafDispatch("1n16t24", 16, 24);
        verify(callBack).leafDispatch("1n24t32", 24, 32);
        verify(callBack).leafDispatch("1n32t40", 32, 40);
        verify(callBack).leafDispatch("1n40t48", 40, 48);
        verify(callBack).leafDispatch("1n48t56", 48, 56);
        verify(callBack).leafDispatch("1n56t64", 56, 64);

        verify(callBack).leafDispatch("1n64t72", 64, 72);
        verify(callBack).leafDispatch("1n72t80", 72, 80);
        verify(callBack).leafDispatch("1n80t81", 80, 81);

        verify(callBack).nodeDispatch("8n0t64", 8, 0, 64);
        verify(callBack).nodeDispatch("8n64t81", 8, 64, 81);

        verify(callBack).nodeDispatch("", 64, 0, 81);
    }
}

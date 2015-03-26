package org.sfm.reflect.asm;


public class ShardingHelper {


    public static void shard(int nb, int maxSize, ShardCallBack callBack) {
        if (nb < maxSize) {
            callBack.leafDispatch("", 0, nb);
        } else {

            int currentSize = nb;
            int currentDivider = 1;

            while(currentSize > 0) {

                int i = 0;
                do {
                    int end = Math.min(nb, i + (currentDivider * maxSize) - 1);
                    if (currentSize == nb) {
                        callBack.leafDispatch("l" + i + "t" + end, i, end);
                    } else if (currentSize < maxSize) {
                        callBack.nodeDispatch("", currentDivider, i, end);
                    } else {
                        callBack.nodeDispatch("n" + i + "t" + end, currentDivider, i, end);
                    }
                    i += maxSize;
                } while( i <= nb);


                currentDivider = currentDivider * maxSize;
                currentSize /= maxSize;
            }


        }
    }

    public static interface ShardCallBack {

        void leafDispatch(String suffix, int start, int end);

        void nodeDispatch(String suffix, int divide, int start, int end);
    }
}

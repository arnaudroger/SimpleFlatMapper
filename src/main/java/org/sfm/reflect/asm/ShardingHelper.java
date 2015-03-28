package org.sfm.reflect.asm;


public class ShardingHelper {


    public static void shard(int nb, int maxSize, ShardCallBack callBack) {
        if (nb == 0) {
            callBack.leafDispatch("", 0, 0);
        } else if (nb < maxSize) {
            callBack.leafDispatch("", 0, nb);
        } else {

            int currentSize = nb;
            int currentDivider = 1;

            while(currentSize > 0) {
                int nextSize = currentSize /maxSize;
                int i = 0;
                do {
                    int pEnd = i + (currentDivider * maxSize);

                    int end = Math.min(nb, pEnd);


                    if (currentSize == nb) {
                        callBack.leafDispatch(currentDivider + "n" + i + "t" + end, i, end);
                    } else if (nextSize == 0) {
                        callBack.nodeDispatch("", currentDivider, i, end);
                    } else {
                        callBack.nodeDispatch(currentDivider + "n" + i + "t" + end, currentDivider, i, end);
                    }
                    i += (currentDivider * maxSize);
                } while( i < nb);


                currentDivider = currentDivider * maxSize;
                currentSize = nextSize;
            }


        }
    }

    public interface ShardCallBack {

        void leafDispatch(String suffix, int start, int end);

        void nodeDispatch(String suffix, int divide, int start, int end);
    }
}

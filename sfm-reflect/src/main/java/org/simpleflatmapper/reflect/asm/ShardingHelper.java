package org.simpleflatmapper.reflect.asm;


public class ShardingHelper {


    public static void shard(int nb, int maxSize, ShardCallBack callBack) {
        if (nb == 0) {
            callBack.leafDispatch("", 0, 0);
        } else if (nb <= maxSize) {
            callBack.leafDispatch("", 0, nb);
        } else {

            int currentSize = nb;
            int currentDivider = 1;
            boolean root = false;
            while(!root) {
                int nextSize = currentSize /maxSize;

                root = (nb -  nextSize * (currentDivider * maxSize)) == 0 ?  nextSize <= 1 : nextSize <= 0;

                int i = 0;
                do {
                    int pEnd = i + (currentDivider * maxSize);

                    int end = Math.min(nb, pEnd);


                    if (currentSize == nb) {
                        callBack.leafDispatch(currentDivider + "n" + i + "t" + end, i, end);
                    } else if (root) {
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

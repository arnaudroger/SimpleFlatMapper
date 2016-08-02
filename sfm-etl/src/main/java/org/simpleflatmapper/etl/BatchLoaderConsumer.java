package org.simpleflatmapper.etl;

import org.jctools.queues.MessagePassingQueue;

import java.util.ArrayList;

public class BatchLoaderConsumer<T> {

//    private final MessagePassingQueue<T> messagePassingQueue;
//    private final BatchLoader<T> batchLoader;
//
//    private final int limit;
//    private final ArrayList<T> bucket;
//
//    private volatile boolean run = true;
//    public BatchLoaderConsumer(MessagePassingQueue<T> messagePassingQueue, BatchLoader<T> batchLoader) {
//        this.messagePassingQueue = messagePassingQueue;
//        this.batchLoader = batchLoader;
//    }
//
//    public void run() {
//        final QueueConsumer queueConsumer = new QueueConsumer();
//        do {
//            messagePassingQueue.drain(queueConsumer, limit);
//
//            batchLoader.load(bucket);
//
//            bucket.clear();
//
//        } while(run);
//
//
//    }
//
//    private class QueueConsumer implements MessagePassingQueue.Consumer<T> {
//        @Override
//        public void accept(T e) {
//            bucket.add(e);
//        }
//    }
}

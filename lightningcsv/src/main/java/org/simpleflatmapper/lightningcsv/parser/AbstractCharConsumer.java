package org.simpleflatmapper.lightningcsv.parser;



import java.io.IOException;

public abstract class AbstractCharConsumer {
    public abstract void consumeAllBuffer(CellConsumer cellConsumer);

    public abstract boolean consumeToNextRow(CellConsumer cellConsumer);

    public abstract void finish(CellConsumer cellConsumer);

    public abstract boolean next() throws IOException;
    
}

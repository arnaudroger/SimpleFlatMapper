package org.simpleflatmapper.jdbc.named;

import java.util.ArrayList;
import java.util.List;

public class TupleBuilder {

    private final Symbol word;
    private List<Symbol> arguments = new ArrayList<Symbol>(10);
    private final int start;

    public TupleBuilder(Symbol word, int start) {
        this.word = word;
        this.start = start;
    }

    public void add(Symbol argument) {
        arguments.add(argument);
    }

    public Tuple toTuple(int end) {
        return new Tuple(word, arguments.toArray(new Symbol[0]), new Position(start, end));
    }

    public int size() {
        return arguments.size();
    }
}

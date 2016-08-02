package org.simpleflatmapper.jdbc.named;

public class Tuple extends Symbol {

    private final Symbol word;
    private final Symbol[] arguments;

    public Tuple(Symbol word, Symbol[] arguments, Position position) {
        super(position);
        this.word = word;
        this.arguments = arguments;
    }

    public int size() {
        return arguments.length;
    }

    public Symbol getSymbol(int position) {
        return arguments[position];
    }
}

package org.simpleflatmapper.jdbc.named;



import java.util.ArrayList;
import java.util.List;

public final class NamedSqlQueryParser {
    final List<Symbol> symbols = new ArrayList<Symbol>(10);
    final List<TupleBuilder> tuples = new ArrayList<TupleBuilder>();
    int lastMarkStart = -1;
    int lastMarkEnd = -1;

    private final Callback callback;

    public NamedSqlQueryParser(Callback callback) {
        this.callback = callback;
    }

    public void parse(CharSequence cs) {

        for(int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            processChar(cs, i, c);
        }

        if(lastMarkStart != -1) {
            processSymbol(cs, cs.length());
        }

    }

    private void processChar(CharSequence cs, int i, char c) {
        if (c == '(') {
            openFunction(cs, i);

        } else if (c == ')') {
            closeFunction(cs, i);

        } else if (isSpaceOrSymbol(c)) {
            if (lastMarkStart != -1 && lastMarkEnd == -1) {
                lastMarkEnd = i;
            }
        } else  {
            if(lastMarkEnd != -1) {
                processSymbol(cs, i);
            }

            if (lastMarkStart == -1) {
                lastMarkStart = i;
            }
        }
    }

    private void closeFunction(CharSequence cs, int i) {
        if (lastMarkStart != -1) {
            processSymbol(cs, i);
        }

        // end of tuples
        TupleBuilder topTuple = tuples.remove(this.tuples.size() - 1);
        Tuple tuple = topTuple.toTuple(i + 1);
        if (tuples.isEmpty()) {
            symbols.add(tuple);
        } else {
            tuples.get(this.tuples.size() - 1).add(tuple);
        }
    }

    private void openFunction(CharSequence cs, int i) {
        // new tuples
        Symbol word = consumeSymbol(cs, i);
        int start = word.getPosition().getStart();
        if (start == -1) {
            start = i;
        }
        tuples.add(new TupleBuilder(word, start));
    }

    private void processSymbol(CharSequence cs, int length) {
        Symbol symbol = consumeSymbol(cs, length);
        if (tuples.isEmpty()) {
            symbols.add(symbol);
        } else {
            tuples.get(this.tuples.size() - 1).add(symbol);
        }
    }

    private boolean isSpaceOrSymbol(char c) {
        return c == ' ' || c == '=' || c == '<' || c == '>' || c == '!' || c == ','|| c == '\r' || c == '\n' ;
    }

    private Symbol consumeSymbol(CharSequence cs, int i) {
        String name = getName(cs, i);
        Position position = consumePosition();
        if (name.startsWith(":")) {
            NamedParameter namedParameter = new NamedParameter(name.substring(1), position);

            callback.param(namedParameter);
            return namedParameter;

        } else if (name.equals("?")) {
            Parameter parameter = new Parameter(position);

            Word extrapolateName = findName(parameter);

            callback.param(new NamedParameter(extrapolateName.getName(), position));
            return parameter;
        }
        return new Word(name, position);
    }

    private Word findName(Parameter parameter) {
        if (symbols.isEmpty()) {
            throw new IllegalArgumentException("Cannot find name for ? at " + parameter.getPosition().getStart());
        }
        if (!tuples.isEmpty()) {
            Symbol s = symbols.get(symbols.size() - 1);

            if (s instanceof Tuple) {
                Word w = lookForArg(0, (Tuple)s);
                if (w != null) {
                    return w;
                }
            }
        }

        for(int i = symbols.size() - 1; i >= 0; i--) {
            Symbol s = symbols.get(i);
            if (Word.class.isInstance(s)) {
                return (Word) s;
            } else if (Tuple.class.isInstance(s)) {
                Word word = getFirstArgument((Tuple)s);
                if (word != null) return word;
            }
        }
        throw new IllegalArgumentException("Cannot find name for ? at " + parameter.getPosition().getStart());

    }

    private Word getFirstArgument(Tuple arg) {
        if (arg.size() == 0) return null;
        Symbol s = arg.getSymbol(0);

        if (s instanceof Word) {
            return (Word) s;
        } else if (s instanceof Tuple) {
            return getFirstArgument((Tuple)s);
        }

        return null;
    }

    private Word lookForArg(int stackLevel, Tuple s) {
        TupleBuilder tupleBuilder = tuples.get(stackLevel);
        int position = tupleBuilder.size();

        if (s.size() > position) {
            Symbol symbol = s.getSymbol(position);

            if (Word.class.isInstance(symbol)) {
                return (Word) symbol;
            } else if (stackLevel  + 1< tuples.size() && symbol instanceof Tuple) {
                return lookForArg(stackLevel + 1, (Tuple) symbol);
            }
        }
        return null;
    }

    private Position consumePosition() {
        Position position = new Position(lastMarkStart, lastMarkEnd);
        lastMarkEnd = -1;
        lastMarkStart = -1;
        return position;
    }

    private String getName(CharSequence cs, int i) {
        String name;
        if (lastMarkStart != -1) {
            if (lastMarkEnd == -1) {
                lastMarkEnd = i;
            }
            name = cs.subSequence(lastMarkStart, lastMarkEnd).toString();
        } else {
            name = "";
        }
        return name;
    }


    public interface Callback {
        void param(NamedParameter namedParameter);
    }
}

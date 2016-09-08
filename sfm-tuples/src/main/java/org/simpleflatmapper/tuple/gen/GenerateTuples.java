package org.simpleflatmapper.tuple.gen;


import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class GenerateTuples {


    public static void generateTuple(Writer writer, int from, int size, boolean upward) throws IOException {
        writer.append("package org.simpleflatmapper.tuple;\n\n");
        writer.append("public class Tuple").append(Integer.toString(size)).append("<");

        for(int i = 0; i < size; i++) {
            if (i != 0) writer.append(", ");
            writer.append("T").append(Integer.toString(i+1));
        }



        writer.append(">");

        if (from > 0) {
            writer.append(" extends Tuple").append(Integer.toString(from)).append("<");
            for(int i = 0; i < from; i++) {
                if (i != 0) writer.append(", ");
                writer.append("T").append(Integer.toString(i+1));
            }

            writer.append(">");
        }

        writer.append(" {\n");


        // variable
        writer.append("\n");

        for(int i = from; i < size; i++) {
            writer.append("    private final T").append(Integer.toString(i+1)).append(" element").append(Integer.toString(i)).append(";\n");
        }

        writer.append("\n");


        // constructor
        writer.append("    public Tuple").append(Integer.toString(size)).append("(");
        for(int i = 0; i < size; i++) {
            if (i != 0) writer.append(", ");
            writer.append("T").append(Integer.toString(i + 1)).append(" element").append(Integer.toString(i));
        }
        writer.append(") {\n");

        if (from >0) {
            writer.append("        super(");
            for(int i = 0; i < from; i++) {
                if (i != 0) writer.append(", ");
                writer.append("element").append(Integer.toString(i));
            }
            writer.append(");\n");
        }

        for(int i = from; i < size; i++) {
            writer.append("        this.element").append(Integer.toString(i)).append(" = element").append(Integer.toString(i)).append(";\n");
        }
        writer.append("    }\n\n");


        // accessor
        for(int i = from; i < size; i++) {
            writer.append("    public final T").append(Integer.toString(i + 1)).append(" getElement").append(Integer.toString(i)).append("() {\n");
            writer.append("        return element").append(Integer.toString(i)).append(";\n");

            writer.append("    }\n\n");

            String thName = getThName(i);
            if (thName != null) {
                writer.append("    public final T").append(Integer.toString(i + 1)).append(" ").append(thName).append("() {\n");
                writer.append("        return getElement").append(Integer.toString(i)).append("();\n");
                writer.append("    }\n\n");
            }
        }

        // equals
        writer
                .append("    @Override\n")
                .append("    public boolean equals(Object o) {\n")
                .append("        if (this == o) return true;\n")
                .append("        if (o == null || getClass() != o.getClass()) return false;\n");


        if (from > 0) {
            writer.append("        if (!super.equals(o)) return false;\n");
        }
        writer.append("\n");

        writer
                .append("        Tuple").append(Integer.toString(size)).append(" tuple").append(Integer.toString(size))
                .append(" = (Tuple").append(Integer.toString(size)).append(") o;\n");

        writer.append("\n");
        for(int i = from; i < size; i++) {
            writer
                    .append("        if (element")
                    .append(Integer.toString(i))
                    .append(" != null ? !element")
                    .append(Integer.toString(i))
                    .append(".equals(tuple")
                    .append(Integer.toString(size))
                    .append(".element")
                    .append(Integer.toString(i))
                    .append(") : tuple")
                    .append(Integer.toString(size))
                    .append(".element")
                    .append(Integer.toString(i))
                    .append(" != null) return false;\n");
        }

        writer
                .append("\n        return true;\n    }\n\n");


        // hashcode
        writer
                .append("    @Override\n")
                .append("    public int hashCode() {\n");

        if (from > 0) {
            writer.append("        int result = super.hashCode();\n");
        }
        for(int i = from; i < size; i++) {
            writer.append("        ");
            if (i == 0) {
                writer.append("int ");
            }
            writer.append("result = ");
            if (i == 0) {
                writer.append("element0 != null ? element0.hashCode() : 0;\n");
            } else {
                writer.append("31 * result + (element").append(Integer.toString(i)).append(" != null ? element").append(Integer.toString(i)).append(".hashCode() : 0);\n");
            }
        }
        writer
                .append("        return result;\n")
                .append("    }\n\n");

        // to string
        writer
                .append("    @Override\n")
                .append("    public String toString() {\n");

        writer
                .append("        return \"Tuple").append(Integer.toString(size)).append("{\" +\n");

        for(int i = 0; i < size; i++) {
            writer
                    .append("                \"");
            if (i > 0) {
                writer.append(", ");
            }
            writer
                    .append("element")
                    .append(Integer.toString(i))
                    .append("=\" + getElement")
                    .append(Integer.toString(i))
                    .append("() +\n");
        }

        writer
                .append("                '}';\n");
        writer.append("    }\n");

        if (upward) {
            writer.append("\n");
            writer.append("    public <T").append(Integer.toString(size + 1)).append("> Tuple").append(Integer.toString(size + 1)).append("<");

            for (int i = 0; i <= size; i++) {
                if (i != 0) writer.append(", ");
                writer.append("T").append(Integer.toString(i + 1));
            }
            writer.append("> tuple").append(Integer.toString(size + 1)).append("(T").append(Integer.toString(size + 1)).append(" element").append(Integer.toString(size)).append(") {\n");
            writer.append("        return new Tuple").append(Integer.toString(size + 1)).append("<");
            for (int i = 0; i <= size; i++) {
                if (i != 0) writer.append(", ");
                writer.append("T").append(Integer.toString(i + 1));
            }
            writer.append(">(");
            for (int i = 0; i < size; i++) {
                if (i != 0) writer.append(", ");
                writer.append("getElement").append(Integer.toString(i)).append("()");
            }
            writer.append(", element").append(Integer.toString(size));
            writer.append(");\n");
            writer.append("    }\n");
        }

        writer.append("}\n");
    }

    public static String getThName(int i) {
        switch (i) {
            case 0: return "first";
            case 1: return "second";
            case 2: return "third";
            case 3: return "fourth";
            case 4: return "fifth";
            case 5: return "sixth";
            case 6: return "seventh";
            case 7: return "eighth";
            case 8: return "ninth";
            case 9: return "tenth";
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        for(int i = 2; i <= 32; i++) {
            write(i);
        }
    }

    private static void write(int i) throws IOException {
        FileWriter writer = new FileWriter("src/main/java/org/simpleflatmapper/tuples/Tuple" + i + ".java");
        try {
            generateTuple(writer, i == 2 ? 0 : i - 1, i , i != 32);
        } finally {
            writer.close();
        }
    }

}

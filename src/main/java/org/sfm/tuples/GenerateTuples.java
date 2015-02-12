package org.sfm.tuples;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class GenerateTuples {


    public static void generateTuple(Writer writer, int size) throws IOException {
        writer.append("package org.sfm.tuples;\n\n");
        writer.append("public class Tuple").append(Integer.toString(size)).append("<");

        for(int i = 0; i < size; i++) {
            if (i != 0) writer.append(", ");
            writer.append("T").append(Integer.toString(i+1));
        }



        writer.append("> {\n");


        // variable
        writer.append("\n");

        for(int i = 0; i < size; i++) {
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
        for(int i = 0; i < size; i++) {
            writer.append("        this.element").append(Integer.toString(i)).append(" = element").append(Integer.toString(i)).append(";\n");
        }
        writer.append("    }\n\n");


        // accessor
        for(int i = 0; i < size; i++) {
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
                .append("        if (o == null || getClass() != o.getClass()) return false;\n\n")
                .append("        Tuple").append(Integer.toString(size)).append(" tuple").append(Integer.toString(size))
                .append(" = (Tuple").append(Integer.toString(size)).append(") o;\n\n");

        for(int i = 0; i < size; i++) {
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
/*
    @Override
    public int hashCode() {
        int result = element0 != null ? element0.hashCode() : 0;
        result = 31 * result + (element1 != null ? element1.hashCode() : 0);
        return result;
    }
 */
        writer
                .append("    @Override\n")
                .append("    public int hashCode() {\n");
        for(int i = 0; i < size; i++) {
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
        /*
            @Override
    public String toString() {
        return "Tuple2{" +
                "element0=" + element0 +
                ", element1=" + element1 +
                '}';
    }
         */
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
                    .append("=\" + element")
                    .append(Integer.toString(i))
                    .append(" +\n");
        }

        writer
                .append("                '}';\n");
        writer.append("    }\n");

        writer.append("}\n");
    }

    private static String getThName(int i) {
        switch (i) {
            case 0: return "first";
            case 1: return "second";
            case 2: return "third";
            case 3: return "forth";
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
        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        generateTuple(writer, 4);
        writer.flush();
    }
}

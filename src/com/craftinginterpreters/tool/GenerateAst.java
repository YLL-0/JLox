package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.time.temporal.IsoFields;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.print("Usage: generated_asg <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        // gre za 'script' ki generera Expr.java
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary : Expr left, Token operator, Expr right:",
                "Grouping : Expr expression",
                "Literal: Object value",
                "Unary: Token operator, Expr right"));
    }

    // funkcioja za Abstract syntax tree
    private static void defineAst(
            String outputDir, String baseName, List<String> types)
            throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        // konstrukot + sutvarimo datoteko .java

        // zapisemo package, importe in calss name v .java datoteko
        writer.println("package com.craftinginterpreters.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println("}");
        writer.close();
    }

    private static void defineType(
            PrintWriter writer,
            String baseName,
            String className,
            String fieldList) {

        writer.println("  static class " + className + " extends " +
                baseName + " {");
        // konstrukotr:
        writer.println("    " + className + "(" + fieldList + ") {");

        // shranimo v fields
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("      this." + name + " = " + name + ";");
        }

        writer.println("    }");

        // fields
        writer.println();
        for (String field : fields) {
            writer.println("    final " + field + ";");
        }
        writer.println("  }");
    }
    // zdej se ze pocasi vid priblizno kako bo na koncu izgledalo
}

/*
 * priemr
 * 
 * package com.craftinginterpreters.lox;
 * 
 * abstract class Expr {
 * static class Binary extends Expr {
 * Binary(Expr left, Token operator, Expr right) {
 * this.left = left;
 * this.operator = operator;
 * this.right = right;
 * }
 * 
 * final Expr left;
 * final Token operator;
 * final Expr right;
 * }
 * 
 * ...
 * }
 * 
 */
package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;



// var language = "lox";
// when we scan through the list of chars. and group them together into sequences we get a LEXEME.
// Lexems together form a token



public class Lox {

 static boolean hadError = false;


// reads a file and executes it
private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));
}

// interpreter prompt is called a REPL {Read a line imput, Evalate it, Print the resault then Loop everything}
private static void runPrompt() throws IOException{
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;){
        System.out.println("> ");
        String line = reader.readLine();
        if (line == null) break;
        run(line);
    }
}

// printing tokens the scanner wil emit
private static void run(String source){
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokns();

    for (Token token : tokens){
        System.out.println(token);
    }
}
// error and report tell some syntax error occurred on a given line (this is bare minimum)
static void error(int line, String message){
    report(line, " ", message);
}

// better to knew where it happened and not "There is an error gl hf"
private static void report(int line, String where, String message){
    System.err.println("[line " + "] Error" + where + ": "+ message);
    hadError = true;
}

     static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }
}
package com.craftinginterpreters.lox;
import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

public class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    // start point at the first char in the lexeme being scanned,
    // current points at the cahr currently being considered
    // line tracks what source line current is on


    Scanner(String source){
        this.source = source;
    }

    List<Token> scanTokens(){
       while (!isAtEnd()){
           // we are at the beginning of the next lexeme
           start = current;
           scanTokens();
       }
       tokens.add(new Token(EOF,"",null,line));
       return tokens;
    }

    private void scanToken(){
        char c = advance();
        switch (c){
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            // because =, !, <, > can have a following statement(!=,<=, =>...) we handle them a bit different
            // that's why we need to look for the second character
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            // if Lox doesn't support a char it prints it as an error
            default: Lox.error(line, "Unexpected character"); break;
        }
    }


    // like advance() we only consume the current char if It's what were looking for
    // basically we check if the lexems in to stages and if we jump to the second one
    private boolean match(char expected){
        if (isAtEnd())  return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }



    private boolean isAtEnd(){
        return  current >= source.length();
    }


    // in the input it consumes the next character in the source file and returns it
    private char advance(){
        return source.charAt(current++);
    }


    // it creates a Token form the lexeme
    private void addToken(TokenType type){
        addToken(type,null);
    }

    private void addToken(TokenType type, Object lineteral){
        String text = source.substring(start, current);
        tokens.add(new Token(type,text,literal,line));

    }



}

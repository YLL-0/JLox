package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    // hash map shranjenin identifierjev za vse pomembne keywoarde
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    // start point at the first char in the lexeme being scanned,
    // current points at the char currently being considered
    // line tracks what source line current is on

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            // because =, !, <, > can have a following statement(!=,<=, =>...) we handle
            // them a bit different
            // that's why we need to look for the second character
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            // if Lox doesn't support a char it prints it as an error
            case '/':
                // preveri ce je komentar do konca vrstice
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd())
                        advance();
                } else {
                    addToken(SLASH);
                }
                break;
            // celoten point tega if je da iscemo druki / in gre do konca vrstice da vemo
            // ali je komenrat ali samo /
            case ' ':
            case '\r':
            case '\t':
                break;
            // ignore whitespace, ce je whitespace skoci na zacetek scan loopa
            case '\n':
                line++;
                break;

            case '"':
                string();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAplha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character");
                }
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null)
            type = IDENTIFIER;
        addToken(type);

    }

    // iscemo '.' ker imamo stevilke shranjene kot 123.0, torej . nam pvoe da je za
    // njo samo se ena stevilka
    private void number() {
        while (isDigit(peek()))
            advance();

        // gledamo za plavajoco vejicondro
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek()))
                advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    // po prvem " preverja in shranjuje sting dokler ne njade ending "
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }
        // zaperanje
        advance();

        // trim the surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);

    }

    // like advance() we only consume the current char if It's what were looking for
    // basically we check if the lexems in to stages and if we jump to the second
    // one
    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    // 'lokahead' pomaga nam videti kaj je nasledni char
    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    // pogelda ce obstarja nasledni po peek in ce ga vrne
    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAplha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAplha(c) || isDigit(c);
    }

    // ko vemo ali je stevilka ga ra razdelimo na dele kakor pri string
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    // in the input it consumes the next character in the source file and returns it
    private char advance() {
        return source.charAt(current++);
    }

    // it creates a Token form the lexeme
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));

    }
    // Maximal munch

}

package com.mycompany;

import static java.util.UUID.randomUUID;

public class Token {

    public static Token generate() {
        Token t = new Token(randomUUID().toString());
        return t;
    }

    private final String token;

    public Token(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token;
    }

}

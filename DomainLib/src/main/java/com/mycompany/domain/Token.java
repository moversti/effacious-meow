package com.mycompany.domain;

import static java.util.UUID.randomUUID;

public class Token {

    public static Token generate() {
        Token t = new Token(randomUUID().toString());
        return t;
    }

    private final String token;
    private final boolean valid;

    public Token(String token, boolean valid) {
        this.token = token;
        this.valid = valid;
    }

    public Token(String token) {
        this.token = token;
        valid = true;
    }

    public String getToken() {
        return token;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        return token;
    }

}

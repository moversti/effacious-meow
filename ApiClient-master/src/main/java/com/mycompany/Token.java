package com.mycompany;

public class Token {
    String token;

    public Token() {
    }

    public Token(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token;
    }
}

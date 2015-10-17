package com.mycompany.domain;

import java.util.UUID;

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
    
    public static Token generate(){
        Token t = new Token(UUID.randomUUID().toString());
        return t;
    }
}

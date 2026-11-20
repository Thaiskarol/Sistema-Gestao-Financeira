package com.bradesco.exception;

public class TokenInvalidoException extends RuntimeException {

    public TokenInvalidoException(String mensagem) {
        super(mensagem);
    }
}
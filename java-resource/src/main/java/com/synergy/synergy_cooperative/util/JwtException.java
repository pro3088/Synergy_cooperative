package com.synergy.synergy_cooperative.util;

public class JwtException extends RuntimeException{

    public JwtException(){
        super();
    }

    public JwtException(String message){
        super(message);
    }
}

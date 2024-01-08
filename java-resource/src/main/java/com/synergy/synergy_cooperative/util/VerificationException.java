package com.synergy.synergy_cooperative.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VerificationException extends RuntimeException{

    public VerificationException() {
        super();
    }

    public VerificationException(final String message) {
        super(message);
    }

}

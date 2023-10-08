package com.poczinha.log.bean;

import java.util.function.Function;

public class LogAuthVerifier {

    private final Function<String, Boolean> verifier;

    public LogAuthVerifier(Function<String, Boolean> verifier) {
        this.verifier = verifier;
    }

    public boolean verify(String role) {
        return verifier.apply(role);
    }
}

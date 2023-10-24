package com.poczinha.log.bean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogAuthVerifierTest {

    @Test
    @DisplayName("Should return true when user is allowed to log")
    void shouldReturnTrueWhenUserIsAllowedToLog() {
        LogAuthVerifier logAuthVerifier = new LogAuthVerifier(item -> true);
        assertTrue(logAuthVerifier.verify("any"));
    }
}
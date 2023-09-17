package com.poczinha.log.processor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrefixLogger {
    private final Logger logger;
    private final String prefix = "\uD83D\uDCDC ";

    public PrefixLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public void info(String message) {
        logger.info(prefix + " " + message);
    }

    public void debug(String message) {
        logger.debug(prefix + " " + message);
    }

    public void warn(String message) {
        logger.warn(prefix + " " + message);
    }

    public void error(String message) {
        logger.error(prefix + " " + message);
    }

    public void info(String message, Object... args) {
        logger.info(prefix + " " + message, args);
    }

    public void debug(String message, Object... args) {
        logger.debug(prefix + " " + message, args);
    }

    public void warn(String message, Object... args) {
        logger.warn(prefix + " " + message, args);
    }

    public void error(String message, Object... args) {
        logger.error(prefix + " " + message, args);
    }

    public void error(String message, Throwable t, Object... args) {
        logger.error(prefix + " " + message, t, args);
    }
}

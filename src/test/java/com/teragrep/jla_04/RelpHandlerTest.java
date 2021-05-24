package com.teragrep.jla_04;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.jupiter.api.*;

public class RelpHandlerTest {
    static final Logger logger = Logger.getLogger(RelpHandler.class.getName());

    @Test
    @DisplayName("Tests normal usage")
    public void testUsage() {
        Assertions.assertAll(() -> {
            // Custom logger name
            System.setProperty("java.util.logging.RelpHandler.custom.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.custom.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.custom.appname", "custom");

            // Default logger name
            System.setProperty("java.util.logging.RelpHandler.default.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.default.server.port", "1667");
            System.setProperty("java.util.logging.RelpHandler.default.appname", "Default");

            // Create handlers
            RelpHandler relpHandler_custom = new RelpHandler("custom");
            RelpHandler relpHandler_default = new RelpHandler();

            // Reset default logmanager and add the handlers
            LogManager.getLogManager().reset();
            logger.addHandler(relpHandler_custom);
            logger.addHandler(relpHandler_default);

            // Set level and send messages
            logger.setLevel(Level.WARNING);
            logger.severe("Severe message");
            logger.warning("Warning message");
            logger.info("Info message");
        });
    }

    @Test
    @DisplayName("Tests Structured Data usage")
    public void testSD() {
        Assertions.assertAll(() -> {
            // We want to use structured data
            System.setProperty("java.util.logging.RelpHandler.withsd.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.withsd.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.withsd.appname", "withsd");
            System.setProperty("java.util.logging.RelpHandler.withsd.useStructuredData", "true");

            // Create handlers
            RelpHandler relpHandler_withsd = new RelpHandler("withsd");

            // Reset default logmanager and add the handlers
            LogManager.getLogManager().reset();
            logger.addHandler(relpHandler_withsd);

            // Set level and send messages
            logger.setLevel(Level.INFO);
            logger.info("With Structured Data");
        });
    }
}
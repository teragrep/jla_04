/*
   Java Util Logging RELP Handler
   Copyright (C) 2021  Suomen Kanuuna Oy

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.teragrep.jla_04;

import java.util.Date;
import java.util.logging.*;

import org.junit.jupiter.api.*;

public class RelpHandlerTest {
    static final Logger logger = Logger.getLogger(RelpHandler.class.getName());

    //@Test
    @DisplayName("Tests normal usage")
    public void testUsage() {
        Assertions.assertAll(() -> {
            // Custom logger name
            System.setProperty("java.util.logging.RelpHandler.custom.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.custom.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.custom.appname", "custom");
            System.setProperty("java.util.logging.RelpHandler.custom.hostname", "customhost");

            // Default logger name
            System.setProperty("java.util.logging.RelpHandler.default.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.default.server.port", "1667");
            System.setProperty("java.util.logging.RelpHandler.default.appname", "Default");
            System.setProperty("java.util.logging.RelpHandler.default.hostname", "localhost");

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

    //@Test
    @DisplayName("Tests Structured Data usage")
    public void testSD() {
        Assertions.assertAll(() -> {
            // We want to use structured data
            System.setProperty("java.util.logging.RelpHandler.withsd.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.withsd.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.withsd.appname", "withsd");
            System.setProperty("java.util.logging.RelpHandler.withsd.hostname", "sdhost");
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

    //@Test
    @DisplayName("Tests Simple formatter")
    public void testSimpleFormatter() {
        Assertions.assertAll(() -> {
            // Formatter test
            System.setProperty("java.util.logging.RelpHandler.simpleformatter.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.simpleformatter.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.simpleformatter.appname", "formatterapp");
            System.setProperty("java.util.logging.RelpHandler.simpleformatter.hostname", "formatterhost");
            System.setProperty("java.util.logging.SimpleFormatter.format", "Format from props: [%4$s]: %5$s [%1$tc]%n");

            // Create handlers
            RelpHandler simpleformatter = new RelpHandler("simpleformatter");
            simpleformatter.setFormatter(new SimpleFormatter());

            // Reset default logmanager and add the handlers
            LogManager.getLogManager().reset();
            logger.addHandler(simpleformatter);

            // Set level and send messages
            logger.setLevel(Level.INFO);
            logger.info("Simpleformatter test.");
        });
    }

    //@Test
    @DisplayName("Tests customized formatter")
    public void testCustomFormatter() {
        Assertions.assertAll(() -> {
            // Formatter test
            System.setProperty("java.util.logging.RelpHandler.formattertest.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.formattertest.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.formattertest.appname", "formatterapp");
            System.setProperty("java.util.logging.RelpHandler.formattertest.hostname", "formatterhost");

            // Create handlers
            RelpHandler formattertest = new RelpHandler("formattertest");
            formattertest.setFormatter(new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] [%2$s] %3$s";
                @Override
                public synchronized String format(LogRecord logrecord) {
                    return String.format(format,
                            new Date(logrecord.getMillis()),
                            logrecord.getLevel().getLocalizedName(),
                            logrecord.getMessage()
                    );
                }
            });

            // Reset default logmanager and add the handlers
            LogManager.getLogManager().reset();
            logger.addHandler(formattertest);

            // Set level and send messages
            logger.setLevel(Level.INFO);
            logger.info("Simple programmatically set SimpleFormatter test.");
        });
    }

    //@Test
    @DisplayName("Tests setting formatter programmatically")
    public void testSetFormatterProgrammatic() {
        Assertions.assertAll(() -> {
            // Formatter test
            System.setProperty("java.util.logging.RelpHandler.xmlformatter.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.xmlformatter.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.xmlformatter.appname", "formatterapp");
            System.setProperty("java.util.logging.RelpHandler.xmlformatter.hostname", "formatterhost");

            // Create handlers
            RelpHandler xmlformatter = new RelpHandler("xmlformatter");
            xmlformatter.setFormatter(new XMLFormatter());

            // Reset default logmanager and add the handlers
            LogManager.getLogManager().reset();
            logger.addHandler(xmlformatter);

            // Set level and send messages
            logger.setLevel(Level.INFO);
            logger.info("XML formatter test.");
        });
    }

    //@Test
    @DisplayName("Tests setting xml formatter via properties")
    public void testSetFormatterFromProperties() {
        Assertions.assertAll(() -> {
            // Formatter test
            System.setProperty("java.util.logging.RelpHandler.xmlformatterprops.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.xmlformatterprops.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.xmlformatterprops.appname", "formatterapp");
            System.setProperty("java.util.logging.RelpHandler.xmlformatterprops.hostname", "formatterhost");
            System.setProperty("java.util.logging.RelpHandler.xmlformatterprops.formatter", "java.util.logging.XMLFormatter");

            // Create handlers
            RelpHandler xmlformatterprops = new RelpHandler("xmlformatterprops");

            // Reset default logmanager and add the handlers
            LogManager.getLogManager().reset();
            logger.addHandler(xmlformatterprops);

            // Set level and send messages
            logger.setLevel(Level.INFO);
            logger.info("XML formatter via properties test.");
        });
    }


    //@Test
    @DisplayName("Tests setting formatter format via properties")
    public void testSetFormatterFormatFromProperties() {
        Assertions.assertAll(() -> {
            // Formatter test
            System.setProperty("java.util.logging.RelpHandler.simpleformatterformat.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.simpleformatterformat.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.simpleformatterformat.appname", "formatterapp");
            System.setProperty("java.util.logging.RelpHandler.simpleformatterformat.hostname", "formatterhost");
            System.setProperty("java.util.logging.RelpHandler.simpleformatterformat.formatter", "java.util.logging.SimpleFormatter");
            System.setProperty("java.util.logging.SimpleFormatter.format", "Secrets: [%4$s]: %5$s [%1$tc]%n");

            // Create handlers
            RelpHandler simpleformatterformat = new RelpHandler("simpleformatterformat");

            // Reset default logmanager and add the handlers
            LogManager.getLogManager().reset();
            logger.addHandler(simpleformatterformat);

            // Set level and send messages
            logger.setLevel(Level.INFO);
            logger.info("Simple formatter format via properties test.");
        });
    }
}
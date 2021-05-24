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

import org.junit.jupiter.api.*;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class InputVerificationTest {
    static final Logger logger = Logger.getLogger(RelpHandler.class.getName());

    // So all the loggers are actually used and initialized
    public void testHandler(RelpHandler handler, Boolean failure, String message) {
        // Just secondary failguard against exceptions
        Assertions.assertEquals(false, failure);
        LogManager.getLogManager().reset();
        logger.addHandler(handler);
        logger.info(message);
    }

    //@Test
    @DisplayName("Tests empty values")
    public void testEmptyValues() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Empty address
            System.setProperty("java.util.logging.RelpHandler.emptyaddress.server.address", "");
            System.setProperty("java.util.logging.RelpHandler.emptyaddress.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.emptyaddress.appname", "emptyaddress");
            testHandler(new RelpHandler("emptyaddress"), true, "Empty address");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Empty appname
            System.setProperty("java.util.logging.RelpHandler.emptyappname.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.emptyappname.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.emptyappname.appname", "");
            testHandler(new RelpHandler("emptyappname"), true, "Empty appname");
        });
    }

    //@Test
    @DisplayName("Tests missing properties")
    public void testMissingProperties() {
        Assertions.assertDoesNotThrow(() -> {
            // No address
            System.setProperty("java.util.logging.RelpHandler.missingaddress.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.missingaddress.appname", "missingaddress");
            testHandler(new RelpHandler("missingaddress"), false, "Missing address");
        });

        Assertions.assertDoesNotThrow(() -> {
            // No port
            System.setProperty("java.util.logging.RelpHandler.missingport.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.missingport.appname", "missingport");
            testHandler(new RelpHandler("missingport"), false, "Missing port");
        });

        Assertions.assertDoesNotThrow(() -> {
            // No appname
            System.setProperty("java.util.logging.RelpHandler.missingappname.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.missingappname.server.port", "1666");
            testHandler(new RelpHandler("missingappname"), false, "Missing appname");
        });
    }

    //@Test
    @DisplayName("Tests invalid port ranges values")
    public void testInvalidPortRangeValues() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Port is too small
            System.setProperty("java.util.logging.RelpHandler.smallport.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.smallport.server.port", "-3");
            System.setProperty("java.util.logging.RelpHandler.smallport.appname", "smallport");
            testHandler(new RelpHandler("smallport"), true,"Small port");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Port is too big
            System.setProperty("java.util.logging.RelpHandler.bigport.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.bigport.server.port", "123456");
            System.setProperty("java.util.logging.RelpHandler.bigport.appname", "bigport");
            testHandler(new RelpHandler("bigport"), true,"Big port");
        });
    }

    //@Test
    @DisplayName("Tests non-numeric port value")
    public void testNonNumericPortValue() {
        Assertions.assertThrows(NumberFormatException.class, () -> {
            // Port is not numeric
            System.setProperty("java.util.logging.RelpHandler.invalidport.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.invalidport.server.port", "NotNumeric");
            System.setProperty("java.util.logging.RelpHandler.invalidport.appname", "invalidport");
            testHandler(new RelpHandler("invalidport"), true,"Invalid port");
        });
    }

    //@Test
    @DisplayName("Tests empty logger name")
    public void testEmptyLoggerName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Logger name is empty
            System.setProperty("java.util.logging.RelpHandler.emptylogger.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.emptylogger.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.emptylogger.appname", "emptylogger");
            testHandler(new RelpHandler(""), true,"Empty logger name");
        });
    }

    //@Test
    @DisplayName("Tests invalid reconnect intervals")
    public void testInvalidReconnectInterval() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Interval is set but empty
            System.setProperty("java.util.logging.RelpHandler.intervalempty.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.intervalempty.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.intervalempty.appname", "intervalempty");
            System.setProperty("java.util.logging.RelpHandler.intervalempty.server.reconnectInterval", "");
            testHandler(new RelpHandler("intervalempty"), true,"Empty interval");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Interval is invalid
            System.setProperty("java.util.logging.RelpHandler.intervalinvalid.server.address", "127.0.0.1");
            System.setProperty("java.util.logging.RelpHandler.intervalinvalid.server.port", "1666");
            System.setProperty("java.util.logging.RelpHandler.intervalinvalid.appname", "intervalinvalid");
            System.setProperty("java.util.logging.RelpHandler.intervalinvalid.server.reconnectInterval", "-13");
            testHandler(new RelpHandler("intervalinvalid"), true,"Invalid interval");
        });
    }
}

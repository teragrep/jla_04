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

import java.util.Properties;
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
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.emptyaddress.server.address", "");
            System.setProperty("com.teragrep.jla_04.RelpHandler.emptyaddress.server.port", "1666");
            System.setProperty("com.teragrep.jla_04.RelpHandler.emptyaddress.appname", "emptyaddress");
            testHandler(new RelpHandler("emptyaddress"), true, "Empty address");
            System.getProperties().clear();
            System.setProperties(default_props);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Empty appname
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.emptyappname.server.address", "127.0.0.1");
            System.setProperty("com.teragrep.jla_04.RelpHandler.emptyappname.server.port", "1666");
            System.setProperty("com.teragrep.jla_04.RelpHandler.emptyappname.appname", "");
            testHandler(new RelpHandler("emptyappname"), true, "Empty appname");
            System.getProperties().clear();
            System.setProperties(default_props);
        });
    }

    //@Test
    @DisplayName("Tests missing properties")
    public void testMissingProperties() {
        Assertions.assertDoesNotThrow(() -> {
            // No address
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.missingaddress.server.port", "1666");
            System.setProperty("com.teragrep.jla_04.RelpHandler.missingaddress.appname", "missingaddress");
            testHandler(new RelpHandler("missingaddress"), false, "Missing address");
            System.getProperties().clear();
            System.setProperties(default_props);
        });

        Assertions.assertDoesNotThrow(() -> {
            // No port
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.missingport.server.address", "127.0.0.1");
            System.setProperty("com.teragrep.jla_04.RelpHandler.missingport.appname", "missingport");
            testHandler(new RelpHandler("missingport"), false, "Missing port");
            System.getProperties().clear();
            System.setProperties(default_props);
        });

        Assertions.assertDoesNotThrow(() -> {
            // No appname
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.missingappname.server.address", "127.0.0.1");
            System.setProperty("com.teragrep.jla_04.RelpHandler.missingappname.server.port", "1666");
            testHandler(new RelpHandler("missingappname"), false, "Missing appname");
            System.getProperties().clear();
            System.setProperties(default_props);
        });
    }

    //@Test
    @DisplayName("Tests invalid port ranges values")
    public void testInvalidPortRangeValues() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Port is too small
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.smallport.server.address", "127.0.0.1");
            System.setProperty("com.teragrep.jla_04.RelpHandler.smallport.server.port", "-3");
            System.setProperty("com.teragrep.jla_04.RelpHandler.smallport.appname", "smallport");
            testHandler(new RelpHandler("smallport"), true,"Small port");
            System.getProperties().clear();
            System.setProperties(default_props);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Port is too big
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.bigport.server.address", "127.0.0.1");
            System.setProperty("com.teragrep.jla_04.RelpHandler.bigport.server.port", "123456");
            System.setProperty("com.teragrep.jla_04.RelpHandler.bigport.appname", "bigport");
            testHandler(new RelpHandler("bigport"), true,"Big port");
            System.getProperties().clear();
            System.setProperties(default_props);
        });
    }

    //@Test
    @DisplayName("Tests non-numeric port value")
    public void testNonNumericPortValue() {
        Assertions.assertThrows(NumberFormatException.class, () -> {
            // Port is not numeric
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.invalidport.server.address", "127.0.0.1");
            System.setProperty("com.teragrep.jla_04.RelpHandler.invalidport.server.port", "NotNumeric");
            System.setProperty("com.teragrep.jla_04.RelpHandler.invalidport.appname", "invalidport");
            testHandler(new RelpHandler("invalidport"), true,"Invalid port");
            System.getProperties().clear();
            System.setProperties(default_props);
        });
    }

    //@Test
    @DisplayName("Tests empty logger name")
    public void testEmptyLoggerName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Logger name is empty
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.emptylogger.server.address", "127.0.0.1");
            System.setProperty("com.teragrep.jla_04.RelpHandler.emptylogger.server.port", "1666");
            System.setProperty("com.teragrep.jla_04.RelpHandler.emptylogger.appname", "emptylogger");
            testHandler(new RelpHandler(""), true,"Empty logger name");
            System.getProperties().clear();
            System.setProperties(default_props);
        });
    }

    //@Test
    @DisplayName("Tests invalid reconnect intervals")
    public void testInvalidReconnectInterval() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Interval is set but empty
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.intervalempty.server.address", "127.0.0.1");
            System.setProperty("com.teragrep.jla_04.RelpHandler.intervalempty.server.port", "1666");
            System.setProperty("com.teragrep.jla_04.RelpHandler.intervalempty.appname", "intervalempty");
            System.setProperty("com.teragrep.jla_04.RelpHandler.intervalempty.server.reconnectInterval", "");
            testHandler(new RelpHandler("intervalempty"), true,"Empty interval");
            System.getProperties().clear();
            System.setProperties(default_props);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Interval is invalid
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.intervalinvalid.server.address", "127.0.0.1");
            System.setProperty("com.teragrep.jla_04.RelpHandler.intervalinvalid.server.port", "1666");
            System.setProperty("com.teragrep.jla_04.RelpHandler.intervalinvalid.appname", "intervalinvalid");
            System.setProperty("com.teragrep.jla_04.RelpHandler.intervalinvalid.server.reconnectInterval", "-13");
            testHandler(new RelpHandler("intervalinvalid"), true,"Invalid interval");
            System.getProperties().clear();
            System.setProperties(default_props);
        });
    }

    //@Test
    @DisplayName("Tests invalid formatter value")
    public void testInvalidFormatterValue() {
        Assertions.assertThrows(java.lang.ClassNotFoundException.class, () -> {
            // Formatter is invalid
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.invalidformatter.server.address", "127.0.0.1");
            System.setProperty("com.teragrep.jla_04.RelpHandler.invalidformatter.server.port", "1666");
            System.setProperty("com.teragrep.jla_04.RelpHandler.invalidformatter.appname", "invalidformatter");
            System.setProperty("com.teragrep.jla_04.RelpHandler.invalidformatter.formatter", "java.util.class.doesnt.exist");
            testHandler(new RelpHandler("invalidformatter"), true,"Invalid Formatter");
            System.getProperties().clear();
            System.setProperties(default_props);
        });
    }
}

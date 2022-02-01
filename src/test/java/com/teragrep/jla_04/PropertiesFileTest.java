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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class PropertiesFileTest {
    static final Logger logger = Logger.getLogger(RelpHandler.class.getName());

    //@Test
    @DisplayName("Tests reading properties file")
    public void testReadPropertiesFile() {
        Assertions.assertDoesNotThrow(() -> {
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.config.file", System.getProperty("user.dir") + "/src/test/java/com/teragrep/jla_04/logging.properties");
            RelpHandler handler = new RelpHandler("fromprops");
            LogManager.getLogManager().reset();
            logger.addHandler(handler);
            logger.info("I am from properties file");
            System.getProperties().clear();
            System.setProperties(default_props);
        });

    }

    //@Test
    @DisplayName("Tests reading properties file and overriding values")
    public void testOverridePropertiesFile() {
        Assertions.assertDoesNotThrow(() -> {
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.RelpHandler.fromprops-override.hostname", "fromprops-hostname-overridden");
            RelpHandler handler = new RelpHandler("fromprops-override");
            LogManager.getLogManager().reset();
            logger.addHandler(handler);
            logger.info("I am from properties file but overridden");
            System.getProperties().clear();
            System.setProperties(default_props);
        });
    }

    //@Test
    @DisplayName("Tests reading properties file and getting formatter")
    public void testGettingFormatter() {
        Assertions.assertDoesNotThrow(() -> {
            Properties default_props = (Properties) System.getProperties().clone();
            System.setProperty("com.teragrep.jla_04.config.file", System.getProperty("user.dir") + "/src/test/java/com/teragrep/jla_04/logging.properties");
            RelpHandler handler = new RelpHandler("fromprops-formatter");
            LogManager.getLogManager().reset();
            logger.addHandler(handler);
            logger.info("My formatter should be from logging.properties");
            System.getProperties().clear();
            System.setProperties(default_props);
        });
    }
}

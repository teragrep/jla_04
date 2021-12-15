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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class PrebuiltTest {
    static final Logger logger = Logger.getLogger(RelpHandler.class.getName());
    //@Test
    @DisplayName("Tests all from properties")
    public void testFullyFromProperties() {
        Assertions.assertDoesNotThrow(() -> {
            LogManager.getLogManager().reset();
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(System.getProperty("user.dir") + "/src/test/java/com/teragrep/jla_04/fully.properties");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                LogManager.getLogManager().readConfiguration(inputStream);
            }
            catch (IOException e) {
                System.out.println("Can't read conf/logging.properties: " + e.getMessage());
            }

            logger.info("I should be from fully.properties");
        });
    }
}

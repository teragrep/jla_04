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

import com.teragrep.rlo_14.Facility;
import com.teragrep.rlo_14.SDElement;
import com.teragrep.rlo_14.Severity;
import com.teragrep.rlo_14.SyslogMessage;
import com.teragrep.rlp_01.RelpBatch;
import com.teragrep.rlp_01.RelpConnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.logging.*;

public class RelpHandler extends Handler {
    private RelpConnection relpConnection;
    private RelpBatch batch;
    private RelpConfig config;
    boolean connected = false;

    // No arguments defaults to 'default' logger
    public RelpHandler() throws NumberFormatException, NoSuchFieldException, IOException, TimeoutException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super();
        initialize("default");
    }

    // Otherwise use what user provided
    public RelpHandler(String name) throws NumberFormatException, NoSuchFieldException, IOException, TimeoutException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super();
        initialize(name);
    }

    private void initialize(String name) throws NoSuchFieldException, IOException, TimeoutException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // All relevant onetime-setup configurations and their validations are in RelpConfig
        this.config = new RelpConfig(name);

        // Connect
        this.relpConnection = new RelpConnection();
        this.relpConnection.setConnectionTimeout(this.config.getConnectionTimeout());
        this.relpConnection.setReadTimeout(this.config.getReadTimeout());
        this.relpConnection.setWriteTimeout(this.config.getWriteTimeout());
        connect();
    }

    @Override
    public synchronized void publish(LogRecord logRecord) {
        // Discards logs with lower than configured logging level
        if (!isLoggable(logRecord)) {
            return;
        }

        // Craft syslog message
        SyslogMessage syslog = new SyslogMessage()
                .withTimestamp(new Date().getTime())
                .withSeverity(Severity.WARNING)
                .withAppName(this.config.getAppname())
                .withHostname(this.config.getHostname())
                .withFacility(Facility.USER)
                .withMsg(this.config.getFormatter().format(logRecord));

        // Add SD if enabled
        if(this.config.getUseSD()) {
            SDElement event_id_48577 = new SDElement("event_id@48577")
                    .addSDParam("hostname", this.config.getHostname())
                    .addSDParam("uuid", UUID.randomUUID().toString())
                    .addSDParam("source", "source")
                    .addSDParam("unixtime", Long.toString(System.currentTimeMillis()));
            SDElement origin_48577 = new SDElement("origin@48577")
                    .addSDParam("hostname", this.config.getRealHostName());
            syslog = syslog
                    .withSDElement(event_id_48577)
                    .withSDElement(origin_48577);
        }

        // Initialize the batch for every set of messages
        if(this.batch == null) {
            this.batch = new RelpBatch();
        }

        // Add to batch and send
        this.batch.insert(syslog.toRfc5424SyslogMessage().getBytes(StandardCharsets.UTF_8));
        flush();
    }


    @Override
    public synchronized void flush() {
        // Loops until all messages have been sent
        boolean allSent = false;
        while (!allSent) {
            try {
                this.relpConnection.commit(this.batch);
            } catch (IllegalStateException | IOException | java.util.concurrent.TimeoutException e) {
                System.out.println("RelpLogger.flush.commit> exception:");
                e.printStackTrace();
                this.relpConnection.tearDown();
                this.connected = false;
            }
            // Check if everything has been sent, retry and reconnect if not.
            if (!this.batch.verifyTransactionAll()) {
                this.batch.retryAllFailed();
                try {
                    reconnect();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            } else {
                allSent = true;
            }
        }
        // Clean the batch
        this.batch = null;
    }

    private void reconnect() throws IOException, TimeoutException {
        disconnect();
        connect();
    }

    private void disconnect() throws IOException, TimeoutException {
        if (!this.connected) {
            return;
        }
        this.relpConnection.disconnect();
        this.relpConnection.tearDown();
        this.connected = false;
    }

    private void connect() {
        while (!this.connected) {
            try {
                this.connected = relpConnection.connect(this.config.getAddress(), this.config.getPort());
            } catch (Exception e) {
                System.out.println("RelpHandler.connect> exception:");
                e.printStackTrace();
            }
            if (!this.connected) {
                try {
                    Thread.sleep(this.config.getReconnectInterval());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public synchronized void close() {
        if (!this.connected) {
            return;
        }
        try {
            disconnect();
        } catch (IOException  | TimeoutException e) {
            e.printStackTrace();
        }
    }
}

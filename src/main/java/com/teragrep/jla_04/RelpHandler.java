package com.teragrep.jla_04;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.SDElement;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.SyslogMessage;
import com.teragrep.rlp_01.RelpBatch;
import com.teragrep.rlp_01.RelpConnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class RelpHandler extends Handler {
    private RelpConnection relpConnection;
    private final RelpBatch batch = new RelpBatch();
    private RelpConfig config;

    // No arguments defaults to 'default' logger
    public RelpHandler() throws NumberFormatException, NoSuchFieldException, IOException, TimeoutException {
        super();
        initialize("default");
    }

    // Otherwise use what user provided
    public RelpHandler(String name) throws NumberFormatException, NoSuchFieldException, IOException, TimeoutException {
        super();
        initialize(name);
    }

    private void initialize(String name) throws NoSuchFieldException, IOException, TimeoutException {
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
                .withMsg(logRecord.getMessage());

        // Add SD if enabled
        if(this.config.getUseSD()) {
            SDElement event_id_48577 = new SDElement("event_id@48577")
                    .addSDParam("hostname", this.config.getHostname())
                    .addSDParam("uuid", UUID.randomUUID().toString())
                    .addSDParam("source", "source")
                    .addSDParam("unixtime", Long.toString(System.currentTimeMillis()));
            SDElement origin_48577 = new SDElement("origin@48577")
                    .addSDParam("hostname", this.config.getHostname());
            syslog = syslog
                    .withSDElement(event_id_48577)
                    .withSDElement(origin_48577);
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
    }

    private void reconnect() throws IOException, TimeoutException {
        disconnect();
        connect();
    }

    private void disconnect() throws IOException, TimeoutException {
        this.relpConnection.disconnect();
        this.relpConnection.tearDown();
    }

    private void connect() {
        boolean connected = false;
        while (!connected) {
            try {
                connected = relpConnection.connect(this.config.getAddress(), this.config.getPort());
            } catch (Exception e) {
                System.out.println("RelpHandler.connect> exception:");
                e.printStackTrace();
            }
            if (!connected) {
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
        // Flush the batches before disconnecting
        flush();
        try {
            disconnect();
        } catch (IOException  | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
package com.teragrep.jla_04;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RelpConfig {
    private int port; // Relp server port
    private String address; // Relp server address
    private String appname; // appname for syslog message
    private String hostname; // hostname for syslog message
    private String name; // logger name
    private Boolean useSD; // if structured data should be used
    private int connectionTimeout; // Relp connection timeout
    private int reconnectInterval; // sleep between relp connection reconnects
    private int readTimeout; // relp connection reading timeout
    private int writeTimeout; // relp connection writing timeout

    public RelpConfig(String name) throws NumberFormatException, IllegalArgumentException, NoSuchFieldException {
        // Name must be set first, others do not depend on the ordering
        initName(name);
        // The rest
        initPort();
        initAddress();
        initAppname();
        initHostName();
        initReconnectInterval();
        initUseSD();
        initConnectionTimeout();
        initReadTimeout();
        initWriteTimeout();
    }

    private String getProperty(String name, String fallback) {
        String prop = System.getProperty("java.util.logging.RelpHandler." + this.getName() + "." + name);
        if (prop == null) {
            return fallback;
        }
        if (prop.equals("")) {
            throw new IllegalArgumentException("Field is set but has no value: java.util.logging.RelpHandler." + this.getName() + "." + name);
        }
        return prop;
    }

    private void initName(String name) throws NoSuchFieldException, IllegalArgumentException {
        if (name == null) {
            throw new NoSuchFieldException("Logger name is null");
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("Logger name is empty");
        }
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    private void initPort() throws NumberFormatException {
        String prop = getProperty("server.port", "601");
        try {
            this.port = Integer.parseInt(prop);
            if (this.port <= 0 || this.port > 65535) {
                throw new IllegalArgumentException("RELP server port is invalid: " + this.port + ", expected to be in range from 1 to 65535");
            }
        }
        catch (NumberFormatException e) {
            throw new NumberFormatException("RELP server port is not a number: " + e);
        }
    }

    public int getPort() {
        return this.port;
    }

    private void initAddress() {
        this.address = getProperty("server.address", "127.0.0.1");
    }

    public String getAddress() {
        return this.address;
    }

    private void initAppname() {
        this.appname = getProperty("appname", "RelpHandler");
    }

    public String getAppname() {
        return this.appname;
    }

    private void initHostName() {
        try {
            this.hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            this.hostname = "localhost";
        }
    }

    public String getHostname() {
        return this.hostname;
    }

    private void initReconnectInterval() throws NumberFormatException {
        String prop = getProperty("server.reconnectInterval", "1000");
        try {
            this.reconnectInterval = Integer.parseInt(prop);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("RELP server reconnect interval is not a number: " + e);
        }
        if (this.reconnectInterval < 0) {
            throw new IllegalArgumentException("RELP server reconnect interval is invalid: " + this.reconnectInterval + ", expected to be >= 0");
        }
    }

    public int getReconnectInterval() {
        return this.reconnectInterval;
    }

    private void initUseSD() {
        // Any truthy value is yes.
        this.useSD = Boolean.valueOf(getProperty("useStructuredData", "true"));
    }

    public boolean getUseSD() {
        return this.useSD;
    }

    private void initConnectionTimeout() throws IllegalArgumentException, NumberFormatException {
        String prop = getProperty("server.connectionTimeout", "0");
        try {
            this.connectionTimeout = Integer.parseInt(prop);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("RELP server connection timeout is not a number: " + e);
        }
        if (this.connectionTimeout < 0) {
            throw new IllegalArgumentException("RELP server connection timeout is invalid: " + this.connectionTimeout + ", expected to be >= 0");
        }
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    private void initWriteTimeout() throws NumberFormatException, IllegalArgumentException {
        String prop = getProperty("server.writeTimeout", "0");
        try {
            this.writeTimeout = Integer.parseInt(prop);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("RELP server write timeout is not a number: " + e);
        }
        if (this.writeTimeout < 0) {
            throw new IllegalArgumentException("RELP server write timeout is invalid: " + this.writeTimeout + ", expected to be >= 0");
        }
    }

    public int getWriteTimeout() {
        return this.writeTimeout;
    }

    private void initReadTimeout() throws NumberFormatException, IllegalArgumentException {
        String prop = getProperty("server.readTimeout", "0");
        try {
            this.readTimeout = Integer.parseInt(prop);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("RELP server read timeout is not a number: " + e);
        }
        if (this.readTimeout < 0) {
            throw new IllegalArgumentException("RELP server read timeout is invalid: " + this.readTimeout + ", expected to be >= 0");
        }
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }
}

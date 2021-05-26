# Java Util Logging RELP Handler
[![Build Status](https://scan.coverity.com/projects/23156/badge.svg)](https://scan.coverity.com/projects/jla_04)

## Supported properties

- java.util.logging.RelpHandler.LOGGERNAME.appname
- java.util.logging.RelpHandler.LOGGERNAME.server.address
- java.util.logging.RelpHandler.LOGGERNAME.server.connectionTimeout
- java.util.logging.RelpHandler.LOGGERNAME.server.port
- java.util.logging.RelpHandler.LOGGERNAME.server.readTimeout
- java.util.logging.RelpHandler.LOGGERNAME.server.reconnectInterval
- java.util.logging.RelpHandler.LOGGERNAME.server.writeTimeout
- java.util.logging.RelpHandler.LOGGERNAME.useStructuredData

## Usage

```
// RelpHandler without arguments defaults LOGGERNAME to "default"
System.setProperty("java.util.logging.RelpHandler.default.server.address", "127.0.0.1");
System.setProperty("java.util.logging.RelpHandler.default.server.port", "1667");
System.setProperty("java.util.logging.RelpHandler.default.appname", "Default");
RelpHandler relpHandler_default = new RelpHandler();

// Other logger
System.setProperty("java.util.logging.RelpHandler.custom.server.address", "127.0.0.1");
System.setProperty("java.util.logging.RelpHandler.custom.server.port", "1668");
System.setProperty("java.util.logging.RelpHandler.custom.appname", "CustomLogger");
RelpHandler relpHandler_custom = new RelpHandler("custom");

// Reset logger and add handlers to it
LogManager.getLogManager().reset();
logger.addHandler(relpHandler_default);
logger.addHandler(relpHandler_custom);

// Set level and send messages
logger.setLevel(Level.WARNING);
logger.severe("Severe message");
```

## Maven dependency definition

```
<dependency>
    <groupId>com.teragrep</groupId>
    <artifactId>jla_04</artifactId>
    <version>%VERSION%</version>
</dependency>
```
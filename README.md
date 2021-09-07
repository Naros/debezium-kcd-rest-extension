[![License](http://img.shields.io/:license-apache%202.0-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.com/debezium/debezium-kcd-rest-extension.svg?branch=master)](https://travis-ci.com/debezium/debezium-kcd-rest-extension/)
[![User chat](https://img.shields.io/badge/chat-users-brightgreen.svg)](https://gitter.im/debezium/user)
[![Developer chat](https://img.shields.io/badge/chat-devs-brightgreen.svg)](https://gitter.im/debezium/dev)
[![Google Group](https://img.shields.io/:mailing%20list-debezium-brightgreen.svg)](https://groups.google.com/forum/#!forum/debezium)
[![Stack Overflow](http://img.shields.io/:stack%20overflow-debezium-brightgreen.svg)](http://stackoverflow.com/questions/tagged/debezium)

Copyright Debezium Authors.
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

# Debezium Kafka Connect Distributed (KCD) REST Extension

Debezium is an open source distributed platform for change data capture (CDC).

This repository contains extensions to Kafka Connect's REST API.

## Setup

1. Install or mount the Debezium Kafka Connect REST Extension jar into a separate Kafka Connect plugin directory.

    For example with `docker-compose`:

```yaml
    volumes:
     - debezium-kcd-rest-extension-1.0.0.jar:/kafka/connect/dbz-rest-extension/debezium-kcd-rest-extension-1.0.0.jar
```

2. Register the REST extension with Kafka Connect:

```yaml
    environment:
     - CONNECT_REST_EXTENSION_CLASSES=io.debezium.kcdrestextension.DebeziumConnectRestExtensions
```

or set `rest.extension.classes=io.debezium.kcdrestextension.DebeziumConnectRestExtensions` in your Kafka Connect properties file.

## Contribution

This project is under active development, any contributions are very welcome.
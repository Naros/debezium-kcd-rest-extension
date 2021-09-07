/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.kcdrestextension;

import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.rest.ConnectRestExtension;
import org.apache.kafka.connect.rest.ConnectRestExtensionContext;

import java.util.Map;

public class DebeziumConnectRestExtensions implements ConnectRestExtension {

    private Map<String, ?> config;

    @Override
    public void register(ConnectRestExtensionContext restPluginContext) {
        restPluginContext.configurable().register(new DebeziumResource(restPluginContext.clusterState()));
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
        this.config = configs;
    }

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }
}

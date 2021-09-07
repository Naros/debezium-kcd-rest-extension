/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.kcdrestextension;

import io.debezium.kcdrestextension.entities.TransformsInfo;
import org.apache.kafka.connect.health.ConnectClusterState;
import org.apache.kafka.connect.runtime.Herder;
import org.apache.kafka.connect.runtime.health.ConnectClusterStateImpl;
import org.apache.kafka.connect.runtime.isolation.PluginDesc;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.predicates.HasHeaderKey;
import org.apache.kafka.connect.transforms.predicates.RecordIsTombstone;
import org.apache.kafka.connect.transforms.predicates.TopicNameMatches;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Path("/debezium")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DebeziumResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebeziumResource.class);

    // TODO: This should not be so long. However, due to potentially long rebalances that may have to wait a full
    // session timeout to complete, during which we cannot serve some requests. Ideally we could reduce this, but
    // we need to consider all possible scenarios this could fail. It might be ok to fail with a timeout in rare cases,
    // but currently a worker simply leaving the group can take this long as well.
    public static final long REQUEST_TIMEOUT_MS = 90 * 1000;
    // Mutable for integration testing; otherwise, some tests would take at least REQUEST_TIMEOUT_MS
    // to run
    private static long requestTimeoutMs = REQUEST_TIMEOUT_MS;

    private final List<TransformsInfo> transforms;
    private final Herder herder;

    @javax.ws.rs.core.Context
    private ServletContext context;

    public DebeziumResource(ConnectClusterState clusterState) {
        Field herderField;
        try {
            herderField = ConnectClusterStateImpl.class.getDeclaredField("herder");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        herderField.setAccessible(true);
        try {
            this.herder = (Herder) herderField.get(clusterState);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        this.transforms = new ArrayList();
    }

    // For testing purposes only
    public static void setRequestTimeout(long requestTimeoutMs) {
        DebeziumResource.requestTimeoutMs = requestTimeoutMs;
    }

    public static void resetRequestTimeout() {
        DebeziumResource.requestTimeoutMs = REQUEST_TIMEOUT_MS;
    }

    @GET
    @Path("/transforms")
    public List<TransformsInfo> listTransforms() {
        return this.getTransforms();
    }

    private synchronized List<TransformsInfo> getTransforms() {
        if (this.transforms.isEmpty()) {
            Iterator var1 = this.herder.plugins().transformations().iterator();

            while(var1.hasNext()) {
                PluginDesc<Transformation> plugin = (PluginDesc)var1.next();

                if("org.apache.kafka.connect.runtime.PredicatedTransformation".equals(plugin.className())) {
                    this.transforms.add(new TransformsInfo(HasHeaderKey.class.getName(), (new HasHeaderKey<>().config())));
                    this.transforms.add(new TransformsInfo(RecordIsTombstone.class.getName(), (new RecordIsTombstone<>().config())));
                    this.transforms.add(new TransformsInfo(TopicNameMatches.class.getName(), (new TopicNameMatches<>().config())));
                } else {
                    this.transforms.add(new TransformsInfo(plugin));
                }
            }
        }

        return Collections.unmodifiableList(this.transforms);
    }
}

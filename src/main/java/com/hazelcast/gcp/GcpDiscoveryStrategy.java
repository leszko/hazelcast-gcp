/*
 * Copyright (c) 2008-2018, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.gcp;

import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.hazelcast.gcp.GcpProperties.PORT;

/**
 * GCP implementation of {@link DiscoveryStrategy}.
 */
public class GcpDiscoveryStrategy
        extends AbstractDiscoveryStrategy {
    private static final ILogger LOGGER = Logger.getLogger(GcpDiscoveryStrategy.class);

    private final GcpClient gcpClient;
    private final PortRange portRange;

    public GcpDiscoveryStrategy(Map<String, Comparable> properties) {
        super(LOGGER, properties);
        try {
            this.gcpClient = new GcpClient(properties);
            this.portRange = createPortRange();
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Invalid GCP Discovery Strategy configuration", e);
        }
    }

    /**
     * For test purposes only.
     */
    GcpDiscoveryStrategy(Map<String, Comparable> properties, GcpClient gcpClient) {
        super(LOGGER, properties);
        this.gcpClient = gcpClient;
        this.portRange = createPortRange();
    }

    private PortRange createPortRange() {
        return new PortRange((String) getOrDefault(PORT.getDefinition(), PORT.getDefaultValue()));
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        try {
            List<GcpAddress> gcpAddresses = gcpClient.getAddresses();
            logGcpAddresses(gcpAddresses);

            List<DiscoveryNode> result = new ArrayList<DiscoveryNode>();
            for (GcpAddress gcpAddress : gcpAddresses) {
                for (int port = portRange.getFromPort(); port <= portRange.getToPort(); port++) {
                    result.add(createDiscoveryNode(gcpAddress, port));
                }
            }

            return result;
        } catch (Exception e) {
            LOGGER.warning("Cannot discover nodes, returning empty list", e);
            return Collections.emptyList();
        }
    }

    private static DiscoveryNode createDiscoveryNode(GcpAddress gcpAddress, int port)
            throws UnknownHostException {
        Address privateAddress = new Address(gcpAddress.getPrivateAddress(), port);
        Address publicAddress = new Address(gcpAddress.getPublicAddress(), port);
        return new SimpleDiscoveryNode(privateAddress, publicAddress);
    }

    private static void logGcpAddresses(List<GcpAddress> gcpAddresses) {
        if (LOGGER.isFinestEnabled()) {
            StringBuilder stringBuilder = new StringBuilder("Found the following GCP instance: ");
            for (GcpAddress gcpAddress : gcpAddresses) {
                stringBuilder.append(String.format("%s, ", gcpAddress));
            }
            LOGGER.finest(stringBuilder.toString());
        }
    }
}

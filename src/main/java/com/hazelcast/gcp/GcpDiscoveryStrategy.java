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
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;

import java.util.Map;

/**
 * GCP implementation of {@link DiscoveryStrategy}.
 */
public class GcpDiscoveryStrategy
        extends AbstractDiscoveryStrategy {
    private static final ILogger LOGGER = Logger.getLogger(GcpDiscoveryStrategy.class);

    private final GcpClient gcpClient;

    public GcpDiscoveryStrategy(Map<String, Comparable> properties) {
        super(LOGGER, properties);
        try {
            this.gcpClient = new GcpClient(properties);
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
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        return null;
    }
}

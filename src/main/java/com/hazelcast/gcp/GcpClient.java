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

import java.util.List;
import java.util.concurrent.Callable;

import static java.util.Arrays.asList;

class GcpClient {
    private static final int RETRIES = 10;

    private final GcpMetadataApi gcpMetadataApi;
    private final GcpComputeApi gcpComputeApi;

    private final List<String> projects;
    private final List<String> zones;
    private final String label;

    GcpClient(GcpMetadataApi gcpMetadataApi, GcpComputeApi gcpComputeApi, GcpConfig gcpConfig) {
        this.gcpMetadataApi = gcpMetadataApi;
        this.gcpComputeApi = gcpComputeApi;

        projects = projectFromConfigOrMetadataApi(gcpConfig);
        zones = null;
        label = gcpConfig.getLabel();
    }

    private List<String> projectFromConfigOrMetadataApi(final GcpConfig gcpConfig) {
        if (!gcpConfig.getProjects().isEmpty()) {
            return gcpConfig.getProjects();
        }
        return asList(RetryUtils.retry(new Callable<String>() {
            @Override
            public String call()
                    throws Exception {
                return gcpMetadataApi.currentProject();
            }
        }, RETRIES));
    }

    private List<String> zonesFromConfigOrMetadataApi(final GcpConfig gcpConfig) {
        if (!gcpConfig.getZones().isEmpty()) {
            return gcpConfig.getProjects();
        }
        return asList(RetryUtils.retry(new Callable<String>() {
            @Override
            public String call()
                    throws Exception {
                return gcpMetadataApi.currentZone();
            }
        }, RETRIES));
    }

    List<GcpAddress> getAddresses() {
        return null;
    }
}

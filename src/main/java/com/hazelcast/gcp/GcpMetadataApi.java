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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Responsible for connecting to the Google Cloud Instance Metadata API.
 *
 * @see <a href="https://cloud.google.com/appengine/docs/standard/java/accessing-instance-metadata">GCP Instance Metatadata</a>
 */
class GcpMetadataApi {
    private static final String METADATA_ENDPOINT = "http://metadata.google.internal";

    private static final int HTTP_OK = 200;

    private final String endpoint;

    GcpMetadataApi() {
        this.endpoint = METADATA_ENDPOINT;
    }

    /**
     * For test purposes only.
     */
    GcpMetadataApi(String endpoint) {
        this.endpoint = endpoint;
    }

    String currentProject() {
        String urlString = String.format("%s/computeMetadata/v1/project/project-id", endpoint);
        return callGet(urlString);
    }

    String currentZone() {
        String urlString = String.format("%s/computeMetadata/v1/instance/zone", endpoint);
        String zoneResponse = callGet(urlString);
        return lastPartOf(zoneResponse);
    }

    private static String lastPartOf(String string) {
        String[] parts = string.split("/");
        return parts[parts.length - 1];
    }

    private static String callGet(String urlString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Metadata-Flavor", "Google");

            if (connection.getResponseCode() != HTTP_OK) {
                throw new GcpApiException(String.format("Failure executing: GET at: %s. Message: %s,", urlString,
                        read(connection.getErrorStream())));
            }
            return read(connection.getInputStream());
        } catch (Exception e) {
            throw new GcpApiException("Failure in while using Google Cloud API", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String read(InputStream stream) {
        Scanner scanner = new Scanner(stream, "UTF-8");
        scanner.useDelimiter("\\Z");
        return scanner.next();
    }
}

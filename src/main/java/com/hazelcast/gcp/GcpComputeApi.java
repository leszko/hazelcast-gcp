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

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

class GcpComputeApi {
    private static final String GOOGLE_API_ENDPOINT = "https://www.googleapis.com";

    private final String endpoint;

    GcpComputeApi() {
        this.endpoint = GOOGLE_API_ENDPOINT;
    }

    /**
     * For test purposes only.
     */
    GcpComputeApi(String endpoint) {
        this.endpoint = endpoint;
    }

    List<GcpAddress> instances(String project, String zone, String label, String accessToken) {
        String response = GcpRestClient
                .create(urlFor(project, zone, label))
                .withHeader("Authorization", String.format("OAuth %s", accessToken))
                .get();

        List<GcpAddress> result = new ArrayList<GcpAddress>();
        for (JsonValue item : toJsonArray(Json.parse(response).asObject().get("items"))) {
            String privateAddress = null;
            String publicAddress = null;
            for (JsonValue networkInterface : toJsonArray(item.asObject().get("networkInterfaces"))) {
                privateAddress = networkInterface.asObject().getString("networkIP", null);
                for (JsonValue accessConfig : toJsonArray(networkInterface.asObject().get("accessConfigs"))) {
                    publicAddress = accessConfig.asObject().getString("natIP", null);
                }
            }
            if (privateAddress != null && publicAddress != null) {
                result.add(new GcpAddress(privateAddress, publicAddress));
            }
        }

        return result;
    }

    private String urlFor(String project, String zone, String label) {
        String url = String.format("%s/compute/v1/projects/%s/zones/%s/instances", endpoint, project, zone);
        if (label != null) {
            String[] labelParts = label.split("=");
            String labelKey = labelParts[0];
            String labelValue = labelParts[1];
            url = String.format("%s?filter=labels.%s+eq+%s", url, labelKey, labelValue);
        }
        return url;
    }

    private static JsonArray toJsonArray(JsonValue jsonValue) {
        if (jsonValue == null || jsonValue.isNull()) {
            return new JsonArray();
        } else {
            return jsonValue.asArray();
        }
    }
}

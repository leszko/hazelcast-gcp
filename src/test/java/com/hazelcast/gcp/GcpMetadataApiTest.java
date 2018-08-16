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

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;

public class GcpMetadataApiTest {
    private static final int PORT = 8089;
    private static final String PROJECT = "project1";
    private static final String ZONE = "us-east1-b";

    private final GcpMetadataApi gcpMetadataApi = new GcpMetadataApi(String.format("http://localhost:%s", PORT));

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    @Test
    public void currentProject() {
        // given
        stubFor(get(urlEqualTo("/computeMetadata/v1/project/project-id"))
                .withHeader("Metadata-Flavor", equalTo("Google"))
                .willReturn(aResponse().withStatus(200).withBody(PROJECT)));

        // when
        String result = gcpMetadataApi.currentProject();

        // then
        assertEquals(PROJECT, result);
    }

    @Test(expected = GcpApiException.class)
    public void currentProjectFailure() {
        // given
        stubFor(get(urlEqualTo("/computeMetadata/v1/project/project-id"))
                .withHeader("Metadata-Flavor", equalTo("Google"))
                .willReturn(aResponse().withStatus(500).withBody("Internal error")));

        // when
        gcpMetadataApi.currentProject();

        // then
        // throw exception
    }

    @Test
    public void currentZone() {
        // given
        stubFor(get(urlEqualTo("/computeMetadata/v1/instance/zone"))
                .withHeader("Metadata-Flavor", equalTo("Google"))
                .willReturn(aResponse().withStatus(200).withBody(zoneResponse(ZONE))));

        // when
        String result = gcpMetadataApi.currentZone();

        // then
        assertEquals(ZONE, result);
    }

    @Test(expected = GcpApiException.class)
    public void currentZoneFailure() {
        // given
        stubFor(get(urlEqualTo("/computeMetadata/v1/instance/zone"))
                .withHeader("Metadata-Flavor", equalTo("Google"))
                .willReturn(aResponse().withStatus(500).withBody("Internal error")));

        // when
        gcpMetadataApi.currentZone();

        // then
        // throw exception
    }

    private static String zoneResponse(String zone) {
        String sampleProjectId = "183928891381";
        return String.format("projects/%s/zones/%s", sampleProjectId, zone);
    }
}
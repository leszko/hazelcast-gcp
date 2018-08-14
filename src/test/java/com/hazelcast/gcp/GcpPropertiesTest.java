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

import org.junit.Test;

import static org.junit.Assert.*;

public class GcpPropertiesTest {
    @Test
    public void hzPort() {
        // given
        String validProject = "project";

        // when
        String projects = (String) GcpProperties.PROJECTS.getDefinition().typeConverter().convert(validProject);
        GcpProperties.PROJECTS.getDefinition().validator().validate(projects);

        // then
        // no exception
    }

    @Test
    public void projectsMultiple() {
        // given
        String validProject = "project1,project2,project3";

        // when
        String projects = (String) GcpProperties.PROJECTS.getDefinition().typeConverter().convert(validProject);
        GcpProperties.PROJECTS.getDefinition().validator().validate(projects);

        // then
        // no exception
    }

    @Test
    public void projectsInvalid() {
        // given
        String validProject = "project1,project2,project3";

        // when
        String projects = (String) GcpProperties.PROJECTS.getDefinition().typeConverter().convert(validProject);
        GcpProperties.PROJECTS.getDefinition().validator().validate(projects);

        // then
        // no exception
    }

}
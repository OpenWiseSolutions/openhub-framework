/*
 * Copyright 2002-2021 the original author or authors.
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

package org.openhubframework.openhub;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhubframework.openhub.common.Profiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Simple test to check spring context is configured properly.
 *
 * @author Karel Kovarik
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = OpenHubApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({Profiles.H2})
public class OpenHubApplicationTest {

    @Test
    public void context_loads() {
        // nothing in here, fails if spring context does not start at all.
    }
}

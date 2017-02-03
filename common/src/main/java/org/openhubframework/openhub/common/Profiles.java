/*
 * Copyright 2002-2016 the original author or authors.
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

package org.openhubframework.openhub.common;

/**
 * Defines Spring profile constants.
 *
 * @author Petr Juza
 * @since 2.0
 */
public final class Profiles {

    /**
     * Spring profile for development.
     */
    public static final String DEV = "dev";

    /**
     * Spring profile for unit testing.
     */
    public static final String TEST = "test";

    /**
     * Spring profile for production.
     */
    public static final String PROD = "prod";

    /**
     * Spring profile for running OpenHub in the cluster.
     */
    public static final String CLUSTER = "cluster";

    /**
     * Spring profile for running H2 DB.
     */
    public static final String H2 = "h2";

    private Profiles() {
    }
}

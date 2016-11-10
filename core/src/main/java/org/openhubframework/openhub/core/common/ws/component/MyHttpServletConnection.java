/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.core.common.ws.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ws.transport.http.HttpServletConnection;


/**
 * My implementation of {@link HttpServletConnection} because there is protected constructor by default.
 *
 * @author Petr Juza
 */
public class MyHttpServletConnection extends HttpServletConnection {

    protected MyHttpServletConnection(HttpServletRequest req, HttpServletResponse res) {
        super(req, res);
    }
}

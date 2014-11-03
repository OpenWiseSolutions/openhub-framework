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

package org.cleverbus.core.common.ws.transport.http.ntlm;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.protocol.HttpContext;


public class NTLMSchemeFactory implements AuthSchemeProvider {

    @Override
    public AuthScheme create(HttpContext httpContext) {
        return new NTLMScheme(new JCIFSEngine());
    }
}
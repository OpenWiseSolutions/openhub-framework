#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package}.admin.services;

import javax.annotation.Resource;
import java.util.Map;

import org.springframework.stereotype.Service;

import org.cleverbus.api.exception.ErrorExtEnum;

/**
 * Implementation of {@link ErrorCatalogService}.
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 */
@Service
public class ErrorCatalogServiceImpl implements ErrorCatalogService {

    @Resource
    private Map<String,ErrorExtEnum[]> errorCodesCatalog;

    @Override
    public Map<String, ErrorExtEnum[]> getErrorCatalog() {
        return errorCodesCatalog;
    }
}

/*
 *  Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.admin.web.config;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.template.ObjectWrapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;


/**
 * By default {@link TaglibFactory} is initialized without {@link ObjectWrapper} be configured.
 * <p>
 * <strong>Hotfix for: </strong>{@code Custom EL functions won't be loaded because no ObjectWarpper was specified.}    
 * </p>
 * @author Tomas Hanus
 * @since 2.0
 * @see <a href="http://stackoverflow.com/a/35693413/2596932">Q: 33891693</a>
 */
@Configuration
public class CustomFreemarkerConfig implements InitializingBean {
    
    @Autowired
    private FreeMarkerConfigurer configurer;

    @Override
    public void afterPropertiesSet() throws Exception {
        final TaglibFactory taglibFactory = configurer.getTaglibFactory();

        taglibFactory.setObjectWrapper(
                freemarker.template.Configuration.getDefaultObjectWrapper(
                        freemarker.template.Configuration.getVersion()));
    }
}

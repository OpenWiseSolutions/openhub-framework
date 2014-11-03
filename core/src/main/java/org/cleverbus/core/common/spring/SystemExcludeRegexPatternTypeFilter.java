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

package org.cleverbus.core.common.spring;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;


/**
 * Custom {@link TypeFilter} for using with {@code context:component-scan: exclude-filter} Spring element.
 * This filter checks system and environment property "{@value #PATTERN_PROP_NAME}" and if defined then property value
 * is used for {@link Pattern} compilation.
 * System property has higher priority.
 * <p/>
 * Example:
 * {@code &lt;context:exclude-filter type="custom" expression="org.cleverbus.core.common.spring.SystemExcludeRegexPatternTypeFilter"/>}
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see SystemIncludeRegexPatternTypeFilter
 */
public class SystemExcludeRegexPatternTypeFilter implements TypeFilter {

    public static final String PATTERN_PROP_NAME = "springExcludePattern";

    @Nullable
    private Pattern pattern;

    public SystemExcludeRegexPatternTypeFilter() {
        String regex = System.getenv(PATTERN_PROP_NAME);

        if (System.getProperty(PATTERN_PROP_NAME) != null) {
            regex = System.getProperty(PATTERN_PROP_NAME);
        }

        // compile pattern
        if (regex != null) {
            this.pattern = Pattern.compile(regex);
        }
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return pattern != null && pattern.matcher(metadataReader.getClassMetadata().getClassName()).matches();
    }
}

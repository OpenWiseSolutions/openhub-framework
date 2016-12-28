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

package org.openhubframework.openhub.admin.services.log;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Lop file filter based on regular expression pattern.
 *
 * @author Tomas Hanus
 * @since 0.4
 */
@Component
public class LogFileFilter implements IOFileFilter, InitializingBean, FileFilter {

    @Value("${log.file.pattern}")
    private String formatPattern;

    private Pattern pattern;

    @Override
    public boolean accept(File file) {
        Matcher matcher = pattern.matcher(file.getName());
        return matcher.matches();
    }

    @Override
    public boolean accept(File file, String s) {
        Matcher matcher = pattern.matcher(file.getName());
        return matcher.matches();
    }

    public String getFormatPattern() {
        return formatPattern;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.pattern = Pattern.compile(getFormatPattern());
    }
}

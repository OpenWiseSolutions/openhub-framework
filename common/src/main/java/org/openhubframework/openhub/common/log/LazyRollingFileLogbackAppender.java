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

package org.openhubframework.openhub.common.log;

import java.io.File;
import java.io.IOException;

import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.util.FileUtil;


/**
 * Logback {@link RollingFileAppender} implementation that supports lazy initialization of appender. It is useful
 * for example when appender should be used only when
 * <a href="https://logback.qos.ch/manual/configuration.html#conditional">condition is satisfied</a>. This feature is
 * <a href="https://jira.qos.ch/browse/LOGBACK-202">not supported by default</a>.
 *
 * @author Tomas Hanus
 * @see LazyFileOutputStream
 * @since 2.0
 */
public class LazyRollingFileLogbackAppender<E> extends RollingFileAppender<E> {

    @Override
    public void openFile(String file_name) throws IOException {
        lock.lock();
        try {
            File file = new File(file_name);
            boolean result = FileUtil.createMissingParentDirectories(file);
            if (!result) {
                addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
            }

            LazyFileOutputStream lazyFos = new LazyFileOutputStream(file, append);
            setOutputStream(lazyFos);
        } finally {
            lock.unlock();
        }
    }

}

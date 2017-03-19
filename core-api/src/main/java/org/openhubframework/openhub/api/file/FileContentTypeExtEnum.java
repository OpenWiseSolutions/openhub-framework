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

package org.openhubframework.openhub.api.file;

/**
 * Contract for file content type enums which should be extensible.
 * <p>
 * Use {@link #getContentType()}} for equal comparison.
 *
 * @author Petr Juza
 */
public interface FileContentTypeExtEnum {

    /**
     * Gets name of this enum.
     *
     * @return entity type enum name
     */
    String getContentType();

    /**
     * Gets file prefix.
     *
     * @return file prefix
     */
    String getFilePrefix();

}

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

package org.cleverbus.core.common.version;

/**
 * Interface for classes that can be used as sources of application version data.
 *
 * @author <a href="mailto:michal.palicka@cleverlance.com">Michal Palicka</a>
 * @version $Id: VersionInfoSource.java 5073 2011-01-23 12:54:01Z mbenda@CLANCE.LOCAL $
 */
public interface VersionInfoSource {

    /**
     * Retrieves version information from available application modules.
     * The method allows to specify a filter that can be used to remove invalid entries from the result.
     * <p>
     * The filter is also an instance of {@code VersionInfo}, but its fields are expected to contain regular
     * expressions instead of plain values.
     * Each available version entry is matched against patterns in the filter (field-by-field).
     * If any of the fields does not match, the version entry is excluded from the result.
     * <p>
     * If the <em>filter</em> is {@code null}, all entries are returned.
     * <p>
     * If a field in the filter is set to {@code null}, then all values are allowed.
     *
     * @param filter the filter used to remove invalid or unwanted entries.
     * @return an array of version entries (in ascending order).
     */
    VersionInfo[] getVersionInformation(VersionInfo filter);
}

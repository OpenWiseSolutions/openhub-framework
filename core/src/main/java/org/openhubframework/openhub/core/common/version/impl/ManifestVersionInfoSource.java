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

package org.openhubframework.openhub.core.common.version.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.PatternSyntaxException;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import org.openhubframework.openhub.core.common.version.VersionInfo;
import org.openhubframework.openhub.core.common.version.VersionInfoSource;


/**
 * This class can be used to determine versions of application modules.
 * The version information is retrieved from manifest files (MANIFEST.MF)
 * that are available at classpath (JAR) and at servlet context path (WAR).
 *
 * @author Michal Palicka
 * @since 0.1
 */
@Component
public class ManifestVersionInfoSource implements VersionInfoSource, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ManifestVersionInfoSource.class);

    //----------------------------------------------------------------------
    // class (static) fields
    //----------------------------------------------------------------------

    private static String MANIFEST_CLASSPATH_RESOURCE_NAME = "classpath*:/META-INF/MANIFEST.MF";

    private static String MANIFEST_CONTEXT_RESOURCE_NAME = "/META-INF/MANIFEST.MF";

    private static String ATTR_TITLE = "Implementation-Title";

    private static String ATTR_VENDOR_ID = "Implementation-Vendor-Id";

    private static String ATTR_VERSION = "Implementation-Version";

    private static String ATTR_REVISION = "Implementation-Build";

    private static String ATTR_TIMESTAMP = "Implementation-Timestamp";

    private static String ATTR_BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";

    private static String ATTR_BUNDLE_NAME = "Bundle-Name";

    private static String ATTR_BUNDLE_VERSION = "Bundle-Version";

    //----------------------------------------------------------------------
    // instance fields
    //----------------------------------------------------------------------

    private ApplicationContext applicationContext;

    //----------------------------------------------------------------------
    // public methods
    //----------------------------------------------------------------------

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    //----------------------------------------------------------------------
    // VersionInfoSource
    //----------------------------------------------------------------------

    /**
     * Retrieves version information from all manifests that are available at classpath.
     * In many cases, the manifest attributes are not properly set and the version information may be incomplete.
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
    @Override
    public VersionInfo[] getVersionInformation(@Nullable VersionInfo filter) {
        // use set to remove duplicate entries
        Set<VersionInfo> result = new HashSet<VersionInfo>();
        try {
            // manifest files at classpath (JARs)
            result.addAll(getVersionData(MANIFEST_CLASSPATH_RESOURCE_NAME, filter));

            if (applicationContext instanceof WebApplicationContext) {
                // manifest file from servlet context (WAR)
                result.addAll(getVersionData(MANIFEST_CONTEXT_RESOURCE_NAME, filter));
            }
        } catch (Exception e) {
            LOG.warn("Unable to retrieve version information: {}", e.getMessage());
        }
        return result.toArray(new VersionInfo[result.size()]);
    }

    //----------------------------------------------------------------------
    // private methods
    //----------------------------------------------------------------------

    private Collection<VersionInfo> getVersionData(String location, @Nullable VersionInfo filter) throws IOException,
        PatternSyntaxException {
        Collection<VersionInfo> result = new ArrayList<VersionInfo>();
        Resource[] resources = applicationContext.getResources(location);
        for (Resource resource : resources) {
            try {
                InputStream is = resource.getInputStream();
                if (is != null) {
                    Manifest manifest = new Manifest(is);
                    VersionInfo info = createVersionInfo(manifest);
                    // Version entries may be empty or incomplete.
                    // Return only entries that match the specified filter.

                    if (info.matches(filter)) {
                        result.add(info);
                    }
                }
            } catch (IOException e) {
                LOG.error("Unable to process manifest resource '{}'", e, resource.getURL());
                throw e;

            } catch (PatternSyntaxException e) {
                LOG.error("Unable to process version data, invalid filter '{}'", e, filter != null ? filter.toString() : null);
            }
        }
        return result;
    }

    private VersionInfo createVersionInfo(Manifest manifest) {
        Attributes attrs = manifest.getMainAttributes();
        return new VersionInfo(getTitle(attrs), getVendorId(attrs), getVersion(attrs), attrs.getValue(ATTR_REVISION),
            attrs.getValue(ATTR_TIMESTAMP));
    }

    private String getVersion(Attributes attrs) {
        String title = attrs.getValue(ATTR_VERSION);
        if (title == null) {
            title = attrs.getValue(ATTR_BUNDLE_VERSION);
        }
        return title;
    }

    private String getTitle(Attributes attrs) {
        String title = attrs.getValue(ATTR_TITLE);
        if (title == null) {
            title = attrs.getValue(ATTR_BUNDLE_NAME);
        }
        return title;
    }

    private String getVendorId(Attributes attrs) {
        String vendorId = attrs.getValue(ATTR_VENDOR_ID);
        if (vendorId == null) {
            vendorId = attrs.getValue(ATTR_BUNDLE_SYMBOLIC_NAME);
        }
        return vendorId;
    }
}

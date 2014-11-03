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

package org.cleverbus.core.common.version.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.cleverbus.core.common.version.VersionInfo;


/**
 * Comparator for {@link VersionInfo} objects.
 *
 * @author <a href="mailto:michal.palicka@cleverlance.com">Michal Palicka</a>
 * @version $Id: VersionInfoComparator.java 5073 2011-01-23 12:54:01Z mbenda@CLANCE.LOCAL $
 */
public class VersionInfoComparator implements Comparator<VersionInfo>, Serializable {

    //----------------------------------------------------------------------
    // instance fields
    //----------------------------------------------------------------------

    /**
     * The name of the {@link VersionInfo} property used for ordering.
     */
    private String propertyName;

    /**
     * Determines the order direction (ascending/descending).
     */
    private boolean ascending = true;

    /**
     * The comparator used for comparisons.
     */
    private Comparator<VersionInfo> comparator;

    //----------------------------------------------------------------------
    // constructors
    //----------------------------------------------------------------------

    /**
     * Constructs a new {@code VersionInfoComparator} instance
     * that uses the natural ordering of {@link VersionInfo} objects.
     *
     * @param ascending specifies the order direction (ascending/descending).
     */
    public VersionInfoComparator(boolean ascending) {
        this(null, ascending);
    }

    /**
     * Constructs a new {@code VersionInfoComparator} instance, that compares objects
     * by the specified property.
     *
     * @param propertyName the name of the property to order by. If the parameter is set to {@code null},
     *        then the natural ordering is used.
     * @param ascending specifies the order direction (ascending/descending).
     */
    public VersionInfoComparator(String propertyName, boolean ascending) {
        this.propertyName = propertyName;
        this.ascending = ascending;

        // initialize the comparator
        if ("title".equalsIgnoreCase(propertyName)) {
            this.comparator = new VersionInfoTitleComparator(ascending);
        } else if ("vendorId".equalsIgnoreCase(propertyName)) {
            this.comparator = new VersionInfoVendorIdComparator(ascending);
        } else if ("version".equalsIgnoreCase(propertyName)) {
            this.comparator = new VersionInfoVersionComparator(ascending);
        } else if ("revision".equalsIgnoreCase(propertyName)) {
            this.comparator = new VersionInfoRevisionComparator(ascending);
        } else if ("timestamp".equalsIgnoreCase(propertyName)) {
            this.comparator = new VersionInfoTimestampComparator(ascending);
        } else {
            this.comparator = null;
        }
    }

    //----------------------------------------------------------------------
    // public methods
    //----------------------------------------------------------------------

    @Override
    public int compare(VersionInfo o1, VersionInfo o2) {
        if (comparator == null) {
            // natural order
            int result = o1.compareTo(o2);
            return (ascending) ? result : -result;
        } else {
            return this.comparator.compare(o1, o2);
        }
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isAscending() {
        return ascending;
    }

    //----------------------------------------------------------------------
    // equality
    //----------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        VersionInfoComparator that = (VersionInfoComparator) o;

        return (comparator != null) ? comparator.equals(that.comparator) : that.comparator == null;
    }

    @Override
    public int hashCode() {
        return (comparator != null ? comparator.hashCode() : 0);
    }
}

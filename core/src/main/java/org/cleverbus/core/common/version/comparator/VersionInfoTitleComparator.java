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

import org.apache.commons.lang.builder.CompareToBuilder;


/**
 * Compares {@link VersionInfo} objects by title.
 *
 * @author <a href="mailto:michal.palicka@cleverlance.com">Michal Palicka</a>
 * @version $Id: VersionInfoTitleComparator.java 5073 2011-01-23 12:54:01Z mbenda@CLANCE.LOCAL $
 */
public class VersionInfoTitleComparator implements Comparator<VersionInfo>, Serializable {

    //----------------------------------------------------------------------
    // instance fields
    //----------------------------------------------------------------------

    /**
     * Determines the order direction (ascending/descending).
     */
    private boolean ascending = true;

    //----------------------------------------------------------------------
    // constructors
    //----------------------------------------------------------------------

    /**
     * Constructs a new {@code VersionInfoTitleComparator} instance with the specified
     * order direction.
     *
     * @param ascending the order direction.
     */
    public VersionInfoTitleComparator(boolean ascending) {
        this.ascending = ascending;
    }

    //----------------------------------------------------------------------
    // public methods
    //----------------------------------------------------------------------

    @Override
    public int compare(VersionInfo o1, VersionInfo o2) {
        int value;

        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            value = -1;
        } else if (o2 == null) {
            value = +1;
        } else {
            value = new CompareToBuilder().append(o1.getTitle(), o2.getTitle()).toComparison();
        }
        return (ascending) ? value : -value;
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

        VersionInfoTitleComparator that = (VersionInfoTitleComparator) o;

        return (ascending == that.ascending);
    }

    @Override
    public int hashCode() {
        return (ascending ? 1 : 0);
    }
}

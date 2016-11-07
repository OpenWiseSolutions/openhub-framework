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

package org.openhubframework.openhub.common.log;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.UID;

/**
 * Represents a globally unique ID by combining the UID (which is unique
 * with respect to the host) and the host's Internet address.
 *
 * @author Michal Palicka
 * @since 0.1
 */
public class GUID implements Serializable {

    //----------------------------------------------------------------------
    // class (static) fields
    //----------------------------------------------------------------------

    /**
     * A dummy address that is used when the InternetAddress
     * of the local host cannot be found. In this case, the
     * global uniqueness of the identifier may be compromised.
     */
    private static final String UNKNOWN_HOST = "0.0.0.0";

    /**
     * This field is used as a cache for the internet address
     * of the local host.
     */
    private static String LOCAL_HOST;

    //----------------------------------------------------------------------
    // class (static) methods
    //----------------------------------------------------------------------

    static {
        try {
            LOCAL_HOST = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOCAL_HOST = UNKNOWN_HOST;
        }
    }

    //----------------------------------------------------------------------
    // instance fields
    //----------------------------------------------------------------------

    /**
     * The internet address of the host from which this instance originates.
     */
    private String host;

    /**
     * An identifier (unique to host).
     */
    private UID uid;

    //----------------------------------------------------------------------
    // constructors
    //----------------------------------------------------------------------

    /**
     * Creates a globally unique ID.
     */
    public GUID() {
        host = LOCAL_HOST;
        uid = new UID();
    }

    //----------------------------------------------------------------------
    // comparing
    //----------------------------------------------------------------------

    /**
     * Indicates whether some other object is equal to this GUID.
     *
     * @param obj the object to compare with.
     * @return true if the two objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GUID)) {
            return false;
        }
        final GUID guid = (GUID) obj;
        return ((this.host.equals(guid.host)) && (this.uid.equals((guid.uid))));
    }

    /**
     * Returns a hashcode for this GUID.
     *
     * @return a hashcode for this GUID.
     */
    @Override
    public int hashCode() {
        int result;
        result = host.hashCode();
        result = (29 * result) + uid.hashCode();
        return result;
    }

    //----------------------------------------------------------------------
    // string representation
    //----------------------------------------------------------------------

    /**
     * Returns the string representation of this GUID.
     *
     * @return the string representation of this GUID.
     */
    @Override
    public String toString() {
        return host + ":" + uid.toString();
    }
}

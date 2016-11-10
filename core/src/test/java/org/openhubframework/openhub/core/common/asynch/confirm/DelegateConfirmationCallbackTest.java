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

package org.openhubframework.openhub.core.common.asynch.confirm;

import static junit.framework.Assert.assertNotNull;

import java.util.Collections;
import java.util.Set;

import org.openhubframework.openhub.api.asynch.confirm.ExternalSystemConfirmation;
import org.openhubframework.openhub.api.entity.ExternalSystemExtEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.core.AbstractCoreTest;

import org.junit.Before;
import org.junit.Test;


/**
 * Test suite for {@link DelegateConfirmationCallback}.
 *
 * @author Tomas Hanus
 */
public class DelegateConfirmationCallbackTest extends AbstractCoreTest {

    private DelegateConfirmationCallback confirmation;

    @Before
    public void initDelegateConfirmationCallback() {
        confirmation = new DelegateConfirmationCallback();
        setPrivateField(confirmation, "msgConfirmations", Collections.singletonList(getDefaultImplementation("CRM")));
    }

    @Test
    public void testGetImplementation() throws Exception {
        assertNotNull(confirmation.getImplementation(getSourceSystem("CRM")));
    }

    private static ExternalSystemConfirmation getDefaultImplementation(final String sourceSystem) {
        return new ExternalSystemConfirmation() {
            @Override
            public void confirm(Message msg) {
                //nothing
            }

            @Override
            public Set<ExternalSystemExtEnum> getExternalSystems() {
                return Collections.singleton(getSourceSystem(sourceSystem));
            }
        };
    }

    private static ExternalSystemExtEnum getSourceSystem(final String sourceSystem) {
        return new ExternalSystemExtEnum() {
            @Override
            public String getSystemName() {
                return sourceSystem;
            }
        };
    }

}

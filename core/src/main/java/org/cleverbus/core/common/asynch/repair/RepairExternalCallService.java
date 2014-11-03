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

package org.cleverbus.core.common.asynch.repair;

import org.cleverbus.api.entity.ExternalCallStateEnum;

/**
 * Repairs external calls that remain in for too long in state {@link ExternalCallStateEnum#PROCESSING}
 * by forcefully setting them to state {@link ExternalCallStateEnum#FAILED}.
 */
public interface RepairExternalCallService {

    public static final String BEAN = "repairExternalCallService";

    /**
     * Repairs external calls that remain in for too long in state {@link ExternalCallStateEnum#PROCESSING}
     * by forcefully setting them to state {@link ExternalCallStateEnum#FAILED}.
     */
    void repairProcessingExternalCalls();
}

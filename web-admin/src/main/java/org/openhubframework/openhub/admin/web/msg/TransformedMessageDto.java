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

package org.openhubframework.openhub.admin.web.msg;


import org.openhubframework.openhub.admin.dao.dto.MessageReportDto;
import org.openhubframework.openhub.api.entity.MsgStateEnum;


/**
 * DTO for message report view.
 *
 * @author Viliam Elischer
 */
public class TransformedMessageDto {

    private String serviceName; //  as service
    private String operationName; // as operation_name
    private String sourceSystem; // asd source_system

    /* STATES fields obtain their value from SQL -> Column State, default 0 */
    private int stateOK = 0;
    private int stateProcessing = 0;
    private int statePartlyFailed = 0;
    private int stateFailed = 0;
    private int stateWaiting = 0;
    private int stateWaitingForRes = 0;
    private int stateCancel = 0;

    public String getServiceName() {
        return serviceName;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public int getStateOK() {
        return stateOK;
    }

    public int getStateProcessing() {
        return stateProcessing;
    }

    public int getStatePartlyFailed() {
        return statePartlyFailed;
    }

    public int getStateFailed() {
        return stateFailed;
    }

    public int getStateWaiting() {
        return stateWaiting;
    }

    public int getStateWaitingForRes() {
        return stateWaitingForRes;
    }

    public int getStateCancel() {
        return stateCancel;
    }

    // Atomic Setters
    void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    // Setters for stateFields
    void addStatesCount(int stateCount, String state) {

        if (MsgStateEnum.OK.name().equals(state)) {
            this.stateOK += stateCount;
        } else if (MsgStateEnum.PROCESSING.name().equals(state)) {
            this.stateProcessing += stateCount;
        } else if (MsgStateEnum.PARTLY_FAILED.name().equals(state)) {
            this.statePartlyFailed += stateCount;
        } else if (MsgStateEnum.FAILED.name().equals(state)) {
            this.stateFailed += stateCount;
        } else if (MsgStateEnum.WAITING.name().equals(state)) {
            this.stateWaiting += stateCount;
        } else if (MsgStateEnum.WAITING_FOR_RES.name().equals(state)) {
            this.stateWaitingForRes += stateCount;
        } else if (MsgStateEnum.CANCEL.name().equals(state)) {
            this.stateCancel += stateCount;
        }
    }

    /**
     * Method used for comparison of the last item's fields in the transformed list and the next item's fields.
     *
     * @param item object is the next object {@link MessageReportDto} in the result list
     * @return true if compared fields don't mach, if not return is false
     */
    public boolean differs(MessageReportDto item) {
        return !serviceName.equals(item.getServiceName())
                || !operationName.equals(item.getOperationName())
                || !sourceSystem.equals(item.getSourceSystem());
    }

    /**
     * Class fields setters.
     *
     * @param item the next object to the last object in the result list of type {@link MessageReportDto}
     */
    public void fill(MessageReportDto item) {
        this.setServiceName(item.getServiceName());
        this.setOperationName(item.getOperationName());
        this.setSourceSystem(item.getSourceSystem());
        this.addStatesCount(item.getStateCount(), item.getState());
    }

    /**
     * Custom setter method for the case that item and last object in the result list match
     * in all fields except the state or statesCount.
     *
     * @param item the next object to the last object in the result list of type {@link MessageReportDto}
     */
    public void add(MessageReportDto item) {
        addStatesCount(item.getStateCount(), item.getState());
    }
}

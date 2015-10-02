/*
*  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.apimgt.impl.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.WorkflowResponse;
import org.wso2.carbon.apimgt.api.WorkflowStatus;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.dto.WorkflowDTO;

import java.util.List;

public class SubscriptionCreationSimpleWorkflowExecutor extends WorkflowExecutor {

    private static final Log log = LogFactory.getLog(SubscriptionCreationSimpleWorkflowExecutor.class);

    @Override
    public String getWorkflowType() {
        return WorkflowConstants.WF_TYPE_AM_SUBSCRIPTION_CREATION;
    }

    @Override
    public List<WorkflowDTO> getWorkflowDetails(String workflowStatus) throws WorkflowException{
        return null;
    }

    /**
     * This method executes subscription creation simple workflow without returning
     *
     * @param workflowDTO - The WorkflowDTO which contains workflow contextual information related to the workflow.
     * @throws WorkflowException Thrown when the workflow execution was not fully performed
     */
    @Override
    public void execute(WorkflowDTO workflowDTO) throws WorkflowException {
        workflowDTO.setStatus(WorkflowStatus.APPROVED);
        complete(workflowDTO);
        super.publishEvents(workflowDTO);
    }

    /**
     * This method executes subscription creation simple workflow and return workflow response back to the caller
     *
     * @param workflowDTO  The WorkflowDTO which contains workflow contextual information related to the workflow
     * @param responseType Type of the response to be returned after the workflow execution
     * @return workflow response back to the caller
     * @throws WorkflowException Thrown when the workflow execution was not fully performed
     */
    @Override
    public WorkflowResponse execute(WorkflowDTO workflowDTO, String responseType) throws WorkflowException {
        workflowDTO.setStatus(WorkflowStatus.APPROVED);
        WorkflowResponse workflowResponse = complete(workflowDTO, responseType);
        super.publishEvents(workflowDTO);

        return workflowResponse;
    }

    /**
     * This method completes subscription creation simple workflow without returning
     *
     * @param workflowDTO - The WorkflowDTO which contains workflow contextual information related to the workflow.
     * @throws WorkflowException Thrown when the workflow execution was not fully performed
     */
    @Override
    public void complete(WorkflowDTO workflowDTO) throws WorkflowException {
        ApiMgtDAO apiMgtDAO = new ApiMgtDAO();
        try {
            apiMgtDAO.updateSubscriptionStatus(Integer.parseInt(workflowDTO.getWorkflowReference()),
                    APIConstants.SubscriptionStatus.UNBLOCKED);
        } catch (APIManagementException e) {
            log.error("Could not complete subscription creation workflow", e);
            throw new WorkflowException("Could not complete subscription creation workflow", e);
        }
    }

    /**
     * This method completes subscription creation simple workflow and return workflow response back to the caller
     *
     * @param workflowDTO  The WorkflowDTO which contains workflow contextual information related to the workflow
     * @param responseType Thrown when the workflow execution was not fully performed
     * @return workflow response back to the caller
     * @throws WorkflowException
     */
    @Override
    public WorkflowResponse complete(WorkflowDTO workflowDTO, String responseType) throws WorkflowException {
        ApiMgtDAO apiMgtDAO = new ApiMgtDAO();
        WorkflowResponse workflowResponse;
        try {
            apiMgtDAO.updateSubscriptionStatus(Integer.parseInt(workflowDTO.getWorkflowReference()),
                    APIConstants.SubscriptionStatus.UNBLOCKED);

            if (WorkflowConstants.RESPONSE_TYPE_HTTP.equals(responseType)) {
                workflowResponse = new HttpWorkflowResponse();
            } else {
                workflowResponse = new NonHttpWorkflowResponse();
            }
        } catch (APIManagementException e) {
            log.error("Could not complete subscription creation workflow", e);
            throw new WorkflowException("Could not complete subscription creation workflow", e);
        }
        return workflowResponse;
    }
}

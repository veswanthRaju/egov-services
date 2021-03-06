/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.eis.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.eis.broker.MovementProducer;
import org.egov.eis.model.Movement;
import org.egov.eis.model.enums.MovementStatus;
import org.egov.eis.model.enums.TypeOfMovement;
import org.egov.eis.repository.MovementRepository;
import org.egov.eis.util.ApplicationConstants;
import org.egov.eis.web.contract.MovementRequest;
import org.egov.eis.web.contract.MovementResponse;
import org.egov.eis.web.contract.MovementSearchRequest;
import org.egov.eis.web.contract.MovementUploadResponse;
import org.egov.eis.web.contract.RequestInfo;
import org.egov.eis.web.contract.ResponseInfo;
import org.egov.eis.web.contract.factory.ResponseInfoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MovementService {

    public static final Logger LOGGER = LoggerFactory.getLogger(MovementService.class);

    @Value("${kafka.topics.movement.create.name}")
    private String movementCreateTopic;

    @Value("${kafka.topics.movement.create.key}")
    private String movementCreateKey;

    @Value("${kafka.topics.movement.update.name}")
    private String movementUpdateTopic;

    @Value("${kafka.topics.movement.update.key}")
    private String movementUpdateKey;

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private HRStatusService hrStatusService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovementProducer movementProducer;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
    
    @Autowired
    private PositionService positionService;
    
    @Autowired
    private ApplicationConstants applicationConstants;

    public List<Movement> getMovements(final MovementSearchRequest movementSearchRequest,
            final RequestInfo requestInfo) {
        return movementRepository.findForCriteria(movementSearchRequest, requestInfo);
    }

    public ResponseEntity<?> createMovements(final MovementRequest movementRequest, final String type) {
        final Boolean isExcelUpload = type != null && "upload".equalsIgnoreCase(type);
        movementRequest.setType(type);
        final List<Movement> movementsList = validate(movementRequest);
        final List<Movement> successMovementsList = new ArrayList<>();
        final List<Movement> errorMovementsList = new ArrayList<>();
        for (final Movement movement : movementsList)
            if (movement.getErrorMsg().isEmpty())
                successMovementsList.add(movement);
            else
                errorMovementsList.add(movement);
        movementRequest.setMovement(successMovementsList);
        for (final Movement movement : movementRequest.getMovement())
            if (isExcelUpload)
                movement.setStatus(hrStatusService.getHRStatuses("APPROVED", movement.getTenantId(),
                        movementRequest.getRequestInfo()).get(0).getId());
            else
                movement.setStatus(hrStatusService.getHRStatuses("APPLIED", movement.getTenantId(),
                        movementRequest.getRequestInfo()).get(0).getId());
        String movementRequestJson = null;
        try {
            movementRequestJson = objectMapper.writeValueAsString(movementRequest);
            LOGGER.info("movementRequestJson::" + movementRequestJson);
        } catch (final JsonProcessingException e) {
            LOGGER.error("Error while converting Movement to JSON", e);
            e.printStackTrace();
        }
        try {
            movementProducer.sendMessage(movementCreateTopic, movementCreateKey,
                    movementRequestJson);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        if (isExcelUpload)
            return getSuccessResponseForUpload(successMovementsList, errorMovementsList,
                    movementRequest.getRequestInfo());
        else
            return getSuccessResponseForCreate(movementsList, movementRequest.getRequestInfo());
    }

    private List<Movement> validate(final MovementRequest movementRequest) {
        for (final Movement movement : movementRequest.getMovement()) {
            String errorMsg = "";
            if (movement.getTypeOfMovement().equals(TypeOfMovement.PROMOTION) && movement.getStatus()
                    .equals(hrStatusService.getHRStatuses(MovementStatus.APPROVED.toString(), movement.getTenantId(),
                            movementRequest.getRequestInfo()).get(0).getId()))
                errorMsg = validateEmployeeForPromotion(movement, movementRequest.getRequestInfo());
            movement.setErrorMsg(errorMsg);
        }
        return movementRequest.getMovement();
    }

    private String validateEmployeeForPromotion(final Movement movement, final RequestInfo requestInfo) {
        String errorMsg = "";
        List<Long> positions = positionService.getPositions(movement, requestInfo);
        if (positions.contains(movement.getPositionAssigned()))
            errorMsg = applicationConstants.getErrorMessage(ApplicationConstants.ERR_POSITION_NOT_VACANT) + " ";
        return errorMsg;
    }

    private ResponseEntity<?> getSuccessResponseForCreate(final List<Movement> movementsList,
            final RequestInfo requestInfo) {
        final MovementResponse movementRes = new MovementResponse();
        HttpStatus httpStatus = HttpStatus.OK;
        if (!movementsList.get(0).getErrorMsg().isEmpty())
            httpStatus = HttpStatus.BAD_REQUEST;
        movementRes.setMovement(movementsList);
        final ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
        responseInfo.setStatus(httpStatus.toString());
        movementRes.setResponseInfo(responseInfo);
        return new ResponseEntity<>(movementRes, httpStatus);
    }

    private ResponseEntity<?> getSuccessResponseForUpload(final List<Movement> successMovementsList,
            final List<Movement> errorMovementsList, final RequestInfo requestInfo) {
        final MovementUploadResponse movementUploadResponse = new MovementUploadResponse();
        movementUploadResponse.getSuccessList().addAll(successMovementsList);
        movementUploadResponse.getErrorList().addAll(errorMovementsList);

        final ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
        responseInfo.setStatus(HttpStatus.OK.toString());
        movementUploadResponse.setResponseInfo(responseInfo);
        return new ResponseEntity<>(movementUploadResponse, HttpStatus.OK);
    }

    public MovementRequest create(final MovementRequest movementRequest) {
        return movementRepository.saveMovement(movementRequest);
    }

    public ResponseEntity<?> updateMovement(final MovementRequest movementRequest) {
        List<Movement> movements = new ArrayList<>();
        movements.add(movementRequest.getMovement().get(0));
        movements = validate(movementRequest);
        if (movements.get(0).getErrorMsg().isEmpty()) {
            final MovementSearchRequest movementSearchRequest = new MovementSearchRequest();
            final List<Long> ids = new ArrayList<>();
            ids.add(movements.get(0).getId());
            movementSearchRequest.setId(ids);
            final List<Movement> oldMovements = movementRepository.findForCriteria(movementSearchRequest,
                    movementRequest.getRequestInfo());
            movements.get(0).setStatus(oldMovements.get(0).getStatus());
            movements.get(0).setStateId(oldMovements.get(0).getStateId());
            String movementRequestJson = null;
            try {
                movementRequestJson = objectMapper.writeValueAsString(movementRequest);
                LOGGER.info("movementRequestJson::" + movementRequestJson);
            } catch (final JsonProcessingException e) {
                LOGGER.error("Error while converting Movement to JSON", e);
                e.printStackTrace();
            }
            try {
                movementProducer.sendMessage(movementUpdateTopic, movementUpdateKey,
                        movementRequestJson);
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
            movementStatusChange(movements.get(0), movementRequest.getRequestInfo());
        }
        return getSuccessResponseForCreate(movements, movementRequest.getRequestInfo());
    }

    private void movementStatusChange(final Movement movement, final RequestInfo requestInfo) {
        final String workFlowAction = movement.getWorkflowDetails().getAction();
        if ("Approve".equalsIgnoreCase(workFlowAction))
            movement
                    .setStatus(hrStatusService.getHRStatuses(MovementStatus.APPROVED.toString(), movement.getTenantId(),
                            requestInfo).get(0).getId());
        else if ("Reject".equalsIgnoreCase(workFlowAction))
            movement
                    .setStatus(hrStatusService.getHRStatuses(MovementStatus.REJECTED.toString(), movement.getTenantId(),
                            requestInfo).get(0).getId());
        else if ("Cancel".equalsIgnoreCase(workFlowAction))
            movement
                    .setStatus(hrStatusService.getHRStatuses(MovementStatus.CANCELLED.toString(), movement.getTenantId(),
                            requestInfo).get(0).getId());
        else if ("Submit".equalsIgnoreCase(workFlowAction))
            movement
                    .setStatus(hrStatusService.getHRStatuses(MovementStatus.RESUBMITTED.toString(), movement.getTenantId(),
                            requestInfo).get(0).getId());
    }

    public Movement update(final MovementRequest movementRequest) {
        return movementRepository.updateMovement(movementRequest);
    }
}
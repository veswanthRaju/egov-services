package org.egov.demand.web.controller;

import org.egov.common.contract.request.RequestInfo;
import org.egov.demand.model.GenerateBillCriteria;
import org.egov.demand.service.BillService;
import org.egov.demand.web.contract.BillRequest;
import org.egov.demand.web.contract.BillResponse;
import org.egov.demand.web.contract.RequestInfoWrapper;
import org.egov.demand.web.contract.factory.ResponseFactory;
import org.egov.demand.web.validator.BillValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("bill")
public class BillController {
	
	@Autowired
	private BillService billService;
	
	@Autowired
	private BillValidator billValidator;
	
	@Autowired
	private ResponseFactory responseFactory;

	@PostMapping("_create")
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody BillRequest billRequest, BindingResult bindingResult){

		log.debug("create billRequest:"+billRequest);
		
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(responseFactory.
					getErrorResponse(bindingResult, billRequest.getRequestInfo()), HttpStatus.BAD_REQUEST);
		}
		billValidator.validateBillRequest(billRequest);
		BillResponse billResponse = billService.createAsync(billRequest);
		
		return new ResponseEntity<>(billResponse,HttpStatus.CREATED);
	}
	
	@PostMapping("_generate")
	@ResponseBody
	public ResponseEntity<?> genrateBill(@RequestBody RequestInfoWrapper requestInfoWrapper, 
			@ModelAttribute GenerateBillCriteria generateBillCriteria, BindingResult bindingResult){
		log.debug("genrateBill generateBillCriteria : "+generateBillCriteria);
		log.debug("genrateBill requestInfoWrapper : "+requestInfoWrapper);
		
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(responseFactory.
					getErrorResponse(bindingResult, requestInfoWrapper.getRequestInfo()), HttpStatus.BAD_REQUEST);
		}
		//billValidator.validateBillRequest(billRequest);
		BillResponse billResponse = billService.generateBill(generateBillCriteria, requestInfoWrapper.getRequestInfo());
		
		return new ResponseEntity<>(billResponse,HttpStatus.CREATED);
	}
	
	@PostMapping("_apportion")
	@ResponseBody
	public ResponseEntity<?> apportion(@RequestBody BillRequest billRequest, @PathVariable String tenantId,
			BindingResult bindingResult) {

		RequestInfo requestInfo = billRequest.getRequestInfo();
		log.debug("genrateBill tenantId : " + tenantId);
		log.debug("genrateBill requestInfo : " + requestInfo);

		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(responseFactory.getErrorResponse(bindingResult, requestInfo),
					HttpStatus.BAD_REQUEST);
		}
		// billValidator.validateBillRequest(billRequest);
		BillResponse billResponse = billService.apportion(billRequest);

		return new ResponseEntity<>(billResponse, HttpStatus.CREATED);
	}
}

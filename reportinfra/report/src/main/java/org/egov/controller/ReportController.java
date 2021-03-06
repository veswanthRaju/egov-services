package org.egov.controller;
import javax.validation.Valid;

import org.egov.ReportApp;
import org.egov.common.contract.request.RequestInfo;
import org.egov.domain.model.MetaDataRequest;
import org.egov.domain.model.ReportDefinitions;
import org.egov.domain.model.ReportYamlMetaData;
import org.egov.report.service.ReportService;
import org.egov.swagger.model.MetadataResponse;
import org.egov.swagger.model.ReportRequest;
import org.egov.swagger.model.ReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

	public ReportDefinitions reportDefinitions;
	public ReportYamlMetaData reportYamlMetaData;
	@Autowired
	public ReportController(ReportDefinitions reportDefinitions) {
		this.reportDefinitions = reportDefinitions;
	}
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
    public static ResourceLoader resourceLoader;
	
	  
	

	@PostMapping("/report/metadata/_get")
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody @Valid final MetaDataRequest metaDataRequest,
			final BindingResult errors) {
		MetadataResponse mdr = reportService.getMetaData(metaDataRequest);
		return reportService.getSuccessResponse(mdr, metaDataRequest.getRequestInfo(),metaDataRequest.getTenantId());
		
	}
	
	@PostMapping("/report/_get")
	@ResponseBody
	public ResponseEntity<?> getReportData(@RequestBody @Valid final ReportRequest reportRequest,
			final BindingResult errors) {
		
		ReportResponse reportResponse = reportService.getReportData(reportRequest);
		return new ResponseEntity<>(reportResponse, HttpStatus.OK);
	}
	@PostMapping("/report/_reload")
	@ResponseBody
	public ResponseEntity<?> reloadYamlData(@RequestBody @Valid final RequestInfo reportRequest,
			final BindingResult errors) {
		ReportApp.loadYaml();
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
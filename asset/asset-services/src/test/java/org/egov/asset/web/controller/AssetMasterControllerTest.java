package org.egov.asset.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.egov.asset.contract.AssetStatusResponse;
import org.egov.asset.model.AssetStatus;
import org.egov.asset.model.AssetStatusCriteria;
import org.egov.asset.model.AuditDetails;
import org.egov.asset.model.StatusValue;
import org.egov.asset.service.AssetMasterService;
import org.egov.asset.util.FileUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(AssetMasterController.class)
public class AssetMasterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AssetMasterService assetMasterService;

	@Test
	public void test_Should_Return_Status() throws Exception {
		final List<AssetStatus> assetStatus = new ArrayList<>();
		assetStatus.add(getAssetStatus());

		final AssetStatusResponse assetStatusResponse = new AssetStatusResponse();
		assetStatusResponse.setAssetStatus(assetStatus);
		assetStatusResponse.setResponseInfo(new ResponseInfo());

		when(assetMasterService.search(Matchers.any(AssetStatusCriteria.class), Matchers.any(RequestInfo.class)))
				.thenReturn(assetStatusResponse);

		mockMvc.perform(post("/assetstatuses/_search").param("code", "CAPITALIZED").param("tenantId", "default")
				.contentType(MediaType.APPLICATION_JSON).content(getFileContents("requestinfowrapper.json")))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(getFileContents("status.json")));
	}

	private AssetStatus getAssetStatus() {
		final AssetStatus assetStatus = new AssetStatus();
		assetStatus.setObjectName("Asset Status");

		final List<StatusValue> statusValues = new ArrayList<>();

		final StatusValue statusValue = new StatusValue();
		statusValue.setCode("CAPITALIZED");
		statusValue.setDescription("Asset is Capitalized");

		statusValues.add(statusValue);

		assetStatus.setStatusValues(statusValues);

		final AuditDetails auditDetails = new AuditDetails();
		auditDetails.setCreatedBy("1");
		auditDetails.setCreatedDate(Long.valueOf("1499061748794"));
		auditDetails.setLastModifiedBy("1");
		auditDetails.setLastModifiedDate(Long.valueOf("1499061748794"));

		assetStatus.setAuditDetails(auditDetails);

		assetStatus.setTenantId("default");
		return assetStatus;
	}

	@Test
	public void test_Should_Return_AssetCategoryType() throws Exception {

		mockMvc.perform(get("/GET_ASSET_CATEGORY_TYPE")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(getFileContents("assetcategorytype.json")));
	}

	@Test
	public void test_Should_Return_DepreciationMethod() throws Exception {

		mockMvc.perform(get("/GET_DEPRECIATION_METHOD")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(getFileContents("depreciationmethod.json")));
	}

	@Test
	public void test_Should_Return_ModeOfAcquisition() throws Exception {

		mockMvc.perform(get("/GET_MODE_OF_ACQUISITION")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(getFileContents("modeofacquisition.json")));
	}

	private String getFileContents(final String fileName) throws IOException {
		return new FileUtils().getFileContents(fileName);
	}
}
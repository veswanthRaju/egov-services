package org.egov.tenant.web.controller;


import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.tenant.domain.model.Tenant;
import org.egov.tenant.domain.service.TenantService;
import org.egov.tenant.web.contract.CreateTenantRequest;
import org.egov.tenant.web.contract.TenantResponse;
import org.egov.tenant.web.contract.TenantSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/tenant")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping(value="_search")
    public TenantResponse searchTenantsForCodes(@RequestBody TenantSearchRequest tenantSearchRequest) {
        RequestInfo requestInfo = tenantSearchRequest.getRequestInfo();
        List<Tenant> tenants =  tenantService.searchTenantsForCodes(tenantSearchRequest.toDomain());
        return getSuccessResponse(tenants,requestInfo);
    }

    @PostMapping(value="_create")
    public TenantResponse createTenant(@RequestBody CreateTenantRequest createTenantRequest) {
        RequestInfo requestInfo = createTenantRequest.getRequestInfo();
        Tenant tenant = tenantService.createTenant(createTenantRequest.toDomainForCreateTenantRequest());
        List<Tenant> tenantList = new ArrayList<Tenant>();
        tenantList.add(tenant);
        return getSuccessResponse(tenantList,requestInfo);
    }

    private TenantResponse getSuccessResponse(final List<Tenant> tenant, final RequestInfo requestInfo) {
        final ResponseInfo responseInfo = ResponseInfo.builder().apiId(requestInfo.getApiId())
                .ver(requestInfo.getVer()).msgId(requestInfo.getMsgId()).status(HttpStatus.OK.toString()).build();
        return new TenantResponse(responseInfo,tenant);
    }
}
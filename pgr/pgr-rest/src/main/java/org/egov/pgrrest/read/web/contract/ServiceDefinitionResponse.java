package org.egov.pgrrest.read.web.contract;

import lombok.Getter;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.pgrrest.common.domain.model.ServiceDefinition;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ServiceDefinitionResponse {
    private ResponseInfo responseInfo;
    private String tenantId;
    private String serviceCode;
    private List<AttributeDefinition> attributes;

    public ServiceDefinitionResponse(ResponseInfo responseInfo, ServiceDefinition serviceDefinition) {
        this.responseInfo = responseInfo;
        this.tenantId = serviceDefinition.getTenantId();
        this.serviceCode = serviceDefinition.getCode();
        this.attributes = mapAttributes(serviceDefinition.getAttributes());
    }

    private List<AttributeDefinition> mapAttributes(
        List<org.egov.pgrrest.common.domain.model.AttributeDefinition> attributes) {
        if (attributes == null) {
            return Collections.emptyList();
        }
        return attributes.stream()
            .map(AttributeDefinition::new)
            .collect(Collectors.toList());
    }

}

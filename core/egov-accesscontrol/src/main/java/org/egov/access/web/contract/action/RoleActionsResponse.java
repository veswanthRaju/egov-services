package org.egov.access.web.contract.action;

import java.util.List;

import org.egov.access.domain.model.RoleAction;
import org.egov.common.contract.response.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleActionsResponse {

	private ResponseInfo responseInfo;
    private List<RoleAction> roleActions;
	
}

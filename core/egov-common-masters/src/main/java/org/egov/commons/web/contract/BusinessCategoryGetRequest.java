package org.egov.commons.web.contract;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class BusinessCategoryGetRequest {
	private String businessCategoryName;

	private Boolean active;

	private List<Long> ids;

	@NotNull
	private String tenantId;

	private String sortBy;

	private String sortOrder;
}

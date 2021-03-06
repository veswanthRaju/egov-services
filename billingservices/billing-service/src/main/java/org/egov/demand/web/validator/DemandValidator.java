/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.demand.web.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.egov.demand.model.BusinessServiceDetail;
import org.egov.demand.model.Demand;
import org.egov.demand.model.DemandDetail;
import org.egov.demand.model.Owner;
import org.egov.demand.model.TaxHeadMaster;
import org.egov.demand.model.TaxHeadMasterCriteria;
import org.egov.demand.repository.OwnerRepository;
import org.egov.demand.service.BusinessServDetailService;
import org.egov.demand.service.TaxHeadMasterService;
import org.egov.demand.web.contract.BusinessServiceDetailCriteria;
import org.egov.demand.web.contract.DemandRequest;
import org.egov.demand.web.contract.UserSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class DemandValidator implements Validator {

	@Autowired
	private TaxHeadMasterService taxHeadMasterService;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private BusinessServDetailService businessServDetailService;

	@Override
	public boolean supports(Class<?> clazz) {

		return DemandRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		DemandRequest demandRequest = null;
		if (target instanceof DemandRequest)
			demandRequest = (DemandRequest) target;
		else
			throw new RuntimeException("Invalid Object type for Demand validator");
		validateDemand(demandRequest, errors);
		validateTaxHeadMaster(demandRequest, errors);
		validateBusinessService(demandRequest, errors);
		validateTaxPeriod(demandRequest, errors);
		validateOwner(demandRequest, errors);
	}

	private void validateDemand(DemandRequest demandRequest, Errors errors) {
		
		for(Demand demand : demandRequest.getDemands())
			for(DemandDetail demandDetail : demand.getDemandDetails()){
				
				BigDecimal tax = demandDetail.getTaxAmount();
				BigDecimal collection = demandDetail.getCollectionAmount();
				int i = tax.compareTo(collection);
				if(i < 0)
					errors.rejectValue("Demands", "",
							"collectionAmount : "+collection+" should not be greater than taxAmount : "+tax+" for demandDetail");
			}
		/*demandDetails.stream().filter(demandDetail -> {
			
			BigDecimal tax = demandDetail.getTaxAmount();
			BigDecimal collection = demandDetail.getCollectionAmount();
			int i = tax.compareTo(collection);
			System.err.println("compare to value" + i);
			if(i < 0)
				errors.rejectValue("Demands", "",
						"collectionAmount : "+collection+" should not be greater than taxAmount : "+tax+" for demandDetail");
			else
				System.err.println("hi");
			return true;
		});*/
	}

	private void validateTaxPeriod(DemandRequest demandRequest, Errors errors) {
		
	}

	private void validateBusinessService(DemandRequest demandRequest, Errors errors) {

		Set<String> businessServiceSet = demandRequest.getDemands().stream().map(demand -> demand.getBusinessService())
				.collect(Collectors.toSet());
		BusinessServiceDetailCriteria businessServiceDetailCriteria = BusinessServiceDetailCriteria.builder()
				.tenantId(demandRequest.getDemands().get(0).getTenantId()).businessService(businessServiceSet).build();
		List<BusinessServiceDetail> businessServiceDetails = businessServDetailService
				.searchBusinessServiceDetails(businessServiceDetailCriteria, demandRequest.getRequestInfo())
				.getBusinessServiceDetails();
		if (businessServiceDetails.isEmpty())
			errors.rejectValue("Demands", "",
					"no businessService is found for value of Demand.businessService, please give a valid businessService code");
		else {
			Map<String, BusinessServiceDetail> map = businessServiceDetails.stream()
					.collect(Collectors.toMap(BusinessServiceDetail::getBusinessService, Function.identity()));
			for (String businessService : businessServiceSet) {
				if (map.get(businessService) == null)
					errors.rejectValue("Demands", "", "the given businessService value '" + businessService
							+ "'of Demand.businessService is invalid, please give a valid businessService code");
			}
		}
	}

	private void validateTaxHeadMaster(DemandRequest demandRequest, Errors errors) {

		Map<String, Demand> codeDemandMap = new HashMap<>();
		for (Demand demand : demandRequest.getDemands()) {
			for (DemandDetail demandDetail : demand.getDemandDetails()) {
				codeDemandMap.put(demandDetail.getTaxHeadMasterCode(), demand);
			}
		}
		TaxHeadMasterCriteria taxHeadMasterCriteria = TaxHeadMasterCriteria.builder()
				.tenantId(demandRequest.getDemands().get(0).getTenantId()).code(codeDemandMap.keySet()).build();
		List<TaxHeadMaster> taxHeadMasters = taxHeadMasterService
				.getTaxHeads(taxHeadMasterCriteria, demandRequest.getRequestInfo()).getTaxHeadMasters();
		if (taxHeadMasters.isEmpty())
			errors.rejectValue("Demands", "",
					"no taxheadmasters found for the given code value DemandDetail.code, please give a valid code");
		else {
			Map<String, List<TaxHeadMaster>> taxHeadMap = taxHeadMasters.stream()
					.collect(Collectors.groupingBy(TaxHeadMaster::getCode, Collectors.toList()));
			for (String code : codeDemandMap.keySet()) {
				Demand demand = codeDemandMap.get(code);
				if (taxHeadMap.get(code) == null)
					errors.rejectValue("Demands", "", "the given code value '" + code
							+ "'of teaxheadmaster in DemandDetail.code is invalid, please give a valid code");
				else {
					TaxHeadMaster taxHeadMaster = taxHeadMasters.stream()
							.filter(t -> demand.getTaxPeriodFrom().compareTo(t.getValidFrom()) >= 0
									&& demand.getTaxPeriodTo().compareTo(t.getValidTill()) <= 0)
							.findAny().orElse(null);
					if (taxHeadMaster == null)
						errors.rejectValue("Demands", "", "the given code value '" + code
								+ "'of teaxheadmaster in DemandDetail.code is invalid, please give a valid code");
				}
			}
		}
	}

	private void validateOwner(DemandRequest demandRequest, Errors errors) {

		List<Demand> demands = demandRequest.getDemands();
		List<Long> ownerIds = new ArrayList<>(
				demands.stream().map(demand -> demand.getOwner().getId()).collect(Collectors.toSet()));
		UserSearchRequest userSearchRequest = UserSearchRequest.builder().requestInfo(demandRequest.getRequestInfo())
				.id(ownerIds).tenantId(demands.get(0).getTenantId()).build();
		Map<Long, Long> ownerMap = new HashMap<>();
		List<Owner> owners = ownerRepository.getOwners(userSearchRequest);
		if (owners.isEmpty())
			errors.rejectValue("demands", "",
					"no user found for the given criteria in Owner.id, please give a valid user id");
		else {
			for (Owner owner : owners) {
				ownerMap.put(owner.getId(), owner.getId());
			}
			for (Long rsId : ownerIds) {
				if (ownerMap.get(rsId) == null)
					errors.rejectValue("demands", "", "the given user id value '" + rsId
							+ "' in Owner.id is invalid, please give a valid user id");
			}
		}
	}

}

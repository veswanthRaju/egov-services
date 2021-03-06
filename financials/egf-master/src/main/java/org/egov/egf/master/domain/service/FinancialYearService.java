package org.egov.egf.master.domain.service;

import java.util.List;

import org.egov.common.domain.exception.CustomBindException;
import org.egov.common.domain.model.Pagination;
import org.egov.common.web.contract.CommonRequest;
import org.egov.egf.master.domain.model.FinancialYear;
import org.egov.egf.master.domain.model.FinancialYearSearch;
import org.egov.egf.master.domain.repository.FinancialYearRepository;
import org.egov.egf.master.web.contract.FinancialYearContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;

@Service
@Transactional(readOnly = true)
public class FinancialYearService {

	public static final String ACTION_CREATE = "create";
	public static final String ACTION_UPDATE = "update";
	public static final String ACTION_VIEW = "view";
	public static final String ACTION_EDIT = "edit";
	public static final String ACTION_SEARCH = "search";

	@Autowired
	private FinancialYearRepository financialYearRepository;

	@Autowired
	private SmartValidator validator;

	public BindingResult validate(List<FinancialYear> financialyears, String method, BindingResult errors) {

		try {
			switch (method) {
			case ACTION_VIEW:
				// validator.validate(financialYearContractRequest.getFinancialYear(),
				// errors);
				break;
			case ACTION_CREATE:
				Assert.notNull(financialyears, "FinancialYears to create must not be null");
				for (FinancialYear financialYear : financialyears) {
					validator.validate(financialYear, errors);
				}
				break;
			case ACTION_UPDATE:
				Assert.notNull(financialyears, "FinancialYears to update must not be null");
				for (FinancialYear financialYear : financialyears) {
					validator.validate(financialYear, errors);
				}
				break;
			default:

			}
		} catch (IllegalArgumentException e) {
			errors.addError(new ObjectError("Missing data", e.getMessage()));
		}
		return errors;

	}

	public List<FinancialYear> fetchRelated(List<FinancialYear> financialyears) {
		for (FinancialYear financialYear : financialyears) {
			// fetch related items

		}

		return financialyears;
	}

	public List<FinancialYear> add(List<FinancialYear> financialyears, BindingResult errors) {
		financialyears = fetchRelated(financialyears);
		validate(financialyears, ACTION_CREATE, errors);
		if (errors.hasErrors()) {
			throw new CustomBindException(errors);
		}
		return financialyears;

	}

	public List<FinancialYear> update(List<FinancialYear> financialyears, BindingResult errors) {
		financialyears = fetchRelated(financialyears);
		validate(financialyears, ACTION_UPDATE, errors);
		if (errors.hasErrors()) {
			throw new CustomBindException(errors);
		}
		return financialyears;

	}

	public void addToQue(CommonRequest<FinancialYearContract> request) {
		financialYearRepository.add(request);
	}

	public Pagination<FinancialYear> search(FinancialYearSearch financialYearSearch) {
		return financialYearRepository.search(financialYearSearch);
	}

}
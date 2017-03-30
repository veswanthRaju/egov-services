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

package org.egov.eis.web.validator;

import java.util.ArrayList;
import java.util.List;

import org.egov.eis.model.Assignment;
import org.egov.eis.model.DepartmentalTest;
import org.egov.eis.model.EducationalQualification;
import org.egov.eis.model.Employee;
import org.egov.eis.model.Probation;
import org.egov.eis.model.Regularisation;
import org.egov.eis.model.ServiceHistory;
import org.egov.eis.model.TechnicalQualification;
import org.egov.eis.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class DataIntegrityValidator implements Validator {

	@Autowired
	private EmployeeService employeeService;

	/**
	 * This Validator validates *just* Employee instances
	 */
	@Override
	public boolean supports(Class<?> paramClass) {
		return Employee.class.equals(paramClass);
	}

	@Override
	public void validate(Object targetObject, Errors errors) {
		if (!(targetObject instanceof Employee))
			return;

		Employee employee = (Employee) targetObject;

		if (employee.getRetirementAge() != null && employee.getRetirementAge() > 100)
			errors.rejectValue("employee.retirementAge", "invalid", "Invalid retirementAge");

		if ((employee.getDateOfAppointment() != null && employee.getDateOfJoining() != null)
				&& (employee.getDateOfAppointment().after(employee.getDateOfJoining()))) {
			errors.rejectValue("employee.dateOfAppointment", "invalid", "Invalid dateOfAppointment");
			errors.rejectValue("employee.dateOfJoining", "invalid", "Invalid dateOfJoining");
		}
		if ((employee.getDateOfResignation() != null && employee.getDateOfJoining() != null)
				&& (employee.getDateOfResignation().before(employee.getDateOfJoining()))) {
			errors.rejectValue("employee.dateOfJoining", "invalid", "Invalid dateOfJoining");
			errors.rejectValue("employee.dateOfResignation", "invalid", "Invalid dateOfResignation");
		}
		if ((employee.getDateOfTermination() != null && employee.getDateOfJoining() != null)
				&& (employee.getDateOfTermination().before(employee.getDateOfJoining()))) {
			errors.rejectValue("employee.dateOfJoining", "invalid", "Invalid dateOfJoining");
			errors.rejectValue("employee.dateOfTermination", "invalid", "Invalid dateOfTermination");
		}
		if ((employee.getDateOfRetirement() != null && employee.getDateOfJoining() != null)
				&& (employee.getDateOfRetirement().before(employee.getDateOfJoining()))) {
			errors.rejectValue("employee.dateOfJoining", "invalid", "Invalid dateOfJoining");
			errors.rejectValue("employee.dateOfRetirement", "invalid", "Invalid dateOfRetirement");
		}

		if ((employee.getPassportNo() != null) && employeeService.getBooleanForDataIntegrityChecks("egeis_employee",
				"passportNo", "'" + employee.getPassportNo() + "'")) {
			errors.rejectValue("employee.passportNo", "concurrent", "passportNo already exists");
		}

		if ((employee.getGpfNo() != null) && employeeService.getBooleanForDataIntegrityChecks("egeis_employee", "gpfNo",
				"'" + employee.getGpfNo() + "'")) {
			errors.rejectValue("employee.gpfNo", "concurrent", "gpfNo already exists");
		}

		if ((employee.getDocuments() != null) && !employee.getDocuments().isEmpty()
				&& employeeService.getBooleanForDataIntegrityChecks("egeis_employeeDocuments", "document",
						getProcessedDocumentsString(employee.getDocuments()))) {
			errors.rejectValue("employee.documents", "concurrent", "document(s) already exists");
		}

		List<Assignment> assignments = employee.getAssignments();
		for (int index = 0; index < assignments.size(); index++) {
			if (assignments.get(index).getDocuments() != null && !assignments.get(index).getDocuments().isEmpty()
					&& employeeService.getBooleanForDataIntegrityChecks("egeis_employeeDocuments", "document",
							getProcessedDocumentsString(assignments.get(index).getDocuments()))) {
				errors.rejectValue("employee.assignments[" + index + "].documents", "concurrent",
						"document(s) already exists");
			}
		}

		List<DepartmentalTest> tests = new ArrayList<>();
		if (tests != null && !tests.isEmpty()) {
			for (int index = 0; index < tests.size(); index++) {
				if (tests.get(index).getDocuments() != null && !tests.get(index).getDocuments().isEmpty()
						&& employeeService.getBooleanForDataIntegrityChecks("egeis_employeeDocuments", "document",
								getProcessedDocumentsString(tests.get(index).getDocuments()))) {
					errors.rejectValue("employee.test[" + index + "].documents", "concurrent",
							"document(s) already exists");
				}
			}
		}

		List<EducationalQualification> educations = new ArrayList<>();
		if (educations != null && !educations.isEmpty()) {
			for (int index = 0; index < educations.size(); index++) {
				if (educations.get(index).getDocuments() != null && !educations.get(index).getDocuments().isEmpty()
						&& employeeService.getBooleanForDataIntegrityChecks("egeis_employeeDocuments", "document",
								getProcessedDocumentsString(educations.get(index).getDocuments()))) {
					System.err.println("Checking");
					errors.rejectValue("employee.education[" + index + "].documents", "concurrent",
							"document(s) already exists");
				}
			}
		}

		List<Probation> probations = new ArrayList<>();
		if (probations != null && !probations.isEmpty()) {
			for (int index = 0; index < probations.size(); index++) {
				if (probations.get(index).getDocuments() != null && !probations.get(index).getDocuments().isEmpty()
						&& employeeService.getBooleanForDataIntegrityChecks("egeis_employeeDocuments", "document",
								getProcessedDocumentsString(probations.get(index).getDocuments()))) {
					errors.rejectValue("employee.probation[" + index + "].documents", "concurrent",
							"document(s) already exists");
				}
			}
		}

		List<Regularisation> regularisations = new ArrayList<>();
		if (regularisations != null && !regularisations.isEmpty()) {
			for (int index = 0; index < regularisations.size(); index++) {
				if (regularisations.get(index).getDocuments() != null
						&& !regularisations.get(index).getDocuments().isEmpty()
						&& employeeService.getBooleanForDataIntegrityChecks("egeis_employeeDocuments", "document",
								getProcessedDocumentsString(regularisations.get(index).getDocuments()))) {
					errors.rejectValue("employee.regularisation[" + index + "].documents", "concurrent",
							"document(s) already exists");
				}
			}
		}

		List<ServiceHistory> histories = new ArrayList<>();
		if (histories != null && !histories.isEmpty()) {
			for (int index = 0; index < histories.size(); index++) {
				if (histories.get(index).getDocuments() != null && !histories.get(index).getDocuments().isEmpty()
						&& employeeService.getBooleanForDataIntegrityChecks("egeis_employeeDocuments", "document",
								getProcessedDocumentsString(histories.get(index).getDocuments()))) {
					errors.rejectValue("employee.history[" + index + "].documents", "concurrent",
							"document(s) already exists");
				}
			}
		}

		List<TechnicalQualification> technicals = new ArrayList<>();
		if (technicals != null && !technicals.isEmpty()) {
			for (int index = 0; index < technicals.size(); index++) {
				if (technicals.get(index).getDocuments() != null && !technicals.get(index).getDocuments().isEmpty()
						&& employeeService.getBooleanForDataIntegrityChecks("egeis_employeeDocuments", "document",
								getProcessedDocumentsString(technicals.get(index).getDocuments()))) {
					errors.rejectValue("employee.technical[" + index + "].documents", "concurrent",
							"document(s) already exists");
				}
			}
		}
	}

	private String getProcessedDocumentsString(List<String> documents) {
		return "'" + String.join("','", documents) + "'";
	}
}
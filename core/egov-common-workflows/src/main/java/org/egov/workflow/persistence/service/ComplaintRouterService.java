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

package org.egov.workflow.persistence.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.workflow.domain.exception.ApplicationRuntimeException;
import org.egov.workflow.domain.exception.EscalationException;
import org.egov.workflow.persistence.entity.ComplaintRouter;
import org.egov.workflow.persistence.repository.BoundaryRepository;
import org.egov.workflow.persistence.repository.ComplaintRouterRepository;
import org.egov.workflow.persistence.repository.ComplaintTypeRepository;
import org.egov.workflow.persistence.repository.EmployeeRepository;
import org.egov.workflow.persistence.repository.PositionHierarchyRepository;
import org.egov.workflow.persistence.repository.PositionRepository;
import org.egov.workflow.web.contract.BoundaryResponse;
import org.egov.workflow.web.contract.ComplaintTypeResponse;
import org.egov.workflow.web.contract.Employee;
import org.egov.workflow.web.contract.Position;
import org.egov.workflow.web.contract.PositionHierarchyResponse;
import org.egov.workflow.web.contract.RequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComplaintRouterService {

	private static final Logger LOG = LoggerFactory.getLogger(ComplaintRouterService.class);

	private final ComplaintRouterRepository complaintRouterRepository;

	private final BoundaryRepository boundaryRepository;

	private final PositionRepository positionRepository;

	private final PositionHierarchyRepository positionHierarchyRepository;

	private final ComplaintTypeRepository complaintTypeRepository;

	private final EmployeeRepository employeeRepository;

	@Autowired
	public ComplaintRouterService(final ComplaintRouterRepository complaintRouterRepository,
			final BoundaryRepository boundaryRepository, final PositionRepository positionRepository,
			final ComplaintTypeRepository complaintTypeRepository, final EmployeeRepository employeeRepository,
			final PositionHierarchyRepository positionHierarchyRepository) {
		this.complaintRouterRepository = complaintRouterRepository;
		this.boundaryRepository = boundaryRepository;
		this.positionRepository = positionRepository;
		this.complaintTypeRepository = complaintTypeRepository;
		this.employeeRepository = employeeRepository;
		this.positionHierarchyRepository = positionHierarchyRepository;
	}

	/**
	 * @param complaint
	 * @return This api takes responsibility of returning suitable position for
	 *         the given complaint Api considers two fields from complaint a.
	 *         complaintType b. Boundary The descision is taken as below 1. If
	 *         complainttype and boundary from complaint is found in router then
	 *         return corresponding position 2. If only complainttype from
	 *         complaint is found search router for matching entry in router and
	 *         return position 3. If no postion found for above then search
	 *         router with only boundary of given complaint and return
	 *         corresponding position 4. If none of the above gets position then
	 *         return GO 5. GO is default for all complaints. It expects the
	 *         data in the following format Say ComplaintType CT1,CT2,CT3
	 *         Present with CT1 locationRequired is true Boundary B1 to B5 are
	 *         child boundaries and B0 is the top boundary (add only child
	 *         boundaries not the top or middle ones) Postion P1 to P10 are
	 *         different positions then ComplaintRouter is populate like this
	 *         ComplaintType Boundary Position
	 *         ===================================================== 1. CT1 B1
	 *         P1 2. CT1 B2 P2 3. CT1 B3 P3 4. CT1 B4 P4 5. CT1 B5 P5 6. CT1
	 *         null P6 This is complaintType default 7. null B5 P7 This is
	 *         Boundary default 8. null B0 P8 This is GO. he is city level
	 *         default. This data is mandatory . Line 6 and 7 are exclusive
	 *         means if 6 present 7 will not be considered . If you want
	 *         boundary level default then dont add complaint type default
	 *         search result complaint is registered with complaint type CT1 and
	 *         boundary B1 will give P1 CT1 and Boundary is not provided will
	 *         give p6, if line 6 not added then it will give P8
	 */
	public Position getAssignee(final Long boundaryId, final String complaintTypeCode, final Long assigneeId,
			RequestInfo requestInfo) {
		Position positionResponse = null;
		Employee employeeResponse = null;
		ComplaintRouter complaintRouter = null;
		List<Position> positions = null;
		List<PositionHierarchyResponse> positionHierarchies = null;
		final List<BoundaryResponse> boundaries = new ArrayList<>();
		if (assigneeId == null) {
			if (null != boundaryId) {
				getParentBoundaries(boundaryId, boundaries);
				if (StringUtils.isNotBlank(complaintTypeCode)) {
					for (final BoundaryResponse bndry : boundaries) {
						final ComplaintTypeResponse complaintType = complaintTypeRepository
								.fetchComplaintTypeByCode(complaintTypeCode);
						complaintRouter = complaintRouterRepository
								.findByComplaintTypeAndBoundary(complaintType.getId(), bndry.getId());
						if (null != complaintRouter)
							break;
					}
					if (null == complaintRouter) {
						final ComplaintTypeResponse complaintType = complaintTypeRepository
								.fetchComplaintTypeByCode(complaintTypeCode);
						complaintRouter = complaintRouterRepository.findByOnlyComplaintType(complaintType.getId());
					}
					if (null == complaintRouter)
						for (final BoundaryResponse bndry : boundaries) {
							complaintRouter = complaintRouterRepository.findByOnlyBoundary(bndry.getId());
							if (null != complaintRouter)
								break;
						}
				}
			} else {
				final ComplaintTypeResponse complaintType = complaintTypeRepository
						.fetchComplaintTypeByCode(complaintTypeCode);
				complaintRouter = complaintRouterRepository.findByOnlyComplaintType(complaintType.getId());
				if (null == complaintRouter)
					complaintRouter = complaintRouterRepository.findCityAdminGrievanceOfficer("ADMINISTRATION");
			}
			if (complaintRouter != null)
				positionResponse = positionRepository.getById(complaintRouter.getPosition(), requestInfo);
			else
				throw new ApplicationRuntimeException("PGR.001");
		} else
			try {
				positionHierarchies = positionHierarchyRepository
						.getByObjectTypeObjectSubTypeAndFromPosition("Complaint", complaintTypeCode, assigneeId);
				if (positionHierarchies.isEmpty() || positionHierarchies.contains(null)) {
					final List<Employee> employees = employeeRepository.getByRoleName("Grievance Routing Officer");
					if (!employees.isEmpty())
						employeeResponse = employees.iterator().next();
					if (employeeResponse != null)
						positions = positionRepository.getByEmployeeId(employeeResponse.getCode(), requestInfo);
					if (!positions.isEmpty())
						positionResponse = positionRepository.getById(positions.iterator().next().getId(), requestInfo);
				} else
					positionResponse = positionHierarchies.iterator().next().getToPosition();
			} catch (final EscalationException e) {
				// Ignoring and logging exception since exception will cause
				// multi city scheduler to fail for all remaining cities.
				LOG.error("An error occurred, escalation can't be completed ", e);
				throw new ApplicationRuntimeException("PGR.001", e);
			}
		return positionResponse;
	}

	public void getParentBoundaries(final Long bndryId, final List<BoundaryResponse> boundaryList) {
		final BoundaryResponse bndry = boundaryRepository.fetchBoundaryById(bndryId);
		if (bndry != null) {
			boundaryList.add(bndry);
			if (bndry.getParent() != null)
				getParentBoundaries(bndry.getParent().getId(), boundaryList);
		}
	}
}

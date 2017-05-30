package org.egov.web.indexer.enricher;

import org.egov.web.indexer.contract.*;
import org.egov.web.indexer.repository.DepartmentRepository;
import org.egov.web.indexer.repository.EmployeeRepository;
import org.egov.web.indexer.repository.contract.ServiceRequestDocument;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static org.apache.commons.lang.StringUtils.isEmpty;

@Service
public class EmployeeDocumentEnricher implements ServiceRequestDocumentEnricher {

    private static final String ASSIGNMENT_ID = "assignmentId";
    private DepartmentRepository departmentRepository;
    private EmployeeRepository employeeRepository;

    public EmployeeDocumentEnricher(DepartmentRepository departmentRepository,
                                    EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public boolean matches(ServiceType serviceType, SevaRequest sevaRequest) {
        return false;
    }

    @Override
    public void enrich(ServiceType serviceType, SevaRequest sevaRequest, ServiceRequestDocument document) {
        final ServiceRequest serviceRequest = sevaRequest.getServiceRequest();
        final Long positionId = getPosition(serviceRequest);
        if (positionId == null) {
            return;
        }
        document.setAssigneeId(positionId);
        setEmployee(document, serviceRequest, positionId);
    }

    private Long getPosition(ServiceRequest serviceRequest) {
        final String assignmentId = serviceRequest.getDynamicSingleValue(ASSIGNMENT_ID);
        if (isEmpty(assignmentId)) {
            return null;
        }
        return Long.valueOf(assignmentId);
    }

    private void setEmployee(ServiceRequestDocument document, ServiceRequest serviceRequest, Long positionId) {
        Employee employee = employeeRepository
            .fetchEmployeeByPositionId(positionId, new LocalDate(), serviceRequest.getTenantId());
        if (employee == null) {
            return;
        }
        document.setAssigneeName(employee.getName());
        setDepartment(document, serviceRequest, employee);
    }

    private void setDepartment(ServiceRequestDocument document, ServiceRequest serviceRequest, Employee employee) {
        if (employee.getAssignments().isEmpty()) {
            return;
        }
        Assignment assignment = employee.getAssignments().get(0);
        DepartmentRes department = departmentRepository
            .getDepartmentById(assignment.getDepartment(), serviceRequest.getTenantId());
        if (department == null || CollectionUtils.isEmpty(department.getDepartment())) {
            return;
        }
        document.setDepartmentName(department.getDepartment().get(0).getName());
        document.setDepartmentCode(department.getDepartment().get(0).getCode());
    }

}
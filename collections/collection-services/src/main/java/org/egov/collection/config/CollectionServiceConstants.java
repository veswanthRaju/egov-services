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
package org.egov.collection.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CollectionServiceConstants {
	
	public static final String INVALID_RECEIPT_REQUEST = "Receipt create request is invalid";
	public static final String INVALID_DEPT_REQUEST = "Get departments request is invalid";
	public static final String INVALID_DESIG_REQUEST = "Get designation request is invalid";
	public static final String INVALID_USERS_REQUEST = "Get users request is invalid";
	public static final String INVALID_WF_REQUEST = "Start Workflow request is invalid";
	public static final String INVALID_REQ_DESC = "Some fields entered are either syntactically or logically incorrect";
	public static final String INVALID_BD = "Fund, FundSource, Function, Department may be invalid.";



	
	public static final String TENANT_ID_MISSING_CODE = "egcl_001";
	public static final String TENANT_ID_MISSING_FIELD = "tenantId";
	public static final String TENANT_ID_MISSING_MESSAGE = "Tenant id is mising";
	
	public static final String PAYEE_NAME_MISSING_CODE = "egcl_002";
	public static final String PAYEE_NAME_MISSING_FIELD = "payeeName";
	public static final String PAYEE_NAME_MISSING_MESSAGE = "Payee Name is mising";
	
	public static final String PAID_BY_MISSING_CODE = "egcl_003";
	public static final String PAID_BY_MISSING_FIELD = "paidBy";
	public static final String PAID_BY_MISSING_MESSAGE = "Paid by id is mising";
	
	public static final String RECEIPT_TYPE_MISSING_CODE = "egcl_004";
	public static final String RECEIPT_TYPE_MISSING_FIELD = "receiptType";
	public static final String RECEIPT_TYPE_MISSING_MESSAGE = "Receipt Type is mising";
	
	public static final String RECEIPT_DATE_MISSING_CODE = "egcl_005";
	public static final String RECEIPT_DATE_MISSING_FIELD = "receiptDate";
	public static final String RECEIPT_DATE_MISSING_MESSAGE = "Receipt Date is mising";
	
	public static final String BD_CODE_MISSING_CODE = "egcl_006";
	public static final String BD_CODE_MISSING_FIELD = "businessDetailsCode";
	public static final String BD_CODE_MISSING_MESSAGE = "Business Details Code is mising";
	
	public static final String COA_MISSING_CODE = "egcl_007";
	public static final String COA_MISSING_FIELD = "glcode";
	public static final String COA_MISSING_MESSAGE = "Chart of Account Code/ GL Code is mising";
	
	public static final String PURPOSE_MISSING_CODE = "egcl_008";
	public static final String PURPOSE_MISSING_FIELD = "purpose";
	public static final String PURPOSE_MISSING_MESSAGE = "Purpose can only be: ARREAR_AMOUNT, CURRENT_AMOUNT, ADVANCE_AMOUNT"
			+ "ARREAR_LATEPAYMENT_CHARGES, CURRENT_LATEPAYMENT_CHARGES, CHEQUE_BOUNCE_PENALTY, REBATE, OTHERS";
	
	public static final String COLLECTIONTYPE_MISSING_CODE = "egcl_009";
	public static final String COLLECTIONTYPE_MISSING_FIELD = "collectionType";
	public static final String COLLECTIONTYPE_MISSING_MESSAGE = "Collection type is mising";
	
	public static final String COLL_ID_NAME = "collection.receiptno";
	public static final String COLL_ID_FORMAT = "GN-CL-[yyyy/MM/dd]-[SEQ_COLL_RCPT_NUM]"; // FIXME: Actual format - [MM/Fin-Yr/6-digit]
	
	
	//URIs for other micro services collection depends on
	public static final String BD_SEARCH_URI = "http://egov-micro-dev.egovernments.org/egov-common-masters/businessDetails/_search";
	public static final String COA_SEARCH_URI = "http://egov-micro-dev.egovernments.org/egf-masters/chartofaccounts/_search";
	public static final String STATUS_SEARCH_URI = "egov-micro-dev.egovernments.org/collection/collectionStatus/_search";
	public static final String ID_GEN_URI = "http://egov-micro-dev.egovernments.org/egov-idgen/id/_generate";
	public static final String GET_DEPT_URI = "http://egov-micro-dev.egovernments.org/egov-common-masters/departments/_search";
	public static final String GET_DESIG_URI = "http://egov-micro-dev.egovernments.org/egov-common-workflows/designations/_search";
	public static final String GET_USERS_URI = "http://egov-micro-dev.egovernments.org/hr-employee/employees/_search";
	public static final String GET_POSITION_URI = "http://egov-micro-dev.egovernments.org/hr-employee/employees/";
	public static final String GET_POSITION_URI_APPEND = "/positions/_search";









}

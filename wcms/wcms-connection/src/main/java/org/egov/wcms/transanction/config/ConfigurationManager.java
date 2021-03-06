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
package org.egov.wcms.transanction.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Configuration
@PropertySource(value = { "classpath:config/application-config.properties" }, ignoreResourceNotFound = true)
@Order(0)
public class ConfigurationManager {


    @Value("${egov.services.workflow_service.startpath}")
    private String workflowServiceStartPath;

    @Value("${egov.services.workflow_service.updatepath}")
    private String workflowServiceUpdatePath;

    @Value("${egov.services.workflow_service.searchpath}")
    private String workflowServiceSearchPath;

    @Value("${egov.services.workflow_service.taskpath}")
    private String workflowServiceTaskPAth;

    @Value("${egov.services.workflow_service.hostname.businesskey}")
    private String workflowServiceBusinessKey;

    @Value("${kafka.topics.start.workflow}")
    private String kafkaStartWorkflowTopic;

    @Value("${kafka.topics.update.workflow}")
    private String kafkaUpdateworkflowTopic;

    @Value("${kafka.topics.save.wcms}")
    private String kafkaSaveWaterConnectionTopic;

    @Value("${kafka.topics.update.wcms}")
    private String kafkaUpdateWaterConnectionTopic;

    @Value("${egov.services.wcms_masters_sourcetype.searchpath}")
    private String waterMasterServiceSourceSearchPathTopic;

    @Value("${egov.services.wcms_masters_supplytype.searchpath}")
    private String waterMasterServiceSupplySearchPathTopic;

    @Value("${egov.services.wcms_masters_pipesize.searchpath}")
    private String waterMasterServicePipesizeSearchPathTopic;

    @Value("${egov.services.wcms_masters_categoryType.searchpath}")
    private String waterMasterServiceCategorySearchPathTopic;

    @Value("${egov.services.wcms_masters_donation.searchpath}")
    private String waterMasterServiceDonationSearchPathTopic;

    @Value("${egov.services.wcms_masters.hostname}")
    private String waterMasterServiceBasePathTopic;

    @Value("${egov.services.id_service.hostname}")
    private String idGenServiceBasePathTopic;

    @Value("${egov.services.id_service.createpath}")
    private String idGenServiceCreatePathTopic;
    @Value("${egov.services.workflow_service.hostname}")
    private String commonworkflowservicebasepath;
    @Value("${id.idName}")
    private String idGenNameServiceTopic;
    
    @Value("${id.format}")
    private String idGenFormatServiceTopic;
    
    @Value("${egov.services.wcms_masters_propertyCategory.searchpath}")
    private String waterMasterPropCategoryMappingTopic;
    
    @Value("${egov.services.wcms_masters_propertyPipeSize.searchpath}")
    private String waterMasterPropPipeSizeMappingTopic;
   
    @Value("${egov.services.wcms_masters_propertyUsageType.searchpath}")
    private String waterMasterPropUsageTypeMappingTopic;
    
    @Value("${egov.services.demandbill_service.hostname}")
    private String billingDemandServiceHostNameTopic;
    
    @Value("${egov.services.demandbill_service.createdemand}")
    private String createbillingDemandServiceTopic;

}

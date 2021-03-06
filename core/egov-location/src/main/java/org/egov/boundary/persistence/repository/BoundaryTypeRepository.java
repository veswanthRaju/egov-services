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

package org.egov.boundary.persistence.repository;

import java.util.List;
import java.util.Set;

import org.egov.boundary.persistence.entity.BoundaryType;
import org.egov.boundary.persistence.entity.HierarchyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoundaryTypeRepository extends JpaRepository<BoundaryType, Long> {

	BoundaryType findByName(String name);

	BoundaryType findByNameContainingIgnoreCase(String name);

	@Query("select bt from BoundaryType bt where bt.hierarchyType.name=:hierarchyName and bt.hierarchy=:hierarchyLevel")
	BoundaryType findByHierarchyTypeNameAndLevel(@Param("hierarchyName") String name,
			@Param("hierarchyLevel") Long hierarchyLevel);

	@Query("select bt from BoundaryType bt where bt.hierarchyType.id=:hierarchyId and bt.tenantId=:tenantId")
	List<BoundaryType> findByHierarchyTypeIdAndTenantId(@Param("hierarchyId") Long id,
			@Param("tenantId") String tenantId);

	@Query("select bt from BoundaryType bt where bt.hierarchyType.name=:hierarchyName and bt.hierarchyType.tenantId=:htTenantId and bt.tenantId=:tenantId ")
	List<BoundaryType> findByHierarchyTypeIdAndTenantName(@Param("hierarchyName") String hierarchyName,
			@Param("htTenantId") String htTenantId, @Param("tenantId") String tenantId);

	@Query("select bt from BoundaryType bt where bt.parent.id=:parentId")
	BoundaryType findByParent(@Param("parentId") Long parentId);

	@Query("select bt from BoundaryType bt where bt.id = :id and bt.hierarchyType.id = :hierarchyId")
	BoundaryType findByIdAndHierarchy(@Param("id") Long id, @Param("hierarchyId") Long hierarchyId);

	BoundaryType findByNameAndHierarchyType(String name, HierarchyType hierarchyType);

	@Query("select bt from BoundaryType bt where bt.name = :boundaryTypeName and bt.hierarchyType.name = :hierarchyTypeName and bt.tenantId = :tenantId")
	BoundaryType findByNameAndHierarchyTypeName(@Param("boundaryTypeName") String name,
												@Param("hierarchyTypeName") String hierarchyTypeName,
												@Param("tenantId") String tenantId);

	@Query("select bt from BoundaryType bt where bt.hierarchyType.name=:name")
	List<BoundaryType> findByHierarchyTypeName(@Param("name") String hierarchyName);

	@Query("select bt from BoundaryType bt where bt.hierarchyType.code in :names and bt.name like 'W%'")
	List<BoundaryType> findByHierarchyTypeNames(@Param("names") final Set<String> names);

	BoundaryType findByTenantIdAndCode(String tenantId, String code);

	BoundaryType findByIdAndTenantId(Long id, String tenantId);

	List<BoundaryType> findAllByTenantId(String tenantId);
}

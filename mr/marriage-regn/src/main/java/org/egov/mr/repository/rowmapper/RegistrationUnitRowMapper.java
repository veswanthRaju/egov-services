package org.egov.mr.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.mr.model.AuditDetails;
import org.egov.mr.model.Location;
import org.egov.mr.model.RegistrationUnit;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class RegistrationUnitRowMapper implements RowMapper<RegistrationUnit> {

	// mapRow will automatically propagate through each record
	public RegistrationUnit mapRow(ResultSet rs, int rowNum) throws SQLException {

		RegistrationUnit registrationUnit = new RegistrationUnit();
		try {
			Location location = new Location();
			location.setDoorNo(rs.getString("doorno"));
			location.setElectionWard(rs.getLong("electionward"));
			location.setPinCode(rs.getInt("pincode"));
			location.setRevenueWard(rs.getLong("revenueward"));
			location.setStreet(rs.getLong("street"));
			location.setBlock(rs.getLong("block"));
			location.setLocality(rs.getLong("locality"));
			location.setZone(rs.getLong("zone"));

			AuditDetails auditDetails = new AuditDetails();
			auditDetails.setCreatedBy(rs.getString("createdBy"));
			auditDetails.setLastModifiedBy(rs.getString("lastModifiedBy"));
			auditDetails.setCreatedTime(rs.getLong("createdTime"));
			auditDetails.setLastModifiedTime(rs.getLong("lastModifiedTime"));

			registrationUnit.setId(rs.getLong("id"));
			registrationUnit.setName(rs.getString("name"));
			registrationUnit.setIsActive(rs.getBoolean("isactive"));
			// Setting location in address parameter
			registrationUnit.setAddress(location);
			registrationUnit.setTenantId(rs.getString("tenantid"));
			// Setting AuditDetails
			registrationUnit.setAuditDetails(auditDetails);
			registrationUnit.setCode(rs.getString("code"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return registrationUnit;
	}
}

package org.egov.property.repository.builder;

public class FloorBuilder {

	public static final String INSERT_FLOOR_QUERY = "INSERT INTO egpt_floors ("
			+ "floorNo,createdBy, lastModifiedBy, createdTime, lastModifiedTime, propertydetails)"
			+ "VALUES(?,?,?,?,?,?)";

	public static final String FLOORS_BY_PROPERTY_DETAILS_QUERY = "select * from egpt_floors where propertydetails "
			+ "= ?";

	public static String updateFloorQuery() {

        StringBuffer floorUpdateSQL = new StringBuffer();

        floorUpdateSQL.append("UPDATE egpt_floors")
        .append(" SET floorNo = ?, lastModifiedBy = ?,")
        .append(" lastModifiedTime = ?, propertydetails = ?")
        .append(" WHERE id = ?" );

        return floorUpdateSQL.toString();
    }

}

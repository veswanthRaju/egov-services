package org.egov.calculator.repository.builder;

/**
 * 
 * @author Yosodhara P This Class will have all the queries which are used in the taxrates API's
 *
 */
public class TaxRatesBuilder {

    public static final String INSERT_TAXRATES_QUERY = "INSERT INTO egpt_mstr_taxrates"
            + " (tenantid, taxHead, dependentTaxHead, fromdate, todate,"
            + " fromValue, toValue, ratePercentage, taxFlatValue, createdby,"
            + " lastmodifiedby, createdtime, lastmodifiedtime)"
            + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static final String UPDATE_TAXRATES_QUERY = "UPDATE egpt_mstr_taxrates"
            + " SET tenantid = ?, taxHead = ?, dependentTaxHead = ?, fromdate = ?,"
            + " todate = ?, fromValue = ?, toValue = ?, ratePercentage = ?,"
            + " taxFlatValue = ?, lastmodifiedby = ?, lastmodifiedtime = ?"
            + " WHERE id = ?";

    public static String getTaxRatesSearchQuery(String tenantId, String taxHead, String validDate,
            Double validARVAmount, String parentTaxHead) {

        StringBuffer selectQuery = new StringBuffer();
        selectQuery.append("SELECT * FROM egpt_mstr_taxrates").append(" WHERE tenantId = '" + tenantId + "'")
                .append(" AND taxHead = '" + taxHead + "'")
                .append(" AND '" + validDate + "' BETWEEN fromdate AND  todate")
                .append(" AND " + validARVAmount + " BETWEEN fromValue AND toValue");

        if (parentTaxHead != null && !parentTaxHead.isEmpty())
            selectQuery.append(" AND dependentTaxHead = '" + parentTaxHead + "'");

        return selectQuery.toString();
    }

    public static String getTaxRatesByTenantAndDate(String tenantId, String validDate) {

        String selectQuery = "SELECT * FROM egpt_mstr_taxrates "
                + " WHERE tenantId = '" + tenantId + "'" + " AND '" + validDate
                + "' BETWEEN fromdate AND  todate";
        return selectQuery;
    }
}

package org.egov.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
/**
 * meta information about the report
 */


public class ReportMetadata   {
  @JsonProperty("reportName")
  private String reportName = null;
  
  @JsonProperty("summary")
  private String summary = null;

  public String getSummary() {
	return summary;
}

public void setSummary(String summary) {
	this.summary = summary;
}

@JsonProperty("reportHeader")
  private List<ColumnDetail> reportHeader = new ArrayList<ColumnDetail>();

  @JsonProperty("searchParams")
  private List<SearchColumn> searchParams = new ArrayList<SearchColumn>();

  public ReportMetadata reportName(String reportName) {
    this.reportName = reportName;
    return this;
  }

   /**
   * name of the report. A tenant specific report can be defined with tenantId as the prefix of the report name. So if the system finds two reports - report1 and mytenant.report1 and the tenantId for this request is mytenant then r eport definition mytenant.report1 will be picked.  Please note that by convention reportname.title and reportname.summary will be teh localization key for the report title and brief description. 
   * @return reportName
  **/
 
  public String getReportName() {
    return reportName;
  }

  public void setReportName(String reportName) {
    this.reportName = reportName;
  }

  public ReportMetadata reportHeader(List<ColumnDetail> reportHeader) {
    this.reportHeader = reportHeader;
    return this;
  }

  public ReportMetadata addReportHeaderItem(ColumnDetail reportHeaderItem) {
    this.reportHeader.add(reportHeaderItem);
    return this;
  }

   /**
   * List of display columns in the report 
   * @return reportHeader
  **/
  
  public List<ColumnDetail> getReportHeader() {
    return reportHeader;
  }

  public void setReportHeader(List<ColumnDetail> reportHeader) {
    this.reportHeader = reportHeader;
  }

  public ReportMetadata searchParams(List<SearchColumn> searchParams) {
    this.searchParams = searchParams;
    return this;
  }

  public ReportMetadata addSearchParamsItem(SearchColumn searchParamsItem) {
    this.searchParams.add(searchParamsItem);
    return this;
  }

   /**
   * array of search parameters to use in report query 
   * @return searchParams
  **/
  
  public List<SearchColumn> getSearchParams() {
    return searchParams;
  }

  public void setSearchParams(List<SearchColumn> searchParams) {
    this.searchParams = searchParams;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReportMetadata reportMetadata = (ReportMetadata) o;
    return Objects.equals(this.reportName, reportMetadata.reportName) &&
        Objects.equals(this.reportHeader, reportMetadata.reportHeader) &&
        Objects.equals(this.searchParams, reportMetadata.searchParams);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reportName, reportHeader, searchParams);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportMetadata {\n");
    
    sb.append("    reportName: ").append(toIndentedString(reportName)).append("\n");
    sb.append("    reportHeader: ").append(toIndentedString(reportHeader)).append("\n");
    sb.append("    searchParams: ").append(toIndentedString(searchParams)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


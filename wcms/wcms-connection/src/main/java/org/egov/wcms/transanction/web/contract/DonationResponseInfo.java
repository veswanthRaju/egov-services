package org.egov.wcms.transanction.web.contract;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DonationResponseInfo {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;
    @JsonProperty("donations")
    private List<DonationResponse> donations;
    
    
    
    
}

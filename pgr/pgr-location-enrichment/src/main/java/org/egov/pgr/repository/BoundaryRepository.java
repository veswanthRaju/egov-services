package org.egov.pgr.repository;

import org.egov.pgr.contract.BoundaryResponse;
import org.egov.pgr.contract.BoundaryServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BoundaryRepository {

    private final String url;
    private RestTemplate restTemplate;

    @Autowired
    public BoundaryRepository(RestTemplate restTemplate,
                              @Value("${egov.services.boundary.host}") String boundaryServiceHost,
                              @Value("${egov.services.boundary.context.fetch_by_lat_lng}") String latLongUrl) {
        this.restTemplate = restTemplate;
        this.url = boundaryServiceHost + latLongUrl;
    }

    public BoundaryResponse findBoundary(String latitude, String longitude) {
        final BoundaryServiceResponse serviceResponse =
                restTemplate.getForObject(this.url, BoundaryServiceResponse.class, latitude, longitude);
        return serviceResponse.getBoundary();
    }

}


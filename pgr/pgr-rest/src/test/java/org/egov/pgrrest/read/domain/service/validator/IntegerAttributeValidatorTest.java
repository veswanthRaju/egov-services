package org.egov.pgrrest.read.domain.service.validator;

import org.egov.pgrrest.common.domain.model.AttributeDataType;
import org.egov.pgrrest.common.domain.model.AttributeDefinition;
import org.egov.pgrrest.common.domain.model.AttributeEntry;
import org.egov.pgrrest.common.domain.model.ServiceDefinition;
import org.egov.pgrrest.read.domain.exception.InvalidIntegerAttributeEntryException;
import org.egov.pgrrest.read.domain.model.ServiceRequest;
import org.egov.pgrrest.read.domain.model.SevaRequestAction;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntegerAttributeValidatorTest {

    @Test
    public void test_should_throw_exception_when_integer_format_is_invalid() {
        final IntegerAttributeValidator validator = new IntegerAttributeValidator();
        final ServiceRequest serviceRequest = mock(ServiceRequest.class);
        when(serviceRequest.getAttributeWithKey("integer1"))
            .thenReturn(new AttributeEntry("integer1", "15.23"));
        final AttributeDefinition attributeDefinition = AttributeDefinition.builder()
            .dataType(AttributeDataType.INTEGER)
            .code("integer1")
            .build();
        final ServiceDefinition serviceDefinition = ServiceDefinition.builder()
            .attributes(Collections.singletonList(attributeDefinition))
            .build();
        try {
            validator.validate(serviceRequest, serviceDefinition, SevaRequestAction.CREATE);
            Assert.fail("Expected exception to be thrown");
        } catch (InvalidIntegerAttributeEntryException ex) {
            assertEquals("integer1", ex.getAttributeCode());
        }
    }

    @Test
    public void test_should_not_throw_exception_when_integer_format_is_valid() {
        final IntegerAttributeValidator validator = new IntegerAttributeValidator();
        final ServiceRequest serviceRequest = mock(ServiceRequest.class);
        when(serviceRequest.getAttributeWithKey("integer1"))
            .thenReturn(new AttributeEntry("integer1", "15"));
        final AttributeDefinition attributeDefinition = AttributeDefinition.builder()
            .dataType(AttributeDataType.INTEGER)
            .code("integer1")
            .build();
        final ServiceDefinition serviceDefinition = ServiceDefinition.builder()
            .attributes(Collections.singletonList(attributeDefinition))
            .build();

        validator.validate(serviceRequest, serviceDefinition, SevaRequestAction.CREATE);
    }


}
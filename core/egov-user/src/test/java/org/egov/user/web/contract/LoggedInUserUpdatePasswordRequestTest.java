package org.egov.user.web.contract;


import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LoggedInUserUpdatePasswordRequestTest {

	@Test
	public void test_should_map_from_contract_to_domain() {
		final User userInfo = User.builder()
				.id(123L)
				.build();
		final RequestInfo requestInfo = RequestInfo.builder()
				.userInfo(userInfo)
				.build();
		final LoggedInUserUpdatePasswordRequest loggedInUserUpdatePasswordRequest = LoggedInUserUpdatePasswordRequest.builder()
				.requestInfo(requestInfo)
				.existingPassword("existingPassword")
				.newPassword("newPassword")
				.build();

		final org.egov.user.domain.model.LoggedInUserUpdatePasswordRequest domain = loggedInUserUpdatePasswordRequest.toDomain();

		assertNotNull(domain);
		assertEquals("existingPassword", domain.getExistingPassword());
		assertEquals("newPassword", domain.getNewPassword());
		assertEquals(Long.valueOf(123), domain.getUserId());
	}

	@Test
	public void test_should_return_user_id_as_null_when_request_info_is_not_present() {
		final LoggedInUserUpdatePasswordRequest loggedInUserUpdatePasswordRequest = LoggedInUserUpdatePasswordRequest.builder()
				.requestInfo(null)
				.existingPassword("existingPassword")
				.newPassword("newPassword")
				.build();

		final org.egov.user.domain.model.LoggedInUserUpdatePasswordRequest domain = loggedInUserUpdatePasswordRequest.toDomain();

		assertNull(domain.getUserId());
	}

	@Test
	public void test_should_return_user_id_as_null_when_user_info_is_not_present() {
		final RequestInfo requestInfo = RequestInfo.builder()
				.userInfo(null)
				.build();
		final LoggedInUserUpdatePasswordRequest loggedInUserUpdatePasswordRequest = LoggedInUserUpdatePasswordRequest.builder()
				.requestInfo(requestInfo)
				.existingPassword("existingPassword")
				.newPassword("newPassword")
				.build();

		final org.egov.user.domain.model.LoggedInUserUpdatePasswordRequest domain = loggedInUserUpdatePasswordRequest.toDomain();

		assertNull(domain.getUserId());
	}
}
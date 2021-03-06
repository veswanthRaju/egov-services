package org.egov.lams.notification.service;

import java.text.MessageFormat;

import org.egov.lams.notification.config.PropertiesManager;
import org.egov.lams.notification.model.Agreement;
import org.egov.lams.notification.model.Allottee;
import org.egov.lams.notification.model.Asset;
import org.egov.lams.notification.model.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService {

	@Autowired
	private PropertiesManager propertiesManager;

	public String getSmsMessage(Agreement agreement, Asset asset, Allottee allottee, City city) {

		Double totalAmount = agreement.getSecurityDeposit() + agreement.getBankGuaranteeAmount();
		String message = MessageFormat.format(propertiesManager.getNotificationMessage(),
				allottee.getName(), asset.getCategory().getName(), asset.getName(),
				agreement.getAcknowledgementNumber(), agreement.getRent(), agreement.getSecurityDeposit(),
				agreement.getBankGuaranteeAmount(), totalAmount,
				city.getLocalName());
		return message;
	}

	public String getApprovalMessage(Agreement agreement, Asset asset, Allottee allottee, City city) {

		String message = MessageFormat.format(propertiesManager.getApproveMessage(), allottee.getName(),
				asset.getCategory().getName(), asset.getName(),
				agreement.getAgreementNumber(), agreement.getRent(),
				city.getLocalName());
		return message;
	}

	public String getRejectedMessage(Agreement agreement, Asset asset, Allottee allottee, City city) {

		String message = MessageFormat.format(propertiesManager.getRejectMessage(), allottee.getName(),
				asset.getCategory().getName(), asset.getName(),
				agreement.getAcknowledgementNumber(), city.getLocalName());
		return message;
	}
}

package org.egov.egf.master.persistence.queue;

import java.util.Map;

import org.egov.common.web.contract.CommonRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class FinancialProducer {

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	public void sendMessage(String topic, String key, Map<String, CommonRequest<?>> message) {

		ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);

		// Handle success or failure of sending
		future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
			@Override
			public void onSuccess(SendResult<String, Object> stringTSendResult) {
			}

			@Override
			public void onFailure(Throwable throwable) {
			}
		});
	}

}

package org.egov.property.consumer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.egov.models.PropertyRequest;
import org.egov.property.services.PersisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Consumer class will use for listing property object from kafka server to
 * insert data in postgres database
 * 
 * @author: S Anilkumar
 */
@Service
public class PropertyConsumer {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Environment environment;

	@Autowired
	KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	PersisterService persisterService;

	/**
	 * This method for getting consumer configuration bean
	 */
	@Bean
	public Map<String, Object> consumerConfig() {
		Map<String, Object> consumerProperties = new HashMap<String, Object>();
		consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
				environment.getProperty("auto.offset.reset.config"));
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				environment.getProperty("kafka.config.bootstrap_server_config"));
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "boundary");
		return consumerProperties;
	}

	/**
	 * This method will return the consumer factory bean based on consumer
	 * configuration
	 */
	@Bean
	public ConsumerFactory<String, PropertyRequest> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(),
				new JsonDeserializer<>(PropertyRequest.class));

	}

	/**
	 * This bean will return kafka listner object based on consumer factory
	 */

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, PropertyRequest> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, PropertyRequest> factory = new ConcurrentKafkaListenerContainerFactory<String, PropertyRequest>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

	/**
	 * receive method
	 * 
	 * @param PropertyRequest
	 *            This method is listened whenever property is created and
	 *            updated
	 */
	@KafkaListener(topics = { "#{environment.getProperty('egov.propertytax.property.create.workflow.started')}",
			"#{environment.getProperty('egov.propertytax.property.update.workflow.started')}",
			"#{environment.getProperty('egov.propertytax.property.update.workflow.approved')}" })
	public void receive(ConsumerRecord<String, PropertyRequest> consumerRecord) throws Exception {

		if (consumerRecord.topic()
				.equalsIgnoreCase(environment.getProperty("egov.propertytax.property.create.workflow.started"))) {
			persisterService.addProperty(consumerRecord.value().getProperties());
		}

		else {
			persisterService.updateProperty(consumerRecord.value().getProperties());
		}
	}
}
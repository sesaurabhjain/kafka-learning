package kafka.beginners.consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.beginners.KafkaConfigurationConstant;

// Assign and seek is used to read specific partitions of specified topics 
public class KafkaConsumerAssignAndSeek {
	private static Logger logger = LoggerFactory.getLogger(KafkaConsumerAssignAndSeek.class);

	public static void main(String args[]) {
		KafkaConsumerThreadAS kafkaConsumerThread = new KafkaConsumerThreadAS(
				KafkaConfigurationConstant.BOOTSTRAP_SERVERS, KafkaConfigurationConstant.TOPIC_NAME);
		new Thread(kafkaConsumerThread).start();
	}
}

class KafkaConsumerThreadAS implements Runnable {
	private Logger logger = LoggerFactory.getLogger(KafkaConsumerThread.class);
	private KafkaConsumer<String, String> consumer;
	String BOOTSTRAP_SERVERS;
	String TOPIC_NAME;
	// String GROUP_ID_CONFIG;

	KafkaConsumerThreadAS(String bootStrapServer, String topic) {
		this.BOOTSTRAP_SERVERS = bootStrapServer;
		this.TOPIC_NAME = topic;
		// this.GROUP_ID_CONFIG = groupId;

	}

	private Properties getProperties() {
		// Create consumer config
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		// properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
				KafkaConfigurationConstant.OFFSET_RESET_EARLIER);
		return properties;
	}

	@Override
	public void run() {
		logger.info("consumer thread started");
		Properties properties = this.getProperties();
		// Create consumer
		consumer = new KafkaConsumer<String, String>(properties);
		// consumer.subscribe(Arrays.asList(TOPIC_NAME));

		// Assign
		TopicPartition topicPartition = new TopicPartition(TOPIC_NAME, 2);
		consumer.assign(Arrays.asList(topicPartition));
		// seek
		consumer.seek(topicPartition, 31);
		try {
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
				for (ConsumerRecord<String, String> record : records) {
					logger.info("Key: " + record.key() + "and Message: " + record.value());
					logger.info("partition:  " + record.partition() + "and ofset: " + record.offset());
				} // for
			} // while
		} catch (WakeupException wakeupException) {
			logger.info("shuting donw consumer with: " + wakeupException);
		} finally {
			consumer.close();
		}
	}

	public void shotdownKafkaConsumer() {
		logger.info("shotdownKafkaConsumer called");
		consumer.wakeup();
	}
}

package kafka.beginners.consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.beginners.KafkaConfigurationConstant;

public class MyKafkaConsumer {
	Logger logger = LoggerFactory.getLogger(MyKafkaConsumer.class);

	public static void main(String[] args) {
		new MyKafkaConsumer().consumeMessage();
	}
	

	public void consumeMessage() {
		//Create consumer config
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfigurationConstant.BOOTSTRAP_SERVERS);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, KafkaConfigurationConstant.GROUP_ID_CONFIG);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KafkaConfigurationConstant.OFFSET_RESET_EARLIER);
    //  properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
	//	properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
		
		// Create consumer
	   KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
	   consumer.subscribe(Arrays.asList(KafkaConfigurationConstant.TOPIC_NAME));
	   while(true) {
		   
		   ConsumerRecords<String ,String > records = consumer.poll(Duration.ofMillis(1000));
		   for (ConsumerRecord<String, String> record: records) {
                   logger.info("Key: "+ record.key()+ "and Message: "+ record.value());
                   logger.info("partition:  "+ record.partition()+ "and ofset: "+ record.offset());
			
		    }
		   consumer.commitAsync();
	   }
	  // consumer.close();

	}
}

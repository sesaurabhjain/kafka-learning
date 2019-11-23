package kafka.beginners.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.beginners.KafkaConfigurationConstant;

public class MyKafkaProducerCallBack {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(MyKafkaProducerCallBack.class);
		
		KafkaProducer<String , String>  producer = MyKafkaProducerCallBack.getKafkaProducer();
		//send data
		for (int i = 0 ;i<10; i ++) {
		ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic",  "Hello from java"+ i);
		// send data - asynchronous
		producer.send(record, new Callback() {

			@Override
			public void onCompletion(RecordMetadata recMetadata, Exception exception) {
				 if(exception == null) {
					 //record is successfully sent
					 logger.info("Received MeataDat"+ "\n" + 
					    "Topic: " + recMetadata.topic() +"\n " +
					    "Partition: " + recMetadata.partition() +"\n " +	 
					    "Offset: "+ recMetadata.offset()  +"\n " +
					    "TimeStamp: "+ recMetadata.timestamp() 
					   );
					 
				 }else {
                    logger.error("Error while producint data "  + exception );
				 }
				
			}
		});
		}
		producer.flush();
		producer.close();

	}
	public static KafkaProducer<String , String> getKafkaProducer() {
	
		System.out.println("kafka producer code");
		// Create Producer properties
		Properties props = new Properties();
		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfigurationConstant.BOOTSTRAP_SERVERS);
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		// Create  producer
		KafkaProducer<String , String> producer = new KafkaProducer<String, String>(props);
        return  producer;
	}

}

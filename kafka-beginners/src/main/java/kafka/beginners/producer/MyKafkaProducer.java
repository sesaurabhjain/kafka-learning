package kafka.beginners.producer;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.beginners.KafkaConfigurationConstant;

public class MyKafkaProducer {

	Logger logger = LoggerFactory.getLogger(MyKafkaProducerWithKeys.class);
	public static void main(String[] args) {
		new MyKafkaProducerWithKeys().produceMessage();

	}
	public void produceMessage() {
		KafkaProducer<String , String>  producer = MyKafkaProducerWithKeys.getKafkaProducer();
		//send data
		String topic = "first_topic";
    	String message = "Hello from java";
    	ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic,  message);
    	
		// send data - asynchronous
		producer.send(record);
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

package kafka.elasticsearch;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.beginners.KafkaConfigurationConstant;

public class ElasticSearchConsumer {
	static Logger logger = LoggerFactory.getLogger(ElasticSearchConsumer.class);

	public static RestHighLevelClient createClient() {
		String hostname = "localhost";
		int port = 9200;
		RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, port, "http"));
		RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);
		return restHighLevelClient;
	}

	public static KafkaConsumer<String, String> createConsumer() {

		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfigurationConstant.BOOTSTRAP_SERVERS);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "kafka-elasticsearch-groupa");
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
		KafkaConfigurationConstant.OFFSET_RESET_EARLIER);
	    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
		
		// Create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
		consumer.subscribe(Arrays.asList("twitter_tweets"));
		return consumer;

	}

	public static void main(String args[]) {
		// logger.info("ElasticSearch Data P");
		// String jsonString = "{\"course\": \"kafka for Beginners\",\"instructor\":
		// \"Stephane Maarek\", \"module\": \"elasticSearch\" }";
		RestHighLevelClient client = createClient();
		// IndexRequest indexRequest = new IndexRequest("twitter").source(jsonString,
		// XContentType.JSON);
		KafkaConsumer<String, String> consumer = createConsumer();

		while (true) {
			//BulkRequest builRequest = new BulkRequest();
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
			for (ConsumerRecord<String, String> record : records) {
				logger.info("Key: " + record.key() + "and Message: " + record.value());
				logger.info("partition:  " + record.partition() + "and ofset: " + record.offset());

				String jsonString = record.value();
				IndexRequest indexRequest = new IndexRequest("twitter").source(jsonString, XContentType.JSON);
				//builRequest.add(indexRequest);
				try {
					IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
					String id = indexResponse.getId();
					logger.info("elastic search record" + id);
				   // Thread.sleep(1000l);
					//	client.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
//			try {
//				BulkResponse bulkResponse = client.bulk(builRequest, RequestOptions.DEFAULT);
//				logger.info(bulkResponse.status().toString());
//				consumer.commitAsync();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
	
		}
		// consumer.close();

	}
}

package kafka.twitter.learning;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import kafka.beginners.KafkaConfigurationConstant;

public class TwitterProducer {
	private Logger logger = LoggerFactory.getLogger(TwitterProducer.class);
	String consumerKey = "AVRWeOE9xxnhbwNp8OiletHMV";
	String consumerSecret = "fZEonlQFvGZT5CHr3r71DDbVbvhUuVqqBjLKSxg04hYaQUypnL";
	String token = "1190147209112608770-c2YLdlWFsvWZAkovSNmv1IMiuDdiJK";
	String secret = "P9yBtvxGOyMh7i4x8MZkZjE9Il1JzBFljJhFu4syoPnFw";

	public TwitterProducer() {

	}

	public static void main(String[] args) {

		new TwitterProducer().run();

	}

	public void run() {
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);

		// Create twitter client
		Client client = createTwitterClient(msgQueue);
		client.connect();
		// Create kafka producer

		KafkaProducer<String, String> producer = createKafkaProducer();

		// Loop to send tweets to Kafka
		while (!client.isDone()) {
			try {
				String msg = msgQueue.poll(5, TimeUnit.SECONDS);
				logger.info("Twitter Message : " + msg);
				if(msg != null) {
				ProducerRecord<String, String> record = new ProducerRecord<String, String>("twitter_tweets", null, msg);
				producer.send(record, new Callback() {
					@Override
					public void onCompletion(RecordMetadata recMetadata, Exception exception) {
						if (exception == null) {
							// record is successfully sent
							logger.info("Received MeataDat" + "\n" + "Topic: " + recMetadata.topic() + "\n "
									+ "Partition: " + recMetadata.partition() + "\n " + "Offset: "
									+ recMetadata.offset() + "\n " + "TimeStamp: " + recMetadata.timestamp());

						} else {
							logger.error("Error while producint data " + exception);
						}

					}
				});
				}
			} catch (Exception e) {
				e.printStackTrace();
				client.stop();
			}
		}

	}

	public KafkaProducer<String, String> createKafkaProducer() {

		// Create Producer properties
		Properties props = new Properties();
		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfigurationConstant.BOOTSTRAP_SERVERS);
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		//Properties for save producer
		props.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		props.setProperty(ProducerConfig.ACKS_CONFIG,"all");
		props.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
		props.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE)); 
		
		//High throwput setting
		props.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");		
		props.setProperty(ProducerConfig.LINGER_MS_CONFIG, Integer.toString(20));
		props.setProperty(ProducerConfig.BATCH_SIZE_CONFIG,Integer.toString(32*1024));
		
		
		
		// Create producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
		return producer;
	}

	public Client createTwitterClient(BlockingQueue<String> msgQueue) {
		/**
		 * Declare the host you want to connect to, the endpoint, and authentication
		 * (basic auth or oauth)
		 */
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		// Optional: set up some followings and track terms
		List<String> terms = Lists.newArrayList("kafka", "bitcoin","usa", "sports", "politics");
		hosebirdEndpoint.trackTerms(terms);

		// These secrets should be read from a config file
		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
		// Creating a client:
		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01") // optional: mainly for the logs
				.hosts(hosebirdHosts).authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));
		Client hosebirdClient = builder.build();
		return hosebirdClient;

	}
}

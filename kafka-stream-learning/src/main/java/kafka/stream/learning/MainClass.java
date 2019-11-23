package kafka.stream.learning;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

public class MainClass {

public static void main(String args[]) {
	 Properties props = new Properties();
     props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-application");
     props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
     props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
     props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
     
     StreamsBuilder builder = new StreamsBuilder();
     KStream<String, String> inputTopicStream = builder.stream("stream-topic-input");
     KStream<String, String> filteredStream = inputTopicStream.filter((K,value) -> {
    	 return value.contains("stream");
     });
     filteredStream.to("stream-topic-output");
     KafkaStreams stream = new KafkaStreams(builder.build(), props);
     stream.start();
}
}

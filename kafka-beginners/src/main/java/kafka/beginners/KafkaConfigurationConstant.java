package kafka.beginners;

public interface KafkaConfigurationConstant {
	    public static String BOOTSTRAP_SERVERS  = "127.0.0.1:9092";
	    public static Integer MESSAGE_COUNT=1000;
	    public static String CLIENT_ID="client1";
	    public static String TOPIC_NAME="first_topic";
	    public static String GROUP_ID_CONFIG="javaConsumerGroup1";
	    public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;
	    public static String OFFSET_RESET_LATEST="latest";
	    public static String OFFSET_RESET_EARLIER="earliest";
	    public static Integer MAX_POLL_RECORDS=1;

}

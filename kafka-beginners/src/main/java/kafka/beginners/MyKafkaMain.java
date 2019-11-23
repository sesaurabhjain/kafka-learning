package kafka.beginners;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyKafkaMain {
	private static Logger logger = LoggerFactory.getLogger(MyKafkaMain.class);

	public static void main(String[] args) {
		logger.info("start");
		CountDownLatch latch = new CountDownLatch(1);
		//new Thread(kafkaConsumerThread).start();
		Runtime.getRuntime().addShutdownHook(new Thread( ()->{
			logger.info("closing kafka consumer");
			//kafkaConsumerThread.shotdownKafkaConsumer();
		}
		));	

		try {
			Thread.sleep(5000);
		//	latch.await();
		} catch (InterruptedException e) {
			logger.error("Application got interrupted");
			e.printStackTrace();
		}finally {
			logger.info("application is closing");
		}
		logger.info("finished");
}

}

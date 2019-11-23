package kafka.elasticsearch;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchConsumer {
	static Logger logger = LoggerFactory.getLogger(ElasticSearchConsumer.class);

	public static RestHighLevelClient createClient() {
		String hostname = "localhost";
		int port = 9200;
		RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, port, "http"));
		RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);
		return restHighLevelClient;
	}

	public static void main(String args[]) {
		// logger.info("ElasticSearch Data P");
		String jsonString = "{\"course\": \"kafka for Beginners\",\"instructor\": \"Stephane Maarek\", \"module\": \"elasticSearch\" }";
		RestHighLevelClient client = createClient();
		IndexRequest indexRequest = new IndexRequest("twitter").source(jsonString, XContentType.JSON);
		try {
			IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
			String id = indexResponse.getId();
			logger.info("elastic search record" + id);
			client.close();
		} catch (IOException e) {
  		   // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

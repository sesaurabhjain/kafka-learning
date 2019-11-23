

Kafka Theory
-------------
 ### Topics 
 * It's is a particular stream of data, similar to a table in DB but with no constraints.
 * You can have as many topics as you need.
 * A topic is identified by it's name
 * Topics are splited in partitions
     * Partition 0 ,   Partition 1,    Partition 2
  * Each partition is ordered
  * Each message within a partition gets an incremental id,  called offset
  * Order is guaranteed only within a partition (not across partitions)
  * Data is kept in partition for only limited time (default is one week)
  * Data is deleted from the partition but offset is keep on increment
  * Once the data is written to a partition, it can't be changed (immutable)
  * data is assigned to a partition unless key is not provided.
   
   ### Broker 
   *  A kafka culster is composed of multiple brokers (servers)
   * Each broker is identified with an ID (integer)
   * Each broker contains cetain topics partitions, 
      * Example of **Topic-A** with 3 partition.
       * Broker 101 -> Topic-A -> Partition 0
       * Broker 102 -> Topic-A -> Partition 1
       * Broker 103 -> Topic-A -> Partition 2 
  * It means when we create a Topic, It's partition can go at any broker
  *  Generally a culster have 3 brokers, but some big cluster have over 100 brokers
  ### Topic Replication
  * Topics should have a replication factor > 1 (usually 2 or 3)
  * This way if a broker is down, another broker can serve the data
  * Exmple: Topic-A with two partition and two replication factor
       * Broker 101 -> Topic-A -> Partition 0
       * Broker 102 -> Topic-A -> Partition 1
       * and respective replicatioin
       * Broker 102 -> Topic-A -> Partition 0
       * Broker 103 -> Topic-A -> Partition 1
  ### Concept of Leader for a partition
  * At any time only one broker can be a leader for a give  partition
  *  only that leader can receive and serve the data for that partition, rest is called ISR (In sync replica)
  * If one broker gets down then  ISR will become the leader for time being till the time broker is not gets up again and sync the data
  
  ### Producers
  * Producers write data to topics (which is made of partitions)
  * Producers automatically know to which broker and partition to write to
  * What if a Broker goes down, Producer automatically will loook for ISR which is reacting as time being leader.
  *  Producer can choose to receive the acknowledgement of data writes.
      1. acks=0: Producer wont wait for acknowledgement (possibility to lose the data)
      2.  ack=1: Producer will wait for leader to acknowledge (limited data lose, if respective broker is down)
      3. acks==all: Leader+replicas acknowlege (no data lose)
 ### Producers : Message key (key is good for log retaintion.policy= compact)
 * if producer sends the data with key (number, string anything ) then data will always go to same partition.
 * if key =null then data can go to any partition
 * **Important**: key can not decide that data will to any specific partition.
 ### Consumers
 * Consumers read data from a topic (identified by name)
*  Data is read in order within the each partition
*  We can not decide which partion to read first
 ### Consumer Groups
 * Multiple consumer within an application is considered a consumer group
 * Each consumer in a group reads from exclusive partition
 * If you have more cunsumer then partition, some consumers wil be inactive
 * So we have to keep as many consumer as the partition are
 * in consumer group we can assign a topic's partition to a consumer
 
### Consumer Offsets
* **Offset** points to the record in a **Kafka** topics's partition with respect to consumer/consumer group  from where consumer start consuming the message.
* When a consumer in a group has processed data received from kafka, It should be committing the  **consumer** Offset.
* Cansumer can also commit the offset once receive the data but what if something goes wrong while processing the data that's why it's not preferred way.
* Process the data first and after sucessful processing commit the **consumer**  offset
* If a consumer dies, it will be able to read back from where it left off. Thanks to the committed consumer offset, this is called delivory sementics

### Consumer delivory sementics

  * enable.auto.commit = ture // default is true
  * enable.auto.commit = false and manually commit the offset by consumer.commitSync()    
  
  
 * At least once : Offset is commited after the message is processed, if something goes wrong in processing then message will be read again.  (Best delivory sementics)
  Note : Make sure your message processing is idempotent. Hint: use upsert for message processing
  ```json
       // Set this property, if auto commit should happen.
        props.put("enable.auto.commit", "true");
      // Make Auto commit interval to a big number so that auto commit does not happen,
      // we are going to control the offset commit via consumer.commitSync(); after processing record.
        props.put("auto.commit.interval.ms", "999999999999");
  ```
 * At most once: Offset will be commited as soon as message is received. If processing goes wrong then message is lost
  ```json
        // Set this property, if auto commit should happen.
        props.put("enable.auto.commit", "true");
       // Auto commit interval is an important property, kafka would commit offset at this interval.
        props.put("auto.commit.interval.ms", "101");
		//do not make call for consumer.commitSync();
  ```
 * Exectly once:  Here we do to commit the offset and read the message by using consumer.seek(topicpartion,offset);
 ```json
   props.put("enable.auto.commit", "false");
   // and do not call consumer.commmitSync()
 ```
### Idempotent consumer
 * use upsert operation
 * use consumer driven id like record.topic()+"_"+record.partion()+"_"+record.offset() //It's unique across the messages

### Kafka Broker Discovery
 * Every kafka broker is also callled **bootstap server**
 *  This means you only need to connect to only one broker and you will be connected to entire cluster
 * Because each broker has metadata info abouot the broker, all the topic and all the partition
 * We (Kafka client) can connect any broker and get the list of all brokers. that's how broker discovery works
### Controller
* Kafka is master less cluster,
* Only first start broker manage rest of the brokers or can say treat like a master in cluster
* if controller broker gets down the another broker will become controller and even previous one will come up  then also controllership will not get
### Zookeeper
* Zookeeper manage all the brokers
* Zookeeper helps to elect the leader partitions, Remember when one broker goes down
* Zeekeer sents notifiction to kafka in many cases like when any broker dies, comesup
* Generally we do mange more then one zookeeper server
* Zookeeper has leader (handle writes), and rest of the zookeeper servers are followers (handle reads)
* **Important** kafka broker cluster is connected by zookeeper cluster
* In one line zookeeper manage mange all borkers (kafa cluster)
### Kafka guarantee
*  Message are append to kafa topic-partition in the order they are sent.
* Cusmers read the messages in the order the are stored in topic-partition
### Kafka guarantee
*  Message are append to kafa topic-partition in the order they are sent.
* Cusmers read the messages in the order the are stored in topic-partition
### Kafka setup
* Binary download, Unzip and set the path as per the OS
* Run Zookeeper first  and then kafka server
  ```sh/bat
    bin/kafka-topics.bat //Just to test successful installation
    zookeeper-server-start.bat config\zookeeper.properties //start zookeeper server
   kafka-server-start.bat config\server.properties // start kafka server
  ``` 
 ### Kafka CLI
### Kafka Topic CLI
```sh
//Most Imp command
$kafka-topics 
//Create a topic named `first_topic` with 3 partition and 1 replication ISR
$kafka-topics --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 1 
//List all available topics.
$kafka-topics --zookeeper 127.0.0.1:2181 --list
$kafka-topics --zookeeper 127.0.0.1:2181 --topic first_topic --describe
//Delete topic
$kafka-topics --zookeeper 127.0.0.1:2181 --topic second_topic --delete
```
### Kafka Console Producer CLI
```sh
 //This tool helps to read data from console and publish it to Kafka
$kafka-console-producer --broker-list localhost:9092 --topic first_topic
> Hello I am learing apache kafka //publish first message
> It's awasome learning experiance  //publish second message
> bye  //publish third message
> ^c //press ctrl+c to quit
```

* If topic does not exist then new topic is created with only 1 partition (as per configuration in server.properties file)
* If we need to change number of partion then update **num.partition = 3** property in config/server.properties file
* Avoid creating topic by publishing data on a topic that does not exist

```
> $kafka-console-producer --broker-list localhost:9092 --topic new_topic --producer-property acks=all
> hello new topic created
> bye
> ^c //press ctrl+c to quit
```
### Kafka Console Consumer CLI
```sh
$kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic //Read the live message not old one
$kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --from-beginning //read all message from the beginning
```
Note : **If we do not sent a group then it creates a new group with random id**
### Kafka Consumers in Group
 Run  below commond more then one time with same group id.  All below consumer will read the topic messages to share the load and also set the cosumer offset so that next time if we read the message with the same group, it will not send already consumed message.

```sh
$kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --group my_first_group
$kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --group my_first_group
```
### kafka-consumer-groups
```sh
$ kafka-consumer-groups --bootstrap-server localhost:9092 --list
>console-consumer-55804
>my_first_group
$kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group my_first_group
Consumer group 'my_first_group' has no active members. //generate below table as output
```
|GROUP          |TOPIC      | PARTITION | CURRENT-OFFSET| LOG-END-OFFSET|LAG|CONSUMER-ID |HOST|CLIENT-ID|
|----------------|------------|---------------|------------------------|------------------------|------|---------------|-----|----|
|my_first_group| first_topic|   0   |      6           |    7           |  1 |    -|   -     |       -    |
|my_first_group| first_topic|   1   |      6           |    7           |  1 |    -|   -     |       -    |
|my_first_group| first_topic|   2   |      7           |    8           |  1 |    -|   -     |       -    |  
Note : **Here there is not active consumer that's why cusumer-id, ost and client id is blank**
### Resettign Offset
```sh
$ kafka-consumer-groups --bootstrap-server localhost:9092 --group my_first_group --reset-offsets --to-earliest --execute --topic first_topic
$ kafka-consumer-groups --bootstrap-server localhost:9092 --group my_first_group --reset-offsets --shift-by 2 --execute --topic first_topic //--shift-by 2 move offset forword, --shift-by -2 move offset backword.
```
### Kafka Tool
Kafka Tool is a GUI application for managing and using **Apache Kafka** clusters. It provides an intuitive UI that allows one to quickly view objects within a Kafka cluster as well as the messages stored in the topics of the cluster. It contains features geared towards both developers and administrators. Some of the key features include
### Kafka Bidirectional capability : 
It means kafa client written by using latest version can read write msg from older version kafka broker and vice-versa.
### Kafka producer configuration
#### Acks messge
* acks =0, No Acknowledgement
* acks = 1 :  Leader acks (default)
* acks = all: meas, Leader and ISR both confirms about write data.
* ack = all must be using with min.insync.replicas
* min.insync.replicas = 2 means min two brokers need to confirm that they have received the data.
* * That means if replication.factor= 3 and acks=all, min.insync.replicas=2, then producer can only tolrate one broker down otherwise it will send the error like not enough replica.
* We can set min.insync.replicas in server.config file
* #### Retries and imdempotent
* `RETRIES_CONFIG`  Setting a value greater than zero will cause the client to resend any record whose send fails. retry will cause of potencial lose of ordering of record.
* To avoid this set`MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION` =1: beacause  number of pararrel request a producer can make, default value is 5
**Note**  note that if this setting is set to be greater than 1 and there are failed sends, there is a risk of  message re-ordering due to retries (i.e., if retries are enabled)."; but if we set 1 the there will be parformance issue :(
#### Idempotent is great solution of abover situation
* just set property `enable.idempotence=true`(producer label):
* Idempotent request holds a req id so that if it fails retry will go with same req key so that broker will understand that it's a retry req not new one and this way we can manage the order of diff req.  Now we set MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION` value from 1 to 5:
#### Compression : 
It's very imp proverty and become more imp when we send the message in batch
```
compression.type = none (default)/gzip/etc
```
#### Batch
* By default producer sends the message to kafka imidiately but
* When number of messages are more then kafka sents the message in batches if we use below configuration
```config
linger.ms = 5 //sends all the message/s after ever 5 mili second
batch.size =16kb // producer will batch the messages untill it's 116KB then producer will send the message batch no matter linger.ms is set what
```
### Buffer size and max.block.ms
What if kafka broker is not able to handle the message the way fast producer is producing,
**Kafka producer buffer the message for specif time after that it will throw the error like broker is down or not able to handle the req etc**
```
$buffer.memory= 32 //the total bytes of memory the producer can use to buffer records waiting to be sent to the server
$max.block.ms = 60*1000// After this time producer.send will throw the error like 
```
-------------------------

### Consumer poll behavior

* Kafka provides poll behavior there are some other enterprise msg sys which provides push behavior also.
* consumer.poll(timeout) // return the data imidiaately if data does not exist then after time out poll will return empty data
* we can do below configurtion to control the poll data like, but default is ok later on we can change as req for optimizaton and max throughput
```json
 properties.put("fetch.minimum.bytes", 100MB); // it reduces the number of call and improve the throughput, default is 50MB
 properties.put("max.poll.reocord", 1000) //by default it's 500, We can increase it if we have lot's of RAM available.
 properties.put("max.partions.fetch.bytes", 1024); // maximum data fetched from each partition
 
```

### Elastic Search by Bonsai
 
 first learn elastic search 
 
### Keafka Extended API
  #### Kafka connect
   `Common Kafka use case`
    1) Source to Kafka : Producer API
	2) kafka to kafka : Kafka Stream API
	3) Kafka to sink : Consumer API
	4) Kafka to App : Consumer API
   `Kakfa connects are implified API for data IN and OUT from kakfa.`
Generally 
* we need to import data into kafka from DataBase, JDBC, MongoDB, Solar etc
* and need to output data into elasticSearch DataBase, MongoDB etc.
If we do all above import/exp from above API, it's bit hard to manage Idempotent, odering, fault tolrance and real world issues.

So many Kafka expert did all above work and expose their work as open source Kafka connect API 
### Running Kafka connect
```sh
 bin/connect-standalone.sh config/connect-standalone.properties connector1.properties [connector2.properties ...]
```
`connect-standalone.properties` holds baisc bootstrap server specific properties.
```json
bootstrap.servers - List of Kafka servers used to bootstrap connections to Kafka
key.converter - Converter class used to convert between Kafka Connect format and the serialized form that is written to Kafka. Examples JSON and Avro.
value.converter - Converter class used to convert between Kafka Connect format and the serialized form that is written to Kafka.
```
`Connector1.properties` : this properties file holds topic and it's config info
```sh

name=local-file-source
connector.class=FileStreamSource
tasks.max=1
file=test.txt
topic=connect-test
```

### Kafka Stream API

Kafka Streams is a client library for building applications and microservices, where the input and output data are stored in Kafka clusters. It combines the simplicity of writing and deploying standard Java and Scala applications on the client side with the benefits of Kafka's server-side cluster technology.
 
* Stream : flow of data
* Stream API tranform (ex filter data : last_mont data) and enrich the data in very fast way
* It directly communicate to kafka cluster 
* We can use strea API for data tranformatoin, enching, froud detection, monitoring and alerting.
* It's a  standard java application, No seprate cluster is req.
* It has exectly one responsibility.
*

Note: We can do this tranformation/enrich work at consumer or producer label but its hard as compare to Steam API because these API are made this this perpuse only and directly integrated to kafka cluster

### Kafka Schema Registry provided by Apache Avro
 Kafka takes bytes as an input and sends bytes as an output. That constraint-free protocol is what makes Kafka powerful
 kafka does not care abt the format at all
 but what about format, if a topic's data format is changed then respective consumer may be breaked.
 Fortunately we can achieve it by shema registry by 
  * Producer sends the schema to SCHEMA REGISTRY Once with respect to topic and
  * Consumer receive the schema  from SCHEMA REGISTRY with respect to topic
  
### Advance Topics

  `Choosing partion count and replication factor`
  `Guideline`: 
     1. Each partition can process few MBs of data and accordingly we can configure the partition but keep in mind about Hardware also
     2. More partitions implies
	    * Better parallelim 
		* Ability to run more consumer in a consumer group
		* Ability to leverage more brokers if you have a large cluster
		* But more partition means more more RAM is req because with respect to each partition kafka keep open a file
		* But More partition more Zookeeper work
		* Dont create a topic having more then 1K partition
		* A kafka cluster can have max 20,000 partion
		* If number of partion is more and if any broker goes then then zookeeper work would be more
    3. Partion per topic is a milion doller question
	    * For small cluster (<6 broker): partition = number of broker*2
		* For medium cluster (6< borker ><12) : can keep two time or equal to number of broker
		* For large cluster (>12 broker): partition = number of broker*1
    4. Creating number of huge partion is also useless
  `Choosing Replication factor`	
    1. Shoud be at least 2, usually 3 at max 4.
	    * Good for reliance of syster (kafka)
        * Higher replication means reduce the throwput, req more memory and disk space
   `Final wordds`
     1. Setup a kafka cluster with 
	       * at least 3 broker
		   * replication factor 3
		   * keep partion 6
		   
		   
### Some Case Study and Interview question

####MovieFlix

Q. Resume Cabapility
A. 
   1.Vedio player call Vedio Position Producer service which store the current vedio position
   2. We we resume the vedio, Vedio Position consumer service call the current position and resume the vedio
   
Q. Recogmandation   
A.    Based many parameter ex their like, what they are currently watching, and analyse and find the similar catagory content to show in recmondation.

#### GetTaxi
Provide the taxi on demand.
Q find the Taxi which is close by driver

Q The priceing should be calculated based on number of driver available in that area and number of req in that area.

Q. All the position data before and during the ride should be stored in an analytics store so that the cost can be computed acurately.

Ans: 
 Taxi position : send taxi position to kafka by each taxi as soon as they connec to system : high volume > 30 partition 
 Store the all user position in kafka which are connecting. Very very high volume > 100 Partition
 By usign kafka stream API store this info into any analytics system (ElasticSearch, Hadoop) and calculate the price and store the price area wise into anyother topic so that user can get taxi price imidiately
 pricing topic is high volume
 
 Remember all the topics hold the data for less time not for default 7 days.

#### MySocialMedia

Drive an application which allow ppl to post images/post and other ppl can like commonets on the same using kafka

provide below features
1) User should be able to post/like/comment
2) user should see total number of like and comments per image/post

create three topic post, like and comments
Use Stream API to agreegating like, post/comments/like with respect to post and can publish the same to new external system,
Use Stream API whcih publish the data to new topic like total_like, total_post

#### MyBank

Req Transaction data alredy exist in DB
Thresholds can be defined by users
Alter must be sent in real time to the user.

Implement above scenario using Kafka
Solution:
Create two topic transaction  and alerting, and threshold
Use Kakfa connect (CDC (Change Data capture) onnector bring real time data from Oracle DB, MySQL, MongoDB etc to kafka topic) for transaction and user specific thresholds
Use Some Stream API service which process the transaction data and put alerting to alerting topic
Create a notification service which consume the notification topic and send the notification to user



#### Logging and Metrices Aggregation

Monitor your logs by using splunk (more similar to ELK but bit proprietry)

## Kafka in the Enterprise for Admin

In Cluster we need to setup multiple broker in different data centers(racks) and at least three zookeeper or 5 or 7 in odd number


Us-east : zookeeper 1 broker 1 broker 4
US-east : zookeeper 2 broker 2 broker 5
US-east : zookeeper 3 broker 3 broker 6

* It's not easy to setup 
* You need to isolate each zookeeper & broker on seprate servers
* Need to implement monitoring.like operation and masterer
* You need a really good Kafka Admin
Note : you can use online apache kafka cloud from diff vendor like Amazon and can avoid all admin work but big company manage their own kafka,

Kafka Monitoring and Operations: 

Need to monitor kafka matrix we can use
1) Confluent Control Centre
2) Kafkatool
3) ELK  (Elastic search and Kibana)

#### Important matrix
1) Number of partitions are have problems with the ISR may indicate a high load on the system.
2) utalization of an apache kafka broker
3) Request timing: How long a req takes to reply
Note : We can have a look at monitoring documentation

### Kafka Security
* producer can send encrypted data and consumer can read and decript the message.
* we can also add user and password/OAUTh for authentication.
* It als support authorization like user-a can only read the topic topic1, user-b can read topic-a and read and write topic-a and topic-b etc

### Kafka Multi Cluster+Replications.

* Kakfa can only operate well in a single regions
* therefor it is very commoon for enterprise to have Kafka clusters across the world, with some level of replication betweek them
* Now question comes how we replicate among the cluster, 
    1. It's simple setup consumer/producer app.
    2. We can use tools for this also like kafka mirror maker
	3. Uber use uReplicator
	4. We can use Kafka connect source
* Remember replication does not preserve the offsets, just data

### Advance Topics Configuration
 #### kafka-configs.bat 
   This tool helps to edit and describe configuration  for a topic, client, user or broker
#### Topic Configuration
Each topic has default values for configurations but as per our req we can change the default and provide our own configuration
 * Replication factor
 * Message Size
 * Number of partition
 * log cleanup policy
 * min.insync.replicas etc
 
 
 ```
 $kafka-config --zookeeper localhost:2181 --entity-type topics --entitiy-name my-cofigured-topic --descripbe
 $kafka-config --zookeeper localhost:2181 --entity-type topics --entitiy-name my-cofigured-topic --add-config min.insync.replicas=2 --alter
 $kafka-config --zookeeper localhost:2181 --entity-type topics --entitiy-name my-cofigured-topic --delete-config min.insync.replicas=2 --alter
 ```
 
  #### Partitions and Segments
   * Topics are made of partitions (we already know) ex first_topic-1, first_topic-2, first_topic-3 actually each partition is a folder which holds 
   * partitions are made of segmetns (files) ex:  .log file, .index. and .timestamp
   * segments holds below files 
       1. .log file which actually holds the message
	   2. .index which allow kafka where to read/wirte message
	   3. .timestamp allows kafka to find message with timestamp
   * Each Segment has startOffset and endOffset
   * At a time only one segment will be active for write operation of that partitions
   * setgment settings: 
       1. segment.byte: size of segmetn in bytes
	   2. segment.ms: after this time kafka will move on to diff segment default is one week
   * if we set log.segment.bites less then number of segment will be more per partition and kafka has to mange more open files which may break the kafka
   * better if we set the segment size which can mange one day data.    
   
 #### Log Cleanup Policies
  
   * Kakka cluster make data expire as per the policy
   * This concept is called cleanup
   1. **Policy** : log.cleanup.policy= delete
          Under this policy data is deleted either by max time or max data
              *  By default messages are deleted  in one week but we can change it
              *  By delete message are not deleted based on max log size (default is -1 = infinite ) but we can change it
              * 
 ```js
 log.retention.hours = 336
 log.retention.bites = 1024000
 ```
    default setting is good
   2. **Policy**: log.clenup.policy = compact (default policy for topic, _consumer_offsets)
                        * In this policy message are not deleted they are compacted 
                        * **Compacted Meaning in Kafka** kafka holds the at least last value for a specif key and compacted messages are stored in snapshot file, compaction only delete old value for a specif key and consumer can still read latest values before delete.retaintion.ms (default 24 hours) is over later on compacted messages are also deleted
                    
 ### Staring Kafka with diff ways
     1) By using Confluent CLI
     2) By using Docker/docker-compose
	 
### Advertise listener : 
 Most imp property when kafka is running at diff cloud

 Kafka broker has three IP, public IP, private IP and ADV_HOST
When kafka client try to connect to broker, broker says to connect from ADV_HOST first and by default ADV_HOST point to localhost (private). 

* so if client is on the same private network, client will connect to kafka server (broker).
* (Q)if client is not on the same private network, client will not be able to connect to kafka server (broker). since ADV_HOST is pointing to private ip
    Solution : Point ADV_HOST to public ip : Now client can connect to kafka broker event when it's not running on the same private network
* (Q) what if public is changed after reboot the machine
    Solution: Use DNS name instead of public IP
	 
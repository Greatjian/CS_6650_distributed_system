import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;


public class KafkaConsumer extends Thread{
  private String topic;

  private KafkaConsumer(String topic) {
    super();
    this.topic=topic;
  }

  @Override
  public void run() {
    ConsumerConnector consumer = createConsumer();
    Map<String,Integer> topicCountMap=new HashMap<String, Integer>();
    topicCountMap.put(topic, 1);

    Map<String, List<KafkaStream<byte[], byte[]>>> MessageStreams = consumer.createMessageStreams(topicCountMap);
    KafkaStream<byte[], byte[]> kafkaStream = MessageStreams.get(topic).get(0);
    ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
    while (iterator.hasNext()) {
      String message = new String(iterator.next().message());
      System.out.println("received："+message);
    }
  }

  public ConsumerConnector createConsumer(){
    Properties properties = new Properties();
    properties.setProperty("zookeeper.connect", "192.168.1.200:2181");
    properties.setProperty("group.id", "group1");
    ConsumerConnector createJavaConsumerConnector = Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
    return createJavaConsumerConnector;
  }

  public static void main(String[] args) {
    new KafkaConsumer("test").start();
  }
}

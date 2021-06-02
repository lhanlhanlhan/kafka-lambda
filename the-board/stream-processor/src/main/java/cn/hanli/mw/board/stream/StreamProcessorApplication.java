package cn.hanli.mw.board.stream;

import cn.hanli.mw.board.stream.utils.PropertyFileReader;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Han Li
 * Created at 2/6/2021 8:42 上午
 * Modified by Han Li at 2/6/2021 8:42 上午
 */
public class StreamProcessorApplication {

    public static void main(String[] args) throws Exception {
        // 读取配置文件
        String property = "spark-local";
        Properties prop = PropertyFileReader.getProperties(property);
        // 创建 Stream 并启动
        new StreamProcessorApplication().start(prop);
    }

    /**
     * 启动 Stream
     * @throws Exception e
     */
    private void start(Properties prop) throws Exception {
        String parquetFile = prop.getProperty("com.iot.app.hdfs") + "board-data-parquet";
        // 获取 Kafka 配置
        Map<String, Object> kafkaConf = getKafkaConf(prop);
    }

    /**
     * 从 Properties 中获取 Kafka 配置
     * @param prop p
     * @return map
     */
    private Map<String, Object> getKafkaConf(Properties prop) {
        Map<String, Object> kafkaProperties = new HashMap<>();
        kafkaProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, prop.getProperty("com.iot.app.kafka.brokerlist"));
        kafkaProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IoTDataDeserializer.class);
        kafkaProperties.put(ConsumerConfig.GROUP_ID_CONFIG, prop.getProperty("com.iot.app.kafka.topic"));
        kafkaProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, prop.getProperty("com.iot.app.kafka.resetType"));
        kafkaProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return kafkaProperties;
    }

}

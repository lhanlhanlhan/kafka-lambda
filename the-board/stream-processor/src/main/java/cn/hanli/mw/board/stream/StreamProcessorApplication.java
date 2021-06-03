package cn.hanli.mw.board.stream;

import cn.hanli.mw.board.stream.callbacks.OnTradeOffsetCommitted;
import cn.hanli.mw.board.stream.decoders.TradeDataDecoder;
import cn.hanli.mw.board.stream.models.TradeData;
import cn.hanli.mw.board.stream.processors.TradeStreamProcessor;
import cn.hanli.mw.board.stream.utils.LatestOffSetReader;
import cn.hanli.mw.board.stream.utils.PropertyFileReader;
import lombok.extern.log4j.Log4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;
import java.util.*;

/**
 * @author Han Li
 * Created at 2/6/2021 8:42 上午
 * Modified by Han Li at 2/6/2021 8:42 上午
 */
@Log4j
public class StreamProcessorApplication {

    public static void main(String[] args) throws Exception {
        // 读取配置文件
        String property = "spark-local";
        // String property = "spark";
        Properties prop = PropertyFileReader.getProperties(property);
        // 创建 Stream 并启动
        new StreamProcessorApplication().start(prop);
    }

    /**
     * 启动 Stream
     * @throws Exception e
     */
    private void start(Properties prop) throws Exception {
        String parquetFile = prop.getProperty("board.app.hdfs") + "board-data-parquet";
        // 1. 获取 Kafka 及 Spark 配置
        Map<String, Object> kafkaConf = getKafkaConf(prop);
        SparkConf sparkConf = getSparkConf(prop);
        // TODO - remove
        sparkConf.set("spark.driver.bindAddress", "127.0.0.1");

        // 2. 新建 StreamingContext
        JavaStreamingContext context = new JavaStreamingContext(sparkConf, Durations.seconds(5));
        context.checkpoint(prop.getProperty("board.app.spark.checkpoint.dir"));
        // 3. 新建 Spark Session，从 parquetFile 中获取最新的 offset
        SparkSession session = SparkSession.builder()
                .config(sparkConf)
                .getOrCreate();
        Map<TopicPartition, Long> newsetOffsets = getOffsets(parquetFile, session);
        // 4. 创建+配置 Kafka 输入源流
        String topicName = prop.getProperty("board.app.kafka.topic");
        JavaInputDStream<ConsumerRecord<String, TradeData>> kafkaStream = getKafkaStream(
                topicName,
                context,
                kafkaConf,
                newsetOffsets
        );
        log.info("Kafka 输入源流已经加载成功");
        // 5. 创建 Stream Processor 并定义算子
        TradeStreamProcessor streamProcessor = new TradeStreamProcessor(kafkaStream);
        streamProcessor
                .appendToHDFS(session, parquetFile)
                .doCity()
                .end();
        // 5.1 定义发送 Ack 的动作
        commitOffset(kafkaStream);
        // 6. 启动 处理机
        context.start();
        context.awaitTermination();
    }

    /**
     * 从 Properties 中获取 Kafka 配置
     * @param prop p
     * @return map
     */
    private Map<String, Object> getKafkaConf(Properties prop) {
        Map<String, Object> kafkaProperties = new HashMap<>();
        // 设置 Broker
        kafkaProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, prop.getProperty("board.app.kafka.brokerlist"));
        // 设置 Topic
        kafkaProperties.put(ConsumerConfig.GROUP_ID_CONFIG, prop.getProperty("board.app.kafka.topic"));
        // ?
        kafkaProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, prop.getProperty("board.app.kafka.resetType"));
        // 反序列化配置
        kafkaProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TradeDataDecoder.class);
        kafkaProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return kafkaProperties;
    }

    /**
     * 从 Properties 中获取 Kafka 配置
     * @param prop 项目配置文件
     * @return Kafka 配置
     */
    private SparkConf getSparkConf(Properties prop) {
        return new SparkConf()
                .setAppName(prop.getProperty("board.app.spark.app.name"))
                .setMaster(prop.getProperty("board.app.spark.master"))
                .set("spark.cassandra.connection.host", prop.getProperty("board.app.cassandra.host"))
                .set("spark.cassandra.connection.port", prop.getProperty("board.app.cassandra.port"))
                .set("spark.cassandra.auth.username", prop.getProperty("board.app.cassandra.username"))
                .set("spark.cassandra.auth.password", prop.getProperty("board.app.cassandra.password"))
                .set("spark.cassandra.connection.keep_alive_ms", prop.getProperty("board.app.cassandra.keep_alive"));
    }


    /**
     * 从 HDFS 中获取 Kafka 所写数据的最新 offset
     * @param parquetFile p
     * @param sparkSession s
     * @return r
     */
    private Map<TopicPartition, Long> getOffsets(final String parquetFile, final SparkSession sparkSession) {
        try {
            LatestOffSetReader latestOffSetReader = new LatestOffSetReader(sparkSession, parquetFile);
            return latestOffSetReader.read().offsets();
        } catch (Exception e) {
            log.warn("不能从 HDFS 读取 Kafka 所写过的数据的最新 offset - parquet 文件真的存在吗？ " + e.getMessage());
            return new HashMap<>();
        }
    }


    /**
     * 获取 Kafka stream
     * @param topic Topic
     * @param streamingContext s
     * @param kafkaProperties k
     * @param fromOffsets f
     * @return r
     */
    private JavaInputDStream<ConsumerRecord<String, TradeData>> getKafkaStream(
            String topic,
            JavaStreamingContext streamingContext,
            Map<String, Object> kafkaProperties,
            Map<TopicPartition, Long> fromOffsets
    ) {
        // 指定 topic (只有 1 个 topic)
        List<String> topicSet = Collections.singletonList(topic);
        if (fromOffsets.isEmpty()) {
            return KafkaUtils.createDirectStream(
                    streamingContext,
                    LocationStrategies.PreferConsistent(),
                    ConsumerStrategies.Subscribe(topicSet, kafkaProperties)
            );
        }
        // 返回 Kafka Stream
        return KafkaUtils.createDirectStream(
                streamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topicSet, kafkaProperties, fromOffsets)
        );
    }


    /**
     * 消费完成后给 Kafka 发送 Ack 消息
     * @param kafkaStream kafka
     */
    private void commitOffset(JavaInputDStream<ConsumerRecord<String, TradeData>> kafkaStream) {
        kafkaStream.foreachRDD(trafficRdd -> {
            if (!trafficRdd.isEmpty()) {
                OffsetRange[] offsetRanges = ((HasOffsetRanges) trafficRdd.rdd()).offsetRanges();
                CanCommitOffsets canCommitOffsets = (CanCommitOffsets) kafkaStream.inputDStream();
                // 把该条经过处理的 RDD commit 掉
                canCommitOffsets.commitAsync(offsetRanges, new OnTradeOffsetCommitted());
            } else {
                log.warn("Empty RDD encountered.");
            }
        });
    }
}

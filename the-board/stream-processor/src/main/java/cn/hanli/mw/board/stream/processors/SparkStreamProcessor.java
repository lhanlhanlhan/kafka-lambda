package cn.hanli.mw.board.stream.processors;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.streaming.api.java.JavaDStream;

/**
 * @author Han Li
 * Created at 2/6/2021 8:53 上午
 * Modified by Han Li at 2/6/2021 8:53 上午
 */
public abstract class SparkStreamProcessor<DataClass> {

    @Getter
    private final JavaDStream<ConsumerRecord<String, DataClass>> srcStream;

    @Getter
    @Setter
    private JavaDStream<DataClass> stream;

    /**
     * 创建流处理器并指定源流
     * @param srcStream 源流 (Kafka 类型)
     */
    public SparkStreamProcessor(JavaDStream<ConsumerRecord<String, DataClass>> srcStream) {
        this.srcStream = srcStream;
        this.prepare();
    }

    /**
     * 将输入流转换为普通流
     */
    protected abstract void prepare();
}

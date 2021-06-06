package cn.hanli.mw.board.stream.processors;

import cn.hanli.mv.board.models.TradeData;
import cn.hanli.mw.board.stream.processors.filters.RealtimeTradeFilter;
import lombok.extern.log4j.Log4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;
import org.apache.spark.streaming.kafka010.OffsetRange;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流处理器
 *
 * @author Han Li
 * Created at 2/6/2021 8:46 上午
 * Modified by Han Li at 2/6/2021 8:46 上午
 */
@Log4j
public class TradeStreamProcessor extends SparkStreamProcessor<TradeData> {

    /**
     * 创建流处理器并指定源流
     *
     * @param srcStream 源流 (Kafka 类型)
     */
    public TradeStreamProcessor(JavaDStream<ConsumerRecord<String, TradeData>> srcStream) {
        super(srcStream);
    }

    /**
     * 将输入流转换为普通流
     */
    @Override
    protected void prepare() {
        JavaDStream<TradeData> stream = this.getSrcStream().transform(item -> {
            // 获取 RDD 中每个 item 的偏移量范围
            OffsetRange[] offsetRanges = ((HasOffsetRanges) item.rdd()).offsetRanges();
            // 根据流中的数据制作 metadata，添加到对应的数据中
            return item.mapPartitionsWithIndex((index, items) -> {
                // 该函数产生流的 iterator，之后会被 Kafka 转换为新流
                List<TradeData> list = new ArrayList<>();
                while (items.hasNext()) {
                    // 对每个 ConsumerRecord 都做以下操作：
                    ConsumerRecord<String, TradeData> next = items.next();
                    TradeData dataItem = next.value();
                    // 制作 metadata
                    Map<String, String> meta = new HashMap<>();
                    LocalDateTime timestamp = dataItem.getTimestamp()
                            .toInstant()
                            .atZone(ZoneId.of("Asia/Hong_Kong"))
                            .toLocalDateTime();
                    meta.put("topic", offsetRanges[index].topic());
                    meta.put("fromOffset", "" + offsetRanges[index].fromOffset());
                    meta.put("kafkaPartition", "" + offsetRanges[index].partition());
                    meta.put("untilOffset", "" + offsetRanges[index].untilOffset());
                    meta.put("dayOfWeek", "" + timestamp.getDayOfWeek().getValue());
                    // 设置数据的 metadata
                    dataItem.setMetadata(meta);
                    list.add(dataItem);
                }
                return list.iterator();
            }, true);
        });
        this.setStream(stream);
    }

    public void end() {}

    /*
     * 流 => Parquet File
     */

    /**
     * 流方法 - 将流中的资料经由 Spark Session 放入 HDFS 中指定名字的 parquet file 中
     *
     * @param dbSession 数据库 session
     * @param parquetFileName parquet 文件
     * @return this
     */
    public TradeStreamProcessor appendToHDFS(final SparkSession dbSession, final String parquetFileName) {
        this.getStream().foreachRDD(rdd -> {
            if (rdd.isEmpty()) {
                return;
            }
            // 将流中的 1 笔数据用 Spark Session 转换成 Spark DataFrame
            Dataset<Row> df = dbSession.createDataFrame(rdd, TradeData.class);
            // 选择要存储的列并改名
            Dataset<Row> dfStore = df.selectExpr(
                    "tradeId", "amount", "timestamp",
                    "deviceType", "payToolType",
                    "city",
                    "metaData.fromOffset as fromOffset",
                    "metaData.untilOffset as untilOffset",
                    "metaData.kafkaPartition as kafkaPartition",
                    "metaData.topic as topic",
                    "metaData.dayOfWeek as dayOfWeek"
            );
            // 保存至 parquet 文件中
            dfStore.printSchema();
            dfStore.write()
                    .partitionBy("topic", "kafkaPartition", "dayOfWeek")
                    .mode(SaveMode.Append)
                    .parquet(parquetFileName);
        });
        return this;
    }

    /*
     * 流 => Cassandra
     */

    /**
     * 流方法 - 按城市统计 (交易笔数；交易金额)
     * @return this
     */
    public TradeStreamProcessor doCity() {
        // 总交易笔数、总交易金额
        RealtimeTradeFilter.processCityTotal(this.getStream());
        return this;
    }
}

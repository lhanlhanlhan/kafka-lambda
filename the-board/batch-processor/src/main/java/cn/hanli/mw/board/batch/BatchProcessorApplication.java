package cn.hanli.mw.board.batch;

import cn.hanli.mv.board.models.TradeData;
import cn.hanli.mw.board.batch.processors.TradeBatchProcessor;
import cn.hanli.mw.board.batch.utils.PropertyFileReader;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.Properties;

/**
 * @author Han Li
 * Created at 5/6/2021 9:15 下午
 * Modified by Han Li at 5/6/2021 9:15 下午
 */
public class BatchProcessorApplication {

    public static void main(String[] args) throws Exception {
        // 读取配置文件
        // String property = "spark-local";
        String property = "spark";
        Properties prop = PropertyFileReader.getProperties(property);
        // 创建 Stream 并启动
        new BatchProcessorApplication().start(prop);
    }

    public void start(Properties prop) {
        String parquetFile = prop.getProperty("board.app.hdfs") + "board-data-parquet";
        // 1. 获取 Spark 配置
        SparkConf conf = getSparkConf(prop);
        // 2. 创建访问 HDFS 的 Spark Session
        SparkSession sparkSession = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();
        // 3. 获取最新 Data Frame, 转为 RDD
        Dataset<Row> df = sparkSession.read().parquet(parquetFile);
        JavaRDD<TradeData> rdd = df.javaRDD()
                .map(row -> {
                    TradeData td = new TradeData();
                    td.setAmount(row.getLong(0));
                    td.setCity(row.getString(1));
                    td.setDeviceType(row.getString(2));
                    td.setPayToolType(row.getString(3));
                    td.setTimestamp(row.getTimestamp(4));
                    td.setTradeId(row.getString(5));
                    return td;
                });
        // 4. 制作数据批处理数据并存储进 Cassandra
        TradeBatchProcessor.doCity(rdd);

        sparkSession.close();
        sparkSession.stop();
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
}

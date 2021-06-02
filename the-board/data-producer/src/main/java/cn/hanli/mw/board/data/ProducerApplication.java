package cn.hanli.mw.board.data;

import cn.hanli.mw.board.data.producers.KafkaProducer;
import cn.hanli.mw.board.data.producers.TradeDataProducer;
import cn.hanli.mw.board.data.utils.PropertyFileReader;
import lombok.extern.log4j.Log4j;

import java.util.Properties;

/**
 * @author Han Li
 * Created at 1/6/2021 10:27 下午
 * Modified by Han Li at 1/6/2021 10:27 下午
 */
@Log4j
public class ProducerApplication {

    public static void main(String[] args) throws InterruptedException {
        Properties properties;
        log.info("正在读取 Kafka 配置文件");
        try {
            properties = PropertyFileReader.getProperties("kafka");
        } catch (Exception e) {
            log.error("读取 Kafka 配置文件出错：" + e.getMessage());
            e.printStackTrace();
            return;
        }
        // Topic
        String topic = properties.getProperty("kafka.topic");
        // 开始生成
        KafkaProducer producer = new TradeDataProducer(properties);
        producer.startProduce(topic);
    }
}

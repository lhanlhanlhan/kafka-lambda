package cn.hanli.mw.board.stream.callbacks;

import lombok.extern.log4j.Log4j;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Han Li
 * Created at 3/6/2021 9:02 上午
 * Modified by Han Li at 3/6/2021 9:02 上午
 */
@Log4j
public class OnTradeOffsetCommitted implements OffsetCommitCallback, Serializable {

    @Override
    public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
        log.info("======== [消息消费完成] ========");
        if (e == null) {
            log.info("以下是 Kafka 发来的消息：\n" + map.toString());
        } else {
            log.warn(e.getMessage());
        }
        log.info("======== [消息消费完成] ========");
    }
}

package cn.hanli.mw.board.data.producers;

/**
 * @author Han Li
 * Created at 1/6/2021 10:38 下午
 * Modified by Han Li at 1/6/2021 10:38 下午
 */
public interface KafkaProducer {

    /**
     * 开始往 topic 生成数据
     * @param topic topic
     */
    void startProduce(String topic) throws InterruptedException;
}

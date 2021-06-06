package cn.hanli.mw.board.data.producers;

import cn.hanli.mv.board.models.TradeData;
import cn.hanli.mw.board.data.utils.Randomise;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.*;

/**
 * @author Han Li
 * Created at 1/6/2021 10:05 下午
 * Modified by Han Li at 1/6/2021 10:05 下午
 */
public class TradeDataProducer implements KafkaProducer {

    private final Producer<String, TradeData> producer;

    public TradeDataProducer(Properties producerProp) {
        ProducerConfig producerConf = new ProducerConfig(producerProp);
        this.producer = new Producer<>(producerConf);
    }

    @Override
    public void startProduce(String topic) throws InterruptedException {
        while (true) {
            // 生成新数据
            TradeData data = new TradeData();
            data.setTradeId(UUID.randomUUID().toString());
            data.setCity(this.genCity());
            data.setAmount(this.genAmount());
            data.setDeviceType(this.genDevice());
            data.setPayToolType(this.genPayTool());
            data.setTimestamp(new Date());

            // 投喂新数据
            producer.send(new KeyedMessage<>(topic, data));

            // 睡觉
            long sleepTime = Randomise.getLong(100, 500);
            Thread.sleep(sleepTime);
        }
    }

    private final List<String> cities = Arrays.asList(
            "厦门", "广州", "北京", "上海", "南宁",
            "深圳", "成都", "杭州", "南京", "重庆",
            "福州", "拉萨", "郑州", "济南", "西安",
            "兰州", "昆明", "福州", "武汉", "长沙"
    );
    private final int[] weights = new int[] {
            2, 5, 7, 7, 2,
            5, 5, 4, 3, 4,
            2, 1, 3, 2, 3,
            1, 2, 2, 4, 3
    };

    /**
     * 不平等随机选择一个城市
     * @return 城市
     */
    private String genCity() {
        return Randomise.selectUnEqually(cities, weights);
    }

    private final long[] amountMinBounds = new long[] {
            0L, 1000L, 5000L, 10000L,
            50000L, 100000L, 500000L, 1000000L
    };

    private final long[] amountMaxBounds = new long[] {
            1000L, 5000L, 10000L, 50000L,
            100000L, 500000L, 1000000L, 5000000L
    };

    private final int[] amountWeights = new int[] {
            29, 22, 16, 11, 7, 4, 2, 1
    };

    private final List<Integer> amountIdx = Arrays.asList(
            0, 1, 2, 3, 4, 5, 6, 7
    );

    /**
     * 不平等随机生成价格
     * @return 价格
     */
    private Long genAmount() {
        Integer amountIdx = Randomise.selectUnEqually(this.amountIdx, this.amountWeights);
        assert amountIdx != null;
        // 生成价格
        long min = this.amountMinBounds[amountIdx];
        long max = this.amountMaxBounds[amountIdx];
        return Randomise.getLong(min, max);
    }

    /*
     * 设备
     */

    private final List<String> deviceList = Arrays.asList(
            "iPhone", "华为", "小米", "OPPO", "VIVO",
            "荣耀", "Android 设备", "电脑端", "平板电脑"
    );

    private final int[] deviceWeight = { 4, 5, 4, 3, 2, 2, 4, 2, 1 };

    /**
     * 不平等随机生成设备
     * @return 设备
     */
    private String genDevice() {
        return Randomise.selectUnEqually(deviceList, deviceWeight);
    }

    /*
     * 支付工具
     */
    private final List<String> payToolList = Arrays.asList(
            "支付宝", "微信支付", "银联卡", "国际信用卡", "云闪付"
    );

    private final int[] payToolWeight = { 5, 6, 3, 1, 2 };

    /**
     * 不平等随机生成支付工具
     * @return 设备
     */
    private String genPayTool() {
        return Randomise.selectUnEqually(payToolList, payToolWeight);
    }
}

package cn.hanli.mw.board.stream.processors.filters;

import cn.hanli.mv.board.models.TradeData;
import cn.hanli.mv.board.models.entities.CityTotalTradeAmount;
import cn.hanli.mv.board.models.entities.CityTotalTradeNumber;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import org.apache.spark.api.java.Optional;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.State;
import org.apache.spark.streaming.StateSpec;
import org.apache.spark.streaming.api.java.JavaDStream;
import scala.Tuple2;

import java.util.Date;
import java.util.HashMap;

import static com.datastax.spark.connector.japi.CassandraStreamingJavaUtil.javaFunctions;

/**
 * Trade 数据流实时分析
 *
 * @author Han Li
 * Created at 2/6/2021 2:51 下午
 * Modified by Han Li at 2/6/2021 2:51 下午
 */
public class RealtimeTradeFilter {


    /**
     * 从流制作实时汇总数据 - 城市，交易笔数、交易金额
     * @param stream 流
     */
    public static void processCityTotal(JavaDStream<TradeData> stream) {
        // 1. 用「状态」来记录每一个【城市】的总数据量或总费用 (维持 1h)
        //    StateSpec<KeyType, ValueType, StateType, MappedType>
        StateSpec<String, Long, Long, Tuple2<String, Long>> citySumStateMapper = StateSpec
                .function((String city, Optional<Long> currValOpt, State<Long> sumState) -> {
                    // 该城市在当前 stream 内的总数据量
                    Long currVal = currValOpt.get();
                    currVal = currVal == null ? 0L : currVal;
                    // 更新统计，把它更新到 state 中
                    Long totalSum = currVal + (sumState.exists() ? sumState.get() : 0L);
                    sumState.update(totalSum);
                    return new Tuple2<>(city, totalSum);
                })
                .timeout(Durations.seconds(3600));

        // 2. 获取每个【城市】的总数据量
        JavaDStream<CityTotalTradeNumber> cityTotalTradeStream = stream
                .mapToPair(tradeData -> new Tuple2<>(tradeData.getCity(), 1L))
                // [(厦门, 1), (南宁, 1), (厦门, 1), ...]
                .reduceByKey(Long::sum)
                // [(厦门, 33), (南宁, 22), (北京, 128), ...]
                .mapWithState(citySumStateMapper)
                // [(厦门, 1453), (南宁, 1133), (北京, 5654), ...]
                .map(cityTradeSum -> {
                    CityTotalTradeNumber totalTrade = new CityTotalTradeNumber();
                    totalTrade.setCity(cityTradeSum._1);
                    totalTrade.setTotalTradeNumber(cityTradeSum._2);
                    totalTrade.setUpdateTime(new Date());
                    return totalTrade;
                });

        // 3. 获取每个【城市】的交易金额
        JavaDStream<CityTotalTradeAmount> cityTotalAmountStream = stream
                .mapToPair(tradeData -> new Tuple2<>(tradeData.getCity(), tradeData.getAmount()))
                .reduceByKey(Long::sum)
                .mapWithState(citySumStateMapper)
                // [(厦门, 1453123123), (南宁, 113131313), (北京, 565444141421), ...]
                .map(cityAmountSum -> {
                    CityTotalTradeAmount totalAmount = new CityTotalTradeAmount();
                    totalAmount.setCity(cityAmountSum._1);
                    totalAmount.setTotalAmount(cityAmountSum._2);
                    totalAmount.setUpdateTime(new Date());
                    return totalAmount;
                });

        // 3. 保存至 Cassandra - 城市交易宗数
        // Java 对象成员名 -> Cassandra 列名
        HashMap<String, String> tradeCountColNames = new HashMap<>();
        tradeCountColNames.put("city", "city");
        tradeCountColNames.put("totalTradeNumber", "number");
        tradeCountColNames.put("updateTime", "updatetime");
        // 写入 Cassandra
        javaFunctions(cityTotalTradeStream).writerBuilder(
                "theboardkeyspace",
                "city_total_trade",
                CassandraJavaUtil.mapToRow(CityTotalTradeNumber.class, tradeCountColNames)
        ).saveToCassandra();

        // 4. 保存至 Cassandra - 城市交易金额
        HashMap<String, String> amountColNames = new HashMap<>();
        amountColNames.put("city", "city");
        amountColNames.put("totalAmount", "amount");
        amountColNames.put("updateTime", "updatetime");
        // 写入 Cassandra
        javaFunctions(cityTotalAmountStream).writerBuilder(
                "theboardkeyspace",
                "city_total_amount",
                CassandraJavaUtil.mapToRow(CityTotalTradeAmount.class, amountColNames)
        ).saveToCassandra();
    }

}

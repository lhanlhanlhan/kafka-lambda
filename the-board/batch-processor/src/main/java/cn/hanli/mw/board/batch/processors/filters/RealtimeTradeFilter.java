package cn.hanli.mw.board.batch.processors.filters;

import cn.hanli.mv.board.models.TradeData;
import cn.hanli.mv.board.models.entities.*;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.Optional;
import scala.Tuple2;

import java.util.Date;
import java.util.HashMap;

/**
 * Trade 数据流实时分析
 *
 * @author Han Li
 * Created at 2/6/2021 2:51 下午
 * Modified by Han Li at 2/6/2021 2:51 下午
 */
public class RealtimeTradeFilter {


    /**
     * 制作实时汇总数据 - 城市，交易笔数、交易金额
     * @param rdd 流
     */
    public static void processCityTotal(JavaRDD<TradeData> rdd) {
        // 1. 不是实时流，因此直接可以获得全体数据，直接硬加起来！
        JavaPairRDD<String, Long> cityTotalTradePair = rdd
                .mapToPair(tradeData -> new Tuple2<>(
                        tradeData.getCity(),
                        1L
                ))
                .reduceByKey(Long::sum);
        JavaPairRDD<String, Long> cityTotalAmountPair = rdd
                .mapToPair(tradeData -> new Tuple2<>(
                        tradeData.getCity(),
                        tradeData.getAmount()
                ))
                .reduceByKey(Long::sum);

        // 2. 获取每个【城市】的总数据量
        JavaRDD<CityTotalTradeNumber> numberJavaRDD = cityTotalTradePair
                .map(cityNumber -> {
                    CityTotalTradeNumber tn = new CityTotalTradeNumber();
                    tn.setCity(cityNumber._1);
                    tn.setTotalTradeNumber(cityNumber._2);
                    tn.setUpdateTime(new Date());
                    return tn;
                });

        JavaRDD<CityTotalTradeAmount> amountJavaRdd = cityTotalAmountPair
                .map(cityNumber -> {
                    CityTotalTradeAmount tn = new CityTotalTradeAmount();
                    tn.setCity(cityNumber._1);
                    tn.setTotalAmount(cityNumber._2);
                    tn.setUpdateTime(new Date());
                    return tn;
                });

        // 3. 保存至 Cassandra - 城市交易宗数
        // Java 对象成员名 -> Cassandra 列名
        HashMap<String, String> tradeCountColNames = new HashMap<>();
        tradeCountColNames.put("city", "city");
        tradeCountColNames.put("totalTradeNumber", "number");
        tradeCountColNames.put("updateTime", "updatetime");
        // 写入 Cassandra
        CassandraJavaUtil.javaFunctions(numberJavaRDD).writerBuilder(
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
        CassandraJavaUtil.javaFunctions(amountJavaRdd).writerBuilder(
                "theboardkeyspace",
                "city_total_amount",
                CassandraJavaUtil.mapToRow(CityTotalTradeAmount.class, amountColNames)
        ).saveToCassandra();
    }
}

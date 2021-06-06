package cn.hanli.mw.board.batch.processors;

import cn.hanli.mv.board.models.TradeData;
import cn.hanli.mw.board.batch.processors.filters.RealtimeTradeFilter;
import lombok.extern.log4j.Log4j;
import org.apache.spark.api.java.JavaRDD;

/**
 * @author Han Li
 * Created at 5/6/2021 9:16 下午
 * Modified by Han Li at 5/6/2021 9:16 下午
 */
@Log4j
public class TradeBatchProcessor {

    public void end() {}

    /*
     * RDD => Cassandra
     */

    /**
     * 批方法 - 按城市统计 (交易笔数；交易金额)
     */
    public static void doCity(JavaRDD<TradeData> rdd) {
        // 总交易笔数、总交易金额
        RealtimeTradeFilter.processCityTotal(rdd);
    }
}

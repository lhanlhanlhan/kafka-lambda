package cn.hanli.mw.board.data.encoders;

import cn.hanli.mw.board.data.models.TradeData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

/**
 * @author Han Li
 * Created at 1/6/2021 10:07 下午
 * Modified by Han Li at 1/6/2021 10:07 下午
 */
@Log4j
@NoArgsConstructor
public class TradeDataEncoder implements Encoder<TradeData> {

    private static final ObjectMapper om = new ObjectMapper();

    public TradeDataEncoder(VerifiableProperties vp) { }

    @Override
    public byte[] toBytes(TradeData tradeData) {
        try {
            String msg = om.writeValueAsString(tradeData);
            log.info(msg);
            return msg.getBytes();
        } catch (JsonProcessingException e) {
            log.error("序列化错误：", e);
        }
        return null;
    }
}

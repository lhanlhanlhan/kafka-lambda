package cn.hanli.mw.board.stream.decoders;

import cn.hanli.mv.board.models.TradeData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * @author Han Li
 * Created at 2/6/2021 5:08 下午
 * Modified by Han Li at 2/6/2021 5:08 下午
 */
public class TradeDataDecoder implements Deserializer<TradeData> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> map, boolean b) { }

    @Override
    public TradeData deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, TradeData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() { }

}

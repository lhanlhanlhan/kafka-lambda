package cn.hanli.mw.board.data.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * 交易数据
 *
 * @author Han Li
 * Created at 1/6/2021 10:06 下午
 * Modified by Han Li at 1/6/2021 10:06 下午
 */
@Getter
@Setter
public class TradeData implements Serializable {
    // 交易单信息
    private final String tradeId;
    private Long amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="HKT")
    private LocalDateTime timestamp;

    // 设备、支付信息
    private String deviceType;
    private String payToolType;

    // 地理位置信息
    private String city;

    public TradeData() {
        this.tradeId = UUID.randomUUID().toString();
    }
}

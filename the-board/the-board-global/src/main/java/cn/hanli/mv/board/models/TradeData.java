package cn.hanli.mv.board.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author Han Li
 * Created at 2/6/2021 8:55 上午
 * Modified by Han Li at 2/6/2021 8:55 上午
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeData extends SparkStreamData {

    // 交易单信息
    private String tradeId;
    private Long amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="HKT")
    private Date timestamp;

    // 设备、支付信息
    private String deviceType;
    private String payToolType;

    // 地理位置信息
    private String city;

}

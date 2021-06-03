package cn.hanli.mw.board.stream.processors.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Cassandra 表 - 城市总交易笔数 "city_total_trade"
 *
 * @author Han Li
 * Created at 2/6/2021 2:56 下午
 * Modified by Han Li at 2/6/2021 2:56 下午
 */
@Getter
@Setter
@NoArgsConstructor
public class CityTotalTradeNumber {
    private String city;
    private Long totalTradeNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="HKT")
    private Date updateTime;
}

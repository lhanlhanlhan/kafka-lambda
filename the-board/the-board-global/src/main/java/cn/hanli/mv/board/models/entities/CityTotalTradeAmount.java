package cn.hanli.mv.board.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Cassandra 表 - 城市总交易金额 "city_total_amount"
 *
 * @author Han Li
 * Created at 2/6/2021 2:56 下午
 * Modified by Han Li at 2/6/2021 2:56 下午
 */
@Getter
@Setter
@NoArgsConstructor
public class CityTotalTradeAmount {
    private String city;
    private Long totalAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="HKT")
    private Date updateTime;
}

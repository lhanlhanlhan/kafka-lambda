package cn.hanli.mw.board.dashboard.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Han Li
 * Created at 3/6/2021 5:12 下午
 * Modified by Han Li at 3/6/2021 5:12 下午
 */
@Data
@Table("city_total_amount")
public class CityTotalAmountData implements Serializable {

    @PrimaryKeyColumn(
            name = "city",
            ordinal = 0,
            type = PrimaryKeyType.PARTITIONED)
    private String city;

    @Column(value = "amount")
    private Long amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="HKT")
    @Column(value = "updatetime")
    private Date timestamp;
}

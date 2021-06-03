package cn.hanli.mw.board.dashboard.utils;

import cn.hanli.mw.board.dashboard.models.CityTotalAmountData;
import cn.hanli.mw.board.dashboard.models.CityTotalTradeData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Han Li
 * Created at 3/6/2021 5:25 下午
 * Modified by Han Li at 3/6/2021 5:25 下午
 */
@Data
public class Response implements Serializable {

    private List<CityTotalAmountData> cityTotalAmount;
    private List<CityTotalTradeData> cityTotalTrade;

}

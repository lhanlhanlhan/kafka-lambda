package cn.hanli.mw.board.dashboard.dao;

import cn.hanli.mw.board.dashboard.models.CityTotalTradeData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

/**
 * @author Han Li
 * Created at 3/6/2021 5:12 下午
 * Modified by Han Li at 3/6/2021 5:12 下午
 */
@Repository
public interface CityTotalTradeRepo
        extends CassandraRepository<CityTotalTradeData, UUID> {

}

package cn.hanli.mw.board.dashboard.services;

import cn.hanli.mw.board.dashboard.dao.CityTotalAmountRepo;
import cn.hanli.mw.board.dashboard.dao.CityTotalTradeRepo;
import cn.hanli.mw.board.dashboard.models.CityTotalAmountData;
import cn.hanli.mw.board.dashboard.models.CityTotalTradeData;
import cn.hanli.mw.board.dashboard.utils.Response;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Han Li
 * Created at 3/6/2021 5:09 下午
 * Modified by Han Li at 3/6/2021 5:09 下午
 */
@Service
@Log4j
public class TradeDataService {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private CityTotalAmountRepo amountRepo;

    @Autowired
    private CityTotalTradeRepo tradeRepo;

    // 标准日期格式
    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 每 5s 推送一次讯息
     */
    @Scheduled(fixedRate = 5000)
    public void trigger() {
        // 从 Cassandra 找数据
        List<CityTotalAmountData> amountDataList = amountRepo.findAll();
        List<CityTotalTradeData> tradeDataList = tradeRepo.findAll();
        // 扔给前端
        Response r = new Response();
        r.setCityTotalAmount(amountDataList);
        r.setCityTotalTrade(tradeDataList);
        if (log.isDebugEnabled()) {
            log.debug("推送数据：" + r);
        }
        // 扔到这个 topic
        this.template.convertAndSend("/topic/the-board", r);
    }
}

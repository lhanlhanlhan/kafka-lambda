package cn.hanli.mw.board.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Han Li
 * Created at 3/6/2021 5:03 下午
 * Modified by Han Li at 3/6/2021 5:03 下午
 */

@SpringBootApplication
@EnableScheduling
// Cassandra 实体包定义
@EnableCassandraRepositories("cn.hanli.mw.board.dashboard.dao")
public class BigBoardApplication {
    public static void main(String[] args) {
        SpringApplication.run(BigBoardApplication.class, args);
    }
}

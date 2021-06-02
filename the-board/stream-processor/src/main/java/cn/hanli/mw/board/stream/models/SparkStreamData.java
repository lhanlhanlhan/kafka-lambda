package cn.hanli.mw.board.stream.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Han Li
 * Created at 2/6/2021 9:16 上午
 * Modified by Han Li at 2/6/2021 9:16 上午
 */
public abstract class SparkStreamData implements Serializable {

    // 流中的元数据
    @Getter @Setter
    private Map<String, String> metadata;

}

package cn.hanli.mw.board.batch.processors;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Han Li
 * Created at 2/6/2021 8:53 上午
 * Modified by Han Li at 2/6/2021 8:53 上午
 */
public abstract class SparkBatchProcessor<DataClass> {

    /**
     * 创建批处理器
     */
    public SparkBatchProcessor() {
        this.prepare();
    }

    /**
     * 将输入流转换为普通流
     */
    protected abstract void prepare();
}

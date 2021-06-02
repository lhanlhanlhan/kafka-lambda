package cn.hanli.mw.board.data.utils;

import java.util.List;
import java.util.Random;

/**
 * @author Han Li
 * Created at 1/6/2021 10:50 下午
 * Modified by Han Li at 1/6/2021 10:50 下午
 */
public class Randomise {

    private static final Random ra = new Random();

    /**
     * 平等选择
     * @param list l
     * @param <T> 种类
     * @return 选择
     */
    public static <T> T selectEqually(List<T> list) {
        int idx = ra.nextInt(list.size());
        return list.get(idx);
    }

    /**
     * 不平等选择
     * @param list l
     * @param <T> 种类
     * @return 选择
     */
    public static <T> T selectUnEqually(List<T> list, int[] weights) {
        if (list.size() != weights.length) {
            return null;
        }
        // weight 求和
        int sumWeights = 0;
        for (int w : weights) {
            sumWeights += w;
        }
        // 选择数字
        int offset = ra.nextInt(sumWeights) + 1;
        for (int idx = 0; idx < weights.length; idx++) {
            if (offset <= weights[idx]) {
                return list.get(idx);
            }
            offset -= weights[idx];
        }
        // 最后一个数字
        return list.get(list.size() - 1);
    }

    /**
     * 获取对应范围内的 Long
     * @param min 范围 1
     * @param max 范围 2
     * @return Long
     */
    public static long getLong(long min, long max) {
        long num = Math.abs(ra.nextLong()) % (max - min);
        return min + num;
    }
}

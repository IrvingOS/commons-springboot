package top.isopen.commons.springboot.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * 抽象策略工厂
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:35
 */
public abstract class AbstractStrategyFactory<T, R> {

    private final Map<T, R> strategyMap = new HashMap<>();

    @SuppressWarnings("unused")
    protected abstract void init();

    protected void addStrategy(T strategy, R processor) {
        strategyMap.put(strategy, processor);
    }

    public R getStrategy(T type) {
        return strategyMap.get(type);
    }

}

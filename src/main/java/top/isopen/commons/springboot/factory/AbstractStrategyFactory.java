package top.isopen.commons.springboot.factory;

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

    protected final Map<T, R> strategyMap = new HashMap<>();

    @SuppressWarnings("unused")
    protected abstract void init();

    public abstract R getStrategy(T type);

}

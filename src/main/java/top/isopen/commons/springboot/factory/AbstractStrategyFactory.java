package top.isopen.commons.springboot.factory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStrategyFactory<T, R> {

    protected final Map<T, R> strategyMap = new HashMap<>();

    @SuppressWarnings("unused")
    protected abstract void init();

    public abstract R getStrategy(T type);

}

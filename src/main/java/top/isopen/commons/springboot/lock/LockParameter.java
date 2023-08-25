package top.isopen.commons.springboot.lock;

import lombok.Value;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import top.isopen.commons.springboot.enums.BaseErrorEnum;
import top.isopen.commons.springboot.lock.annotation.RedLock;
import top.isopen.commons.springboot.lock.annotation.RedLocks;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 锁的参数
 * <p>
 * 用来处理锁注解 {@link RedLock} {@link RedLocks} 中配置的锁参数
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:41
 */
@Value
public class LockParameter {

    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private static final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    private static final String KEY_SEPARATOR = ":";
    private static final char SP_EL_PREFIX = '#';

    String key;
    String[] keys;
    String[] parameters;
    Object[] args;
    boolean fair;
    long leaseTime;
    TimeUnit timeUnit;
    long waitTime;

    LockParameter(RedLock redLock, JoinPoint joinPoint) {
        keys = null;
        parameters = parameterNameDiscoverer.getParameterNames(((MethodSignature) joinPoint.getSignature()).getMethod());
        args = joinPoint.getArgs();
        if (parameters != null && parameters.length != args.length) {
            BaseErrorEnum.INVALID_RED_LOCK_ASPECT_PARAMETER_ERROR.throwException();
        }
        fair = redLock.isFair();
        leaseTime = redLock.leaseTime();
        timeUnit = redLock.timeUnit();
        waitTime = redLock.waitTime();
        key = resolveKey(redLock.key(), parameters, args);
    }

    LockParameter(RedLocks redLocks, JoinPoint joinPoint) {
        key = null;
        parameters = parameterNameDiscoverer.getParameterNames(((MethodSignature) joinPoint.getSignature()).getMethod());
        args = joinPoint.getArgs();
        if (parameters != null && parameters.length != args.length) {
            BaseErrorEnum.INVALID_RED_LOCK_ASPECT_PARAMETER_ERROR.throwException();
        }
        fair = redLocks.isFair();
        leaseTime = redLocks.leaseTime();
        timeUnit = redLocks.timeUnit();
        waitTime = redLocks.waitTime();
        keys = resolveKey(redLocks.keys(), parameters, args);
    }

    /**
     * 解析 {@link RedLock#key()}
     *
     * @param key        {@link RedLock#key()}
     * @param parameters 被 {@link RedLock} 注释的方法的参数项
     * @param args       被 {@link RedLock} 注释的方法的参数值
     * @return {@link String}
     * @author TimeChaser
     * @since 2023/8/25 14:48
     */
    private static String resolveKey(String key, String[] parameters, Object[] args) {
        StandardEvaluationContext context = getElContext(parameters, args);
        return resolveKey(key, context);
    }

    /**
     * 解析 {@link RedLocks#keys()}
     *
     * @param keys       {@link RedLocks#keys()}
     * @param parameters 被 {@link RedLocks} 注释的方法的参数项
     * @param args       被 {@link RedLocks} 注释的方法的参数值
     * @return String[]
     * @author TimeChaser
     * @since 2023/8/25 14:50
     */
    private static String[] resolveKey(String[] keys, String[] parameters, Object[] args) {
        String[] values = new String[keys.length];
        StandardEvaluationContext context = getElContext(parameters, args);

        for (int i = 0; i < keys.length; i++) {
            values[i] = resolveKey(keys[i], context);
        }
        return values;
    }

    /**
     * 通过 EL 表达式上下文解析 key
     * <p>
     * TODO 当前 {@link RedLock#key()} 中有且只能存在一个 EL 表达式，其实是可以通过递归和 try-catch 的组合优化的
     *
     * @param key     锁的 key
     * @param context EL 上下文
     * @return {@link String}
     * @author TimeChaser
     * @since 2023/8/25 14:51
     */
    private static String resolveKey(String key, StandardEvaluationContext context) {
        String[] comb = resolveEl(key);
        assert comb != null;
        assert comb.length == 3;
        String spEl = comb[1];

        String value = spelExpressionParser.parseRaw(spEl).getValue(context, String.class);
        return (comb[0].length() != 0 ? comb[0] + KEY_SEPARATOR : "")
                + value
                + (comb[2].length() != 0 ? KEY_SEPARATOR + comb[2] : "");
    }

    /**
     * 解析 key 的 EL 表达式组成部分
     *
     * @param el EL 表达式
     * @return String[]
     * @author TimeChaser
     * @since 2023/8/25 14:53
     */
    private static String[] resolveEl(String el) {
        String[] splits = el.split(KEY_SEPARATOR);
        for (int i = 0, n = splits.length; i < n; i++) {
            String target = splits[i];
            if (target.charAt(0) == SP_EL_PREFIX) {
                return new String[]{
                        i == 0 ? "" : String.join("", Arrays.copyOfRange(splits, 0, i)),
                        target,
                        i == n - 1 ? "" : String.join("", Arrays.copyOfRange(splits, i + 1, n))
                };
            }
        }
        BaseErrorEnum.INVALID_SP_EL_ERROR.throwException();
        return null;
    }

    /**
     * 组装 EL 表达式上下文
     *
     * @param parameters 切面方法的参数项
     * @param args       切面方法的参数值
     * @return {@link StandardEvaluationContext}
     * @author TimeChaser
     * @since 2023/8/25 15:06
     */
    private static StandardEvaluationContext getElContext(String[] parameters, Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(parameters[i], args[i]);
        }
        return context;
    }

}

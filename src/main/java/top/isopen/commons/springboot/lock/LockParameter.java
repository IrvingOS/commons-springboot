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

    public LockParameter(RedLock redLock, JoinPoint joinPoint) {
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

    public LockParameter(RedLocks redLocks, JoinPoint joinPoint) {
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

    private static String resolveKey(String str, String[] parameters, Object[] args) {
        StandardEvaluationContext context = getElContext(parameters, args);
        return resolveKey(str, context);
    }

    private static String[] resolveKey(String[] strings, String[] parameters, Object[] args) {
        String[] values = new String[strings.length];
        StandardEvaluationContext context = getElContext(parameters, args);

        for (int i = 0; i < strings.length; i++) {
            values[i] = resolveKey(strings[i], context);
        }
        return values;
    }


    private static String resolveKey(String str, StandardEvaluationContext context) {
        String[] comb = resolveEl(str);
        assert comb != null;
        assert comb.length == 3;
        String spEl = comb[1];

        String value = spelExpressionParser.parseRaw(spEl).getValue(context, String.class);
        return (comb[0].length() != 0 ? comb[0] + KEY_SEPARATOR : "")
                + value
                + (comb[2].length() != 0 ? KEY_SEPARATOR + comb[2] : "");
    }

    private static String[] resolveEl(String str) {
        String[] splits = str.split(KEY_SEPARATOR);
        for (int i = 0, n = splits.length; i < n; i++) {
            String split = splits[i];
            if (split.charAt(0) == SP_EL_PREFIX) {
                return new String[]{
                        i == 0 ? "" : String.join("", Arrays.copyOfRange(splits, 0, i)),
                        split,
                        i == n - 1 ? "" : String.join("", Arrays.copyOfRange(splits, i + 1, n))
                };
            }
        }
        BaseErrorEnum.INVALID_SP_EL_ERROR.throwException();
        return null;
    }

    private static StandardEvaluationContext getElContext(String[] parameters, Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(parameters[i], args[i]);
        }
        return context;
    }

}

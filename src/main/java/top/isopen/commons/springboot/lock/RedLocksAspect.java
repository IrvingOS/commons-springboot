package top.isopen.commons.springboot.lock;

import io.reactivex.rxjava3.functions.BiFunction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import top.isopen.commons.logging.Log;
import top.isopen.commons.logging.LogFactory;
import top.isopen.commons.springboot.enums.BaseErrorEnum;
import top.isopen.commons.springboot.lock.annotation.RedLocks;

import java.util.Arrays;

@Aspect
public class RedLocksAspect {

    private static final Log log = LogFactory.getLog(RedLocksAspect.class);
    private final RedissonClient redissonClient;

    public RedLocksAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around(value = "@within(redLocks) || @annotation(redLocks)")
    public Object process(ProceedingJoinPoint joinPoint, RedLocks redLocks) throws Throwable {
        LockParameter parameter = new LockParameter(redLocks, joinPoint);
        boolean fair = parameter.isFair();
        String[] keys = parameter.getKeys();

        BiFunction<RedissonClient, String, RLock> doGetLock = fair ? RedissonClient::getFairLock : RedissonClient::getLock;

        RLock[] locks = new RLock[keys.length];
        for (int i = 0; i < keys.length; i++) {
            locks[i] = doGetLock.apply(redissonClient, keys[i]);
        }
        RLock multiLock = redissonClient.getMultiLock(locks);

        boolean locked = multiLock.tryLock(parameter.getWaitTime(), parameter.getLeaseTime(), parameter.getTimeUnit());
        if (!locked) {
            log.info("try multi lock failed: {}", Arrays.toString(keys));
            BaseErrorEnum.INVALID_RED_LOCK_TRY_ERROR.throwException();
        }
        log.info("try multi lock succeed: {}", Arrays.toString(keys));

        return joinPoint.proceed();
    }

}

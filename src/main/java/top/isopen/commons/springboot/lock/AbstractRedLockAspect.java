package top.isopen.commons.springboot.lock;

import io.reactivex.rxjava3.functions.BiFunction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import top.isopen.commons.logging.Log;
import top.isopen.commons.logging.LogFactory;
import top.isopen.commons.springboot.enums.BaseErrorEnum;
import top.isopen.commons.springboot.lock.annotation.RedLock;
import top.isopen.commons.springboot.lock.annotation.RedLocks;

import java.util.concurrent.TimeUnit;

public abstract class AbstractRedLockAspect {
    private static final Log log = LogFactory.getLog(AbstractRedLockAspect.class);
    private final RedissonClient redissonClient;

    protected AbstractRedLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    protected void lockOn(ProceedingJoinPoint joinPoint, RedLocks redLocks) throws Throwable {
        LockParameter parameter = new LockParameter(redLocks, joinPoint);
        String[] keys = parameter.getKeys();

        RLock multiLock = this.getLock(keys, parameter.isFair());
        this.lockOn(multiLock, parameter);
    }

    protected void lockOn(ProceedingJoinPoint joinPoint, RedLock redLock) throws Throwable {
        LockParameter parameter = new LockParameter(redLock, joinPoint);
        RLock lock = this.getLock(parameter.getKey(), parameter.isFair());
        this.lockOn(lock, parameter);
    }

    private RLock getLock(String key, boolean isFair) {
        return isFair ? redissonClient.getFairLock(key) : redissonClient.getLock(key);
    }

    private RLock getLock(String[] keys, boolean isFair) throws Throwable {
        BiFunction<RedissonClient, String, RLock> doGetLock = isFair ? RedissonClient::getFairLock : RedissonClient::getLock;

        RLock[] locks = new RLock[keys.length];
        for (int i = 0; i < keys.length; i++) {
            locks[i] = doGetLock.apply(redissonClient, keys[i]);
        }
        return redissonClient.getMultiLock(locks);
    }

    private void lockOn(RLock lock, LockParameter parameter) throws InterruptedException {
        long waitTime = parameter.getWaitTime();
        long leaseTime = parameter.getLeaseTime();
        TimeUnit timeUnit = parameter.getTimeUnit();

        if (waitTime == -1L) {
            /*等待时间为 -1L 则为自旋锁，一直等待直到加锁成功*/
            lock.lock(leaseTime, timeUnit);
        } else {
            boolean locked = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (!locked) {
                log.info("try lock failed");
                BaseErrorEnum.INVALID_RED_LOCK_TRY_ERROR.throwException();
            }
        }
        log.info("try lock succeed");
    }

}

package top.isopen.commons.springboot.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import top.isopen.commons.logging.Log;
import top.isopen.commons.logging.LogFactory;
import top.isopen.commons.springboot.enums.BaseErrorEnum;
import top.isopen.commons.springboot.lock.annotation.RedLock;

/**
 * {@link RedLock} 的切面
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:42
 */
@Aspect
public class RedLockAspect {

    private static final Log log = LogFactory.getLog(RedLockAspect.class);
    private final RedissonClient redissonClient;

    public RedLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around(value = "@within(redLock) || @annotation(redLock)")
    public Object process(ProceedingJoinPoint joinPoint, RedLock redLock) throws Throwable {
        LockParameter parameter = new LockParameter(redLock, joinPoint);
        boolean fair = parameter.isFair();
        String key = parameter.getKey();

        RLock lock = fair ? redissonClient.getFairLock(key) : redissonClient.getLock(key);
        boolean locked = lock.tryLock(parameter.getWaitTime(), parameter.getLeaseTime(), parameter.getTimeUnit());
        if (!locked) {
            log.info("try lock failed: {}", key);
            BaseErrorEnum.INVALID_RED_LOCK_TRY_ERROR.throwException();
        }
        log.info("try lock succeed: {}", key);

        return joinPoint.proceed();
    }

}

package top.isopen.commons.springboot.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RedissonClient;
import top.isopen.commons.springboot.lock.annotation.RedLocks;

/**
 * {@link RedLocks} 的切面
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:42
 */
@Aspect
public class RedLocksAspect extends AbstractRedLockAspect {

    public RedLocksAspect(RedissonClient redissonClient) {
        super(redissonClient);
    }

    @Around(value = "@within(redLocks) || @annotation(redLocks)")
    public Object process(ProceedingJoinPoint joinPoint, RedLocks redLocks) throws Throwable {
        lockOn(joinPoint, redLocks);
        return joinPoint.proceed();
    }

}

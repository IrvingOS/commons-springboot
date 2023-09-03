package top.isopen.commons.springboot.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RedissonClient;
import top.isopen.commons.springboot.lock.annotation.RedLock;

/**
 * {@link RedLock} 的切面
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:42
 */
@Aspect
public class RedLockAspect extends AbstractRedLockAspect {

    public RedLockAspect(RedissonClient redissonClient) {
        super(redissonClient);
    }

    @Around(value = "@within(redLock) || @annotation(redLock)")
    public Object process(ProceedingJoinPoint joinPoint, RedLock redLock) throws Throwable {
        lockOn(joinPoint, redLock);
        return joinPoint.proceed();
    }

}

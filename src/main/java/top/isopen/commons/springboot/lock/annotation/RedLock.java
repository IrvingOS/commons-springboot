package top.isopen.commons.springboot.lock.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解
 * <p>
 * 可重入、可 Watch Dog，底层依赖 {@link org.redisson.Redisson}
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/6/28 16:11
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedLock {

    /**
     * 是否公平锁
     */
    boolean isFair() default false;

    /**
     * 锁的 key
     * <p>
     * key 中只能存在一个 spEl 表达式
     * <p>
     * 类似于：
     * <p>
     * #user.userId
     * <p>
     * #user.userId:password
     * <p>
     * user:#user.userId:password
     */
    String key();

    /**
     * 加锁的时间（单位 {@link RedLock#timeUnit()}），超过这个时间后锁便自动解锁；
     * <p>
     * 如果 leaseTime 为 -1，则启用 Watch Dog 机制，先加锁 30 秒，线程存货则续期
     */
    long leaseTime() default -1L;

    /**
     * 参数的时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 获取锁的最大尝试时间（单位 {@link RedLock#timeUnit()}）
     * <p>
     * 该值大于 0 则使用 locker.tryLock 方法加锁，在 waitTime 时间过后仍未加锁成功则抛出异常
     * <p>
     * 等于 -1 则使用 locker.lock 方法自旋锁循环加锁，直到加锁成功
     */
    long waitTime() default 500L;

}

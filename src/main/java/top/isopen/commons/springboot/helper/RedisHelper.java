package top.isopen.commons.springboot.helper;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.types.Expiration;
import top.isopen.commons.logging.Log;
import top.isopen.commons.logging.LogFactory;
import top.isopen.commons.springboot.enums.BaseErrorEnum;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class RedisHelper {

    private static final Log log = LogFactory.getLog(RedisHelper.class);

    /**
     * 使用 StringRedisTemplate（其是 RedisTemplate 的定制化升级）
     */
    private static StringRedisTemplate redisTemplate;

    public RedisHelper(StringRedisTemplate redisTemplate) {
        RedisHelper.redisTemplate = redisTemplate;
    }

    /**
     * key 相关操作
     *
     * @author JustryDeng
     * @since 2020/3/7 16:54:25
     */
    public static class KeyOps {

        /**
         * 根据 key, 删除 redis 中的对应 key-value
         * <p>
         * 注：若删除失败, 则返回 false。
         * <p>
         * 若 redis 中，不存在该 key, 那么返回的也是 false。
         * 所以，不能因为返回了 false,就认为 redis 中一定还存
         * 在该 key 对应的 key-value。
         *
         * @param key 要删除的 key
         * @return 删除是否成功
         * @since 2020/3/7 17:15:02
         */
        public static boolean delete(String key) {
            log.info("delete(...) => key -> {}", key);
            Boolean result = redisTemplate.delete(key);
            log.info("delete(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 根据 keys, 批量删除 key-value
         * <p>
         * 注：若 redis 中，不存在对应的 key, 那么计数不会加 1, 即:
         * redis 中存在的 key-value 里，有名为 a1、a2 的 key，
         * 删除时，传的集合是 a1、a2、a3，那么返回结果为 2。
         *
         * @param keys 要删除的 key 集合
         * @return 删除了的 key-value 个数
         * @since 2020/3/7 17:48:04
         */
        public static long delete(Collection<String> keys) {
            log.info("delete(...) => keys -> {}", keys);
            Long count = redisTemplate.delete(keys);
            log.info("delete(...) => count -> {}", count);
            if (count == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return count;
        }

        /**
         * 将 key 对应的 value 值进行序列化，并返回序列化后的 value 值。
         * <p>
         * 注：若不存在对应的 key, 则返回 null。
         * 注：dump 时，并不会删除 redis 中的对应 key-value。
         * 注：dump 功能与 restore 相反。
         *
         * @param key 要序列化的 value 的 key
         * @return 序列化后的 value 值
         * @since 2020/3/8 11:34:13
         */
        public static byte[] dump(String key) {
            log.info("dump(...) =>key -> {}", key);
            byte[] result = redisTemplate.dump(key);
            log.info("dump(...) => result -> {}", Arrays.toString(result));
            return result;
        }

        /**
         * 将给定的 value 值，反序列化到 redis 中, 形成新的 key-value。
         *
         * @param key        value 对应的 key
         * @param value      要反序列的 value 值。
         *                   注：这个值可以由 {@link KeyOps#dump(String)} 获得
         * @param timeToLive 反序列化后的 key-value 的存活时长
         * @param unit       timeToLive 的单位
         * @throws RedisSystemException 如果 redis 中已存在同样的 key 时，抛出此异常
         * @since 2020/3/8 11:36:45
         */
        public static void restore(String key, byte[] value, long timeToLive, TimeUnit unit) {
            restore(key, value, timeToLive, unit, false);
        }

        /**
         * 将给定的 value 值，反序列化到 redis 中, 形成新的 key-value。
         *
         * @param key        value 对应的 key
         * @param value      要反序列的 value 值。
         *                   注：这个值可以由 {@link KeyOps#dump(String)} 获得
         * @param timeToLive 反序列化后的 key-value 的存活时长
         * @param unit       timeToLive 的单位
         * @param replace    若 redis 中已经存在了相同的 key, 是否替代原来的 key-value
         * @throws RedisSystemException 如果 redis 中已存在同样的key, 且 replace 为 false 时，抛出此异常
         * @since 2020/3/8 11:36:45
         */
        public static void restore(String key, byte[] value, long timeToLive, TimeUnit unit, boolean replace) {
            log.info("restore(...) => key -> {}, value -> {}, timeToLive -> {}, unit -> {}, replace -> {}", key, value, timeToLive, unit, replace);
            redisTemplate.restore(key, value, timeToLive, unit, replace);
        }

        /**
         * redis 中是否存在,指定 key 的 key-value
         *
         * @param key 指定的 key
         * @return 是否存在对应的 key-value
         * @since 2020/3/8 12:16:46
         */
        public static boolean hasKey(String key) {
            log.info("hasKey(...) => key -> {}", key);
            Boolean result = redisTemplate.hasKey(key);
            log.info("hasKey(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 给指定的 key 对应的 key-value 设置：多久过时
         * <p>
         * 注：过时后，redis 会自动删除对应的 key-value。
         * 注：若 key 不存在，那么也会返回 false。
         *
         * @param key        指定的 key
         * @param timeToLive 过时时间
         * @param unit       timeout 的单位
         * @return 操作是否成功
         * @since 2020/3/8 12:18:58
         */
        public static boolean expire(String key, long timeToLive, TimeUnit unit) {
            log.info("expire(...) => key -> {}, timeToLive -> {}, unit -> {}", key, timeToLive, unit);
            Boolean result = redisTemplate.expire(key, timeToLive, unit);
            log.info("expire(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 给指定的 key 对应的 key-value 设置：什么时候过时
         * <p>
         * 注：过时后，redis 会自动删除对应的 key-value。
         * 注：若 key 不存在，那么也会返回 false。
         *
         * @param key  指定的 key
         * @param date 啥时候过时
         * @return 操作是否成功
         * @since 2020/3/8 12:19:29
         */
        public static boolean expireAt(String key, Date date) {
            log.info("expireAt(...) => key -> {}, date -> {}", key, date);
            Boolean result = redisTemplate.expireAt(key, date);
            log.info("expireAt(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 找到所有匹配 pattern 的 key,并返回该 key 的集合.
         * <p>
         * 提示：若 redis 中键值对较多，此方法耗时相对较长，慎用！慎用！慎用！
         *
         * @param pattern 匹配模板。
         *                注：常用的通配符有：
         *                ?    有且只有一个;
         *                *     >=0 哥;
         * @return 匹配 pattern 的 key 的集合。 可能为 null。
         * @since 2020/3/8 12:38:38
         */
        public static Set<String> keys(String pattern) {
            log.info("keys(...) => pattern -> {}", pattern);
            Set<String> keys = redisTemplate.keys(pattern);
            log.info("keys(...) => keys -> {}", keys);
            return keys;
        }

        /**
         * 将当前数据库中的 key 对应的 key-value,移动到对应位置的数据库中。
         * <p>
         * 注：单机版的 redis,默认将存储分为 16 个 db, index 为 0 到 15。
         * 注：同一个 db 下，key 唯一； 但是在不同 db 中，key 可以相同。
         * 注：若目标 db 下，已存在相同的 key, 那么 move 会失败，返回 false。
         *
         * @param key     定位要移动的 key-value 的 key
         * @param dbIndex 要移动到哪个 db
         * @return 移动是否成功。
         * 注：若目标 db 下，已存在相同的 key, 那么 move 会失败，返回 false。
         * @since 2020/3/8 13:01:00
         */
        public static boolean move(String key, int dbIndex) {
            log.info("move(...) => key  -> {}, dbIndex -> {}", key, dbIndex);
            Boolean result = redisTemplate.move(key, dbIndex);
            log.info("move(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 移除 key 对应的 key-value 的过期时间, 使该 key-value 一直存在
         * <p>
         * 注：若 key 对应的 key-value，本身就是一直存在（无过期时间的）， 那么 persist 方法会返回 false;
         * 若没有 key 对应的 key-value 存在，本那么 persist 方法会返回 false;
         *
         * @param key 定位 key-value 的 key
         * @return 操作是否成功
         * @since 2020/3/8 13:10:02
         */
        public static boolean persist(String key) {
            log.info("persist(...) => key -> {}", key);
            Boolean result = redisTemplate.persist(key);
            log.info("persist(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 获取 key 对应的 key-value 的过期时间
         * <p>
         * 注：若 key-value 永不过期， 那么返回的为 -1。
         * 注：若不存在 key 对应的 key-value， 那么返回的为 -2
         * 注：若存在零碎时间不足 1 SECONDS，则（大体上）四舍五入到 SECONDS 级别。
         *
         * @param key 定位 key-value 的 key
         * @return 过期时间（单位s）
         * @since 2020/3/8 13:17:35
         */
        public static long getExpire(String key) {
            return getExpire(key, TimeUnit.SECONDS);
        }

        /**
         * 获取 key 对应的 key-value 的过期时间
         * <p>
         * 注：若 key-value 永不过期， 那么返回的为 -1。
         * 注：若不存在 key 对应的 key-value，那么返回的为 -2
         * 注：若存在零碎时间不足 1 unit，则（大体上）四舍五入到 unit 别。
         *
         * @param key 定位 key-value 的 key
         * @return 过期时间（单位 unit）
         * @since 2020/3/8 13:17:35
         */
        public static long getExpire(String key, TimeUnit unit) {
            log.info("getExpire(...) =>key -> {}, unit is -> {}", key, unit);
            Long result = redisTemplate.getExpire(key, unit);
            log.info("getExpire(...) => result ->  {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 从 redis 的所有 key 中，随机获取一个 key
         * <p>
         * 注：若 redis 中不存在任何 key-value，那么这里返回 null
         *
         * @return 随机获取到的一个 key
         * @since 2020/3/8 14:11:43
         */
        public static String randomKey() {
            String result = redisTemplate.randomKey();
            log.info("randomKey(...) => result -> {}", result);
            return result;
        }

        /**
         * 重命名对应的 oldKey 为新的 newKey
         * <p>
         * 注：若 oldKey 不存在， 则会抛出异常.
         * 注：若 redis 中已存在与 newKey 一样的 key,
         * 那么原 key-value 会被丢弃，
         * 只留下新的 key，以及原来的 value
         * 示例说明：假设 redis 中已有（keyAlpha, valueAlpha）和（keyBeta, valueBeat）,
         * 在使用 rename（keyAlpha, keyBeta）替换后, redis 中只会剩下（keyBeta, valueAlpha）
         *
         * @param oldKey 旧的 key
         * @param newKey 新的 key
         * @throws RedisSystemException 若 oldKey 不存在时， 抛出此异常
         * @since 2020/3/8 14:14:17
         */
        public static void rename(String oldKey, String newKey) {
            log.info("rename(...) => oldKey -> {}, newKey -> {}", oldKey, newKey);
            redisTemplate.rename(oldKey, newKey);
        }

        /**
         * 当 redis 中不存在 newKey 时, 重命名对应的 oldKey 为新的 newKey。
         * 否者不进行重命名操作。
         * <p>
         * 注：若 oldKey 不存在， 则会抛出异常.
         *
         * @param oldKey 旧的 key
         * @param newKey 新的 key
         * @throws RedisSystemException 若 oldKey 不存在时， 抛出此异常
         * @since 2020/3/8 14:14:17
         */
        public static boolean renameIfAbsent(String oldKey, String newKey) {
            log.info("renameIfAbsent(...) => oldKey -> {}, newKey -> {}", oldKey, newKey);
            Boolean result = redisTemplate.renameIfAbsent(oldKey, newKey);
            log.info("renameIfAbsent(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 获取 key 对应的 value 的数据类型
         * <p>
         * 注：若 redis 中不存在该 key 对应的 key-value， 那么这里返回 NONE。
         *
         * @param key 用于定位的 key
         * @return key 对应的 value 的数据类型的枚举
         * @since 2020/3/8 14:40:16
         */
        public static DataType type(String key) {
            log.info("type(...) => key -> {}", key);
            DataType result = redisTemplate.type(key);
            log.info("type(...) => result -> {}", result);
            return result;
        }

    }

    /**
     * string 相关操作
     *
     * @author JustryDeng
     * @since 2020/3/7 16:54:25
     */
    public static class StringOps {

        /**
         * 设置 key-value
         * <p>
         * 注：若已存在相同的 key, 那么原来的 key-value 会被丢弃。
         *
         * @param key   key
         * @param value key 对应的 value
         * @since 2020/3/8 15:40:59
         */
        public static void set(String key, String value) {
            log.info("set(...) => key -> {}, value -> {}", key, value);
            redisTemplate.opsForValue().set(key, value);
        }

        /**
         * 处理 redis 中 key 对应的 value 值, 将第 offset 位的值, 设置为 1 或 0。
         * <p>
         * 说明：在 redis 中，存储的字符串都是以二级制的进行存在的；如存储的 key-value 里，值为 abc，实际上，
         * 在 redis 里面存储的是 011000010110001001100011，前 8 位对应 a，中间 8 位对应 b，后面 8 位对应 c。
         * 示例：这里如果 setBit(key, 6, true) 的话，就是将索引位置 6 的那个数，设置值为 1，值就变成
         * 了 011000110110001001100011
         * 追注：offset 即 index，从 0 开始。
         * <p>
         * 注：参数 value 为 true，则设置为 1；参数 value 为 false，则设置为 0。
         * <p>
         * 注：若 redis 中不存在对应的 key，那么会自动创建新的。
         * 注：offset 可以超过 value 在二进制下的索引长度。
         *
         * @param key    定位 value 的 key
         * @param offset 要改变的 bit 的索引
         * @param value  改为 1 或 0，true - 改为 1，false - 改为 0
         * @return set 是否成功
         * @since 2020/3/8 16:30:37
         */
        public static boolean setBit(String key, long offset, boolean value) {
            log.info("setBit(...) => key -> {}, offset -> {}, value -> {}", key, offset, value);
            Boolean result = redisTemplate.opsForValue().setBit(key, offset, value);
            log.info("setBit(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 设置 key-value
         * <p>
         * 注：若已存在相同的 key，那么原来的 key-value 会被丢弃
         * <p>
         * 注：原子操作
         *
         * @param key        key
         * @param value      key 对应的 value
         * @param timeToLive 过时时长
         * @param unit       timeToLive 的单位
         * @since 2020/3/8 15:40:59
         */
        public static void setEx(String key, String value, long timeToLive, TimeUnit unit) {
            log.info("setEx(...) => key -> {}, value -> {}, timeToLive -> {}, unit -> {}", key, value, timeToLive, unit);
            redisTemplate.opsForValue().set(key, value, timeToLive, unit);
        }

        /**
         * 若不存在 key 时，向 redis 中添加 key-value, 返回成功/失败。
         * 若存在，则不作任何操作，返回 false。
         *
         * @param key   key
         * @param value key 对应的 value
         * @return set 是否成功
         * @since 2020/3/8 16:51:36
         */
        public static boolean setIfAbsent(String key, String value) {
            log.info("setIfAbsent(...) => key -> {}, value -> {}", key, value);
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value);
            log.info("setIfAbsent(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }


        /**
         * 若不存在 key 时, 向 redis 中添加一个（具有超时时长的）key-value, 返回成功/失败。
         * 若存在，则不作任何操作，返回 false。
         *
         * @param key        key
         * @param value      key 对应的 value
         * @param timeToLive 超时时长
         * @param unit       timeToLive 的单位
         * @return set 是否成功
         * @since 2020/3/8 16:51:36
         */
        public static boolean setIfAbsent(String key, String value, long timeToLive, TimeUnit unit) {
            log.info("setIfAbsent(...) => key -> {}, value -> {}, timeToLive -> {}, unit -> {}",
                    key, value, timeToLive, unit);
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, timeToLive, unit);
            log.info("setIfAbsent(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 从（redis 中 key 对应的）value 的 offset 位置起（包含该位置），用 replaceValue 替换对应长度的值。
         * <p>
         * 举例说明：
         * 1. 假设 redis 中存在 key-value("ds", "0123456789"); 调
         * 用 setRange("ds", "abcdefghijk", 3)后，redis 中该 value 值就变为了 [012abcdefghijk]
         * <p>
         * 2. 假设 redis 中存在 key-value("jd", "0123456789"); 调
         * 用 setRange("jd", "xyz", 3)后，redis 中该 value 值就变为了 [012xyz6789]
         * <p>
         * 3. 假设 redis 中存在 key-value("ey", "0123456789"); 调
         * 用 setRange("ey", "qwer", 15)后，redis 中该 value 值就变为了 [0123456789     qwer]
         * 注：case3 比较特殊，offset 超过了原 value 的长度了。中间就会有一些空格来填充，但是如果在程序
         * 中直接输出的话，中间那部分空格可能会出现乱码。
         *
         * @param key          定位 key-value 的 key
         * @param replaceValue 要替换的值
         * @param offset       起始位置
         * @since 2020/3/8 17:04:31
         */
        public static void setRange(String key, String replaceValue, long offset) {
            log.info("setRange(...) => key -> {}, replaceValue -> {}, offset -> {}", key, replaceValue, offset);
            redisTemplate.opsForValue().set(key, replaceValue, offset);
        }

        /**
         * 获取到 key 对应的 value 的长度。
         * <p>
         * 注：长度等于 {@link String#length}。
         * 注：若 redis 中不存在对应的 key-value，则返回值为 0.
         *
         * @param key 定位 value 的 key
         * @return value 的长度
         * @since 2020/3/8 17:14:30
         */
        public static long size(String key) {
            log.info("size(...) => key -> {}", key);
            Long result = redisTemplate.opsForValue().size(key);
            log.info("size(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 批量设置 key-value
         * <p>
         * 注：若存在相同的 key, 则原来的 key-value会 被丢弃。
         *
         * @param maps key-value 集
         * @since 2020/3/8 17:21:19
         */
        public static void multiSet(Map<String, String> maps) {
            log.info("multiSet(...) => maps -> {}", maps);
            redisTemplate.opsForValue().multiSet(maps);
        }

        /**
         * 当 redis 中，不存在任何一个 keys 时，才批量设置 key-value，并返回成功/失败。
         * 否者，不进行任何操作，并返回 false。
         * <p>
         * 即：假设调用此方法时传入的参数 map 是这样的：{k1=v1, k2=v2, k3=v3}
         * 那么 redis 中，k1、k2、k3 都不存在时，才会批量设置 key-value；
         * 否则不会设置任何 key-value。
         * <p>
         * 注：若存在相同的 key，则原来的 key-value 会被丢弃。
         *
         * @param maps key-value 集
         * @return 操作是否成功
         * @since 2020/3/8 17:21:19
         */
        public static boolean multiSetIfAbsent(Map<String, String> maps) {
            log.info("multiSetIfAbsent(...) => maps -> {}", maps);
            Boolean result = redisTemplate.opsForValue().multiSetIfAbsent(maps);
            log.info("multiSetIfAbsent(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 增/减 整数
         * <p>
         * 注：负数则为减。
         * <p>
         * 注：若 key 对应的 value 值不支持增/减操作（即：value不是数字），那么会
         * 抛出 org.springframework.data.redis.RedisSystemException
         *
         * @param key       用于定位 value 的 key
         * @param increment 增加多少
         * @return 增加后的总值。
         * @throws RedisSystemException key 对应的 value 值不支持增/减操作时
         * @since 2020/3/8 17:45:51
         */
        public static long incrBy(String key, long increment) {
            log.info("incrBy(...) => key -> {}, increment -> {}", key, increment);
            Long result = redisTemplate.opsForValue().increment(key, increment);
            log.info("incrBy(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 增/减 浮点数
         * <p>
         * 注：慎用浮点数，会有精度问题。
         * <p>
         * 如: 先 RedisHelper.StringOps.set("ds", "123");
         * 然后再 RedisUtil.StringOps.incrByFloat("ds", 100.6);
         * 就会看到精度问题。
         * <p>
         * 注：负数则为减。
         * <p>
         * 注：若 key 对应的 value 值不支持增/减操作（即：value不是数字），那么会
         * 抛出 org.springframework.data.redis.RedisSystemException
         *
         * @param key       用于定位value的key
         * @param increment 增加多少
         * @return 增加后的总值。
         * @throws RedisSystemException key对应的value值不支持增/减操作时
         * @since 2020/3/8 17:45:51
         */
        public static double incrByFloat(String key, double increment) {
            log.info("incrByFloat(...) => key -> {}, increment -> {}", key, increment);
            Double result = redisTemplate.opsForValue().increment(key, increment);
            log.info("incrByFloat(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 追加值到末尾
         * <p>
         * 注: 当 redis 中原本不存在 key 时，那么（从效果上来看）此方法就等价于 {@link this#set(String, String)}
         *
         * @param key   定位 value 的 key
         * @param value 要追加的 value 值
         * @return 追加后，整个 value 的长度
         * @since 2020/3/8 17:59:21
         */
        public static int append(String key, String value) {
            log.info("append(...) => key -> {}, value -> {}", key, value);
            Integer result = redisTemplate.opsForValue().append(key, value);
            log.info("append(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 根据 key，获取到对应的 value 值
         *
         * @param key key-value 对应的 key
         * @return 该 key 对应的值。
         * 注: 若 key 不存在， 则返回 null。
         * @since 2020/3/8 16:27:41
         */
        public static String get(String key) {
            log.info("get(...) => key -> {}", key);
            String result = redisTemplate.opsForValue().get(key);
            log.info("get(...) => result -> {} ", result);
            return result;
        }

        /**
         * 对（key 对应的）value 进行截取, 截取范围为 [start, end]
         * <p>
         * 注：若 [start, end] 的范围不在 value 的范围中，那么返回的是空字符串 ""
         * <p>
         * 注：若 value 只有一部分在 [start, end] 的范围中，那么返回的是 value 对应部分的内容（即：不足的地方，并不会以空来填充）
         *
         * @param key   定位 value 的 key
         * @param start 起始位置（从0开始）
         * @param end   结尾位置（从0开始）
         * @return 截取后的字符串
         * @since 2020/3/8 18:08:45
         */
        public static String getRange(String key, long start, long end) {
            log.info("getRange(...) => kry -> {}", key);
            String result = redisTemplate.opsForValue().get(key, start, end);
            log.info("getRange(...) => result -> {} ", result);
            return result;
        }

        /**
         * 给指定 key 设置新的 value，并返回旧的 value
         * <p>
         * 注: 若 redis 中不存在 key，那么此操作仍然可以成功，不过返回的旧值是 null
         *
         * @param key      定位 value 的 key
         * @param newValue 要为该 key 设置的新的 value 值
         * @return 旧的 value 值
         * @since 2020/3/8 18:14:24
         */
        public static String getAndSet(String key, String newValue) {
            log.info("getAndSet(...) => key -> {}, value -> {}", key, newValue);
            String oldValue = redisTemplate.opsForValue().getAndSet(key, newValue);
            log.info("getAndSet(...) => oldValue -> {}", oldValue);
            return oldValue;
        }

        /**
         * 获取（key对应的）value 在二进制下，offset 位置的 bit 值。
         * <p>
         * 注：当 offset 的值在（二进制下的 value 的）索引范围外时，返回的也是 false。
         * <p>
         * 示例：
         * RedisHelper.StringOps.set("akey", "a");
         * 字符串 a，转换为二进制为 01100001
         * 那么 getBit("akey", 6) 获取到的结果为 false。
         *
         * @param key    定位 value 的 key
         * @param offset 定位 bit 的索引
         * @return offset 位置对应的 bit 的值（true - 1, false - 0）
         * @since 2020/3/8 18:21:10
         */
        public static boolean getBit(String key, long offset) {
            log.info("getBit(...) => key -> {}, offset -> {}", key, offset);
            Boolean result = redisTemplate.opsForValue().getBit(key, offset);
            log.info("getBit(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 批量获取 value 值
         * <p>
         * 注：若 redis 中，对应的 key 不存在，那么该 key 对应的返回的 value 值为 null
         *
         * @param keys key 集
         * @return value 值集合
         * @since 2020/3/8 18:26:33
         */
        public static List<String> multiGet(Collection<String> keys) {
            log.info("multiGet(...) => keys -> {}", keys);
            List<String> result = redisTemplate.opsForValue().multiGet(keys);
            log.info("multiGet(...) => result -> {}", result);
            return result;
        }

    }

    /**
     * hash 相关操作
     * <p>
     * 提示：简单的，可以将 redis 中 hash 的数据结构看作是 Map<String, Map<HK, HV> >
     *
     * @author JustryDeng
     * @since 2020/3/8 23:39:26
     */
    public static class HashOps {

        /**
         * 向 key 对应的 hash 中，增加一个键值对 entryKey-entryValue
         * <p>
         * 注：同一个 hash 里面，若已存在相同的 entryKey，那么此操作将丢弃原来的 entryKey-entryValue，
         * 而使用新的 entryKey-entryValue。
         *
         * @param key        定位 hash 的 key
         * @param entryKey   要向 hash 中增加的键值对里的键
         * @param entryValue 要向 hash 中增加的键值对里的值
         * @since 2020/3/8 23:49:52
         */
        public static void hPut(String key, String entryKey, String entryValue) {
            log.info("hPut(...) => key -> {}, entryKey -> {}, entryValue -> {}", key, entryKey, entryValue);
            redisTemplate.opsForHash().put(key, entryKey, entryValue);
        }

        /**
         * 向 key 对应的 hash 中，增加 maps（即: 批量增加 entry 集）
         * <p>
         * 注：同一个 hash 里面，若已存在相同的 entryKey，那么此操作将丢弃原来的 entryKey-entryValue，
         * 而使用新的 entryKey-entryValue
         *
         * @param key  定位 hash 的 key
         * @param maps 要向 hash 中增加的键值对集
         * @since 2020/3/8 23:49:52
         */
        public static void hPutAll(String key, Map<String, String> maps) {
            log.info("hPutAll(...) => key -> {}, maps -> {}", key, maps);
            redisTemplate.opsForHash().putAll(key, maps);
        }

        /**
         * 当 key 对应的 hash 中，不存在 entryKey 时，才（向 key 对应的 hash 中）增加 entryKey-entryValue
         * 否者，不进行任何操作
         *
         * @param key        定位 hash 的 key
         * @param entryKey   要向 hash 中增加的键值对里的键
         * @param entryValue 要向 hash 中增加的键值对里的值
         * @return 操作是否成功。
         * @since 2020/3/8 23:49:52
         */
        public static boolean hPutIfAbsent(String key, String entryKey, String entryValue) {
            log.info("hPutIfAbsent(...) => key -> {}, entryKey -> {}, entryValue -> {}",
                    key, entryKey, entryValue);
            Boolean result = redisTemplate.opsForHash().putIfAbsent(key, entryKey, entryValue);
            log.info("hPutIfAbsent(...) => result -> {}", result);
            return result;
        }

        /**
         * 获取到 key 对应的 hash 里面的对应字段的值
         * <p>
         * 注: 若 redis 中不存在对应的 key，则返回 null。
         * 若 key 对应的 hash 中不存在对应的 entryKey，也会返回 null。
         *
         * @param key      定位 hash 的 key
         * @param entryKey 定位 hash 里面的 entryValue 的 entryKey
         * @return key 对应的 hash 里的 entryKey 对应的 entryValue 值
         * @since 2020/3/9 9:09:30
         */
        public static Object hGet(String key, String entryKey) {
            log.info("hGet(...) => key -> {}, entryKey -> {}", key, entryKey);
            Object entryValue = redisTemplate.opsForHash().get(key, entryKey);
            log.info("hGet(...) => entryValue -> {}", entryValue);
            return entryValue;
        }

        /**
         * 获取到 key 对应的 hash（即：获取到 key 对应的 Map<HK, HV>）
         * <p>
         * 注：若 redis 中不存在对应的 key，则返回一个没有任何 entry 的空的 Map（而不是返回 null）。
         *
         * @param key 定位 hash 的 key
         * @return key 对应的 hash。
         * @since 2020/3/9 9:09:30
         */
        public static Map<Object, Object> hGetAll(String key) {
            log.info("hGetAll(...) => key -> {}", key);
            Map<Object, Object> result = redisTemplate.opsForHash().entries(key);
            log.info("hGetAll(...) => result -> {}", result);
            return result;
        }

        /**
         * 批量获取（key 对应的）hash 中的 entryKey 的 entryValue
         * <p>
         * 注：若 hash 中对应的 entryKey 不存在，那么返回的对应的 entryValue 值为 null
         * <p>
         * 注：redis 中 key 不存在，那么返回的 List 中，每个元素都为 null。
         * <p>
         * 追注：这个 List 本身不为 null，size 也不为 0，只是每个 list 中的每个元素为 null 而已。
         *
         * @param key       定位 hash 的 key
         * @param entryKeys 需要获取的 hash 中的字段集
         * @return hash 中对应 entryKeys 的对应 entryValue 集
         * @since 2020/3/9 9:25:38
         */
        public static List<Object> hMultiGet(String key, Collection<Object> entryKeys) {
            log.info("hMultiGet(...) => key -> {}, entryKeys -> {}", key, entryKeys);
            List<Object> entryValues = redisTemplate.opsForHash().multiGet(key, entryKeys);
            log.info("hMultiGet(...) => entryValues -> {}", entryValues);
            return entryValues;
        }

        /**
         * （批量）删除（key 对应的）hash 中的对应 entryKey-entryValue
         * <p>
         * 注: 1、若 redis 中不存在对应的 key，则返回 0;
         * 2、若要删除的 entryKey，在 key 对应的 hash 中不存在，在 count 不会 +1, 如:
         * <p>
         * RedisHelper.HashOps.hPut("ds", "name", "邓沙利文");
         * RedisHelper.HashOps.hPut("ds", "birthday", "1994-02-05");
         * RedisHelper.HashOps.hPut("ds", "hobby", "女");
         * 则调用 RedisUtil.HashOps.hDelete("ds", "name", "birthday", "hobby", "non-exist-entryKey")
         * 的返回结果为 3
         * 注：若（key 对应的）hash 中的所有 entry 都被删除了，那么该 key 也会被删除
         *
         * @param key       定位 hash 的 key
         * @param entryKeys 定位要删除的 entryKey-entryValue 的 entryKey
         * @return 删除了对应 hash 中多少个 entry
         * @since 2020/3/9 9:37:47
         */
        public static long hDelete(String key, Object... entryKeys) {
            log.info("hDelete(...) => key -> {}, entryKeys -> {}", key, entryKeys);
            Long count = redisTemplate.opsForHash().delete(key, entryKeys);
            log.info("hDelete(...) => count -> {}", count);
            return count;
        }


        /**
         * 查看（key 对应的）hash中，是否存在 entryKey 对应的 entry
         * <p>
         * 注：若 redis 中不存在 key，则返回 false。
         * 注：若 key 对应的 hash 中不存在对应的 entryKey，也会返回 false。
         *
         * @param key      定位 hash 的 key
         * @param entryKey 定位 hash 中 entry 的 entryKey
         * @return hash 中是否存在 entryKey 对应的 entry.
         * @since 2020/3/9 9:51:55
         */
        public static boolean hExists(String key, String entryKey) {
            log.info("hDelete(...) => key -> {}, entryKeys -> {}", key, entryKey);
            Boolean exist = redisTemplate.opsForHash().hasKey(key, entryKey);
            log.info("hDelete(...) => exist -> {}", exist);
            return exist;
        }

        /**
         * 增/减(hash中的某个entryValue值) 整数
         * <p>
         * 注: 负数则为减。
         * 注: 若key不存在，那么会自动创建对应的hash,并创建对应的entryKey、entryValue,entryValue的初始值为increment。
         * 注: 若entryKey不存在，那么会自动创建对应的entryValue,entryValue的初始值为increment。
         * 注: 若key对应的value值不支持增/减操作(即: value不是数字)， 那么会
         * 抛出org.springframework.data.redis.RedisSystemException
         *
         * @param key       用于定位hash的key
         * @param entryKey  用于定位entryValue的entryKey
         * @param increment 增加多少
         * @return 增加后的总值。
         * @throws RedisSystemException key对应的value值不支持增/减操作时
         * @since 2020/3/9 10:09:28
         */
        public static long hIncrBy(String key, Object entryKey, long increment) {
            log.info("hIncrBy(...) => key -> {}, entryKey -> {}, increment -> {}",
                    key, entryKey, increment);
            Long result = redisTemplate.opsForHash().increment(key, entryKey, increment);
            log.info("hIncrBy(...) => result -> {}", result);
            return result;
        }

        /**
         * 增/减(hash中的某个entryValue值) 浮点数
         * <p>
         * 注: 负数则为减。
         * 注: 若key不存在，那么会自动创建对应的hash,并创建对应的entryKey、entryValue,entryValue的初始值为increment。
         * 注: 若entryKey不存在，那么会自动创建对应的entryValue,entryValue的初始值为increment。
         * 注: 若key对应的value值不支持增/减操作(即: value不是数字)， 那么会
         * 抛出org.springframework.data.redis.RedisSystemException
         * 注: 因为是浮点数， 所以可能会和{@link RedisHelper.StringOps#incrByFloat(String, double)}一样， 出现精度问题。
         * 追注: 本人简单测试了几组数据，暂未出现精度问题。
         *
         * @param key       用于定位hash的key
         * @param entryKey  用于定位entryValue的entryKey
         * @param increment 增加多少
         * @return 增加后的总值。
         * @throws RedisSystemException key对应的value值不支持增/减操作时
         * @since 2020/3/9 10:09:28
         */
        public static double hIncrByFloat(String key, Object entryKey, double increment) {
            log.info("hIncrByFloat(...) => key -> {}, entryKey -> {}, increment -> {}",
                    key, entryKey, increment);
            Double result = redisTemplate.opsForHash().increment(key, entryKey, increment);
            log.info("hIncrByFloat(...) => result -> {}", result);
            return result;
        }

        /**
         * 获取(key对应的)hash中的所有entryKey
         * <p>
         * 注: 若key不存在，则返回的是一个空的Set(，而不是返回null)
         *
         * @param key 定位hash的key
         * @return hash中的所有entryKey
         * @since 2020/3/9 10:30:13
         */
        public static Set<Object> hKeys(String key) {
            log.info("hKeys(...) => key -> {}", key);
            Set<Object> entryKeys = redisTemplate.opsForHash().keys(key);
            log.info("hKeys(...) => entryKeys -> {}", entryKeys);
            return entryKeys;
        }

        /**
         * 获取(key对应的)hash中的所有entryValue
         * <p>
         * 注: 若key不存在，则返回的是一个空的List(，而不是返回null)
         *
         * @param key 定位hash的key
         * @return hash中的所有entryValue
         * @since 2020/3/9 10:30:13
         */
        public static List<Object> hValues(String key) {
            log.info("hValues(...) => key -> {}", key);
            List<Object> entryValues = redisTemplate.opsForHash().values(key);
            log.info("hValues(...) => entryValues -> {}", entryValues);
            return entryValues;
        }

        /**
         * 获取(key对应的)hash中的所有entry的数量
         * <p>
         * 注: 若redis中不存在对应的key, 则返回值为0
         *
         * @param key 定位hash的key
         * @return (key对应的)hash中, entry的个数
         * @since 2020/3/9 10:41:01
         */
        public static long hSize(String key) {
            log.info("hSize(...) => key -> {}", key);
            Long count = redisTemplate.opsForHash().size(key);
            log.info("hSize(...) => count -> {}", count);
            return count;
        }

        /**
         * 根据options匹配到(key对应的)hash中的对应的entryKey, 并返回对应的entry集
         * <p>
         * 注: ScanOptions实例的创建方式举例:
         * 1、ScanOptions.NONE
         * 2、ScanOptions.scanOptions().match("n??e").build()
         *
         * @param key     定位hash的key
         * @param options 匹配entryKey的条件
         *                注: ScanOptions.NONE表示全部匹配。
         *                注: ScanOptions.scanOptions().match(pattern).build()表示按照pattern匹配,
         *                其中pattern中可以使用通配符 * ? 等,
         *                * 表示>=0个字符
         *                ？ 表示有且只有一个字符
         *                此处的匹配规则与{@link RedisHelper.KeyOps#keys(String)}处的一样。
         * @return 匹配到的(key对应的)hash中的entry
         * @since 2020/3/9 10:49:27
         */
        public static Cursor<Map.Entry<Object, Object>> hScan(String key, ScanOptions options) {
            log.info("hScan(...) => key -> {}, options -> {}", key, JSON.toJSONString(options));
            Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(key, options);
            log.info("hScan(...) => cursor -> {}", JSON.toJSONString(cursor));
            return cursor;
        }

    }

    /**
     * list相关操作
     * <p>
     * 提示: 列表中的元素，可以重复。
     * <p>
     * 提示: list是有序的。
     * <p>
     * 提示: redis中的list中的索引，可分为两类,这两类都可以用来定位list中元素:
     * 类别一: 从left到right, 是从0开始依次增大:   0,  1,  2,  3...
     * 类别二: 从right到left, 是从-1开始依次减小: -1, -2, -3, -4...
     * <p>
     * 提示: redis中String的数据结构可参考resources/data-structure/List(列表)的数据结构(示例一).png
     * redis中String的数据结构可参考resources/data-structure/List(列表)的数据结构(示例二).png
     *
     * @author JustryDeng
     * @since 2020/3/9 11:30:48
     */
    public static class ListOps {

        /**
         * 从左端推入元素进列表
         * <p>
         * 注: 若redis中不存在对应的key, 那么会自动创建
         *
         * @param key  定位list的key
         * @param item 要推入list的元素
         * @return 推入后，(key对应的)list的size
         * @since 2020/3/9 11:56:05
         */
        public static long lLeftPush(String key, String item) {
            log.info("lLeftPush(...) => key -> {}, item -> {}", key, item);
            Long size = redisTemplate.opsForList().leftPush(key, item);
            log.info("lLeftPush(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 从左端批量推入元素进列表
         * <p>
         * 注: 若redis中不存在对应的key, 那么会自动创建
         * 注: 这一批item中，先push左侧的, 后push右侧的
         *
         * @param key   定位list的key
         * @param items 要批量推入list的元素集
         * @return 推入后，(key对应的)list的size
         * @since 2020/3/9 11:56:05
         */
        public static long lLeftPushAll(String key, String... items) {
            log.info("lLeftPushAll(...) => key -> {}, items -> {}", key, items);
            Long size = redisTemplate.opsForList().leftPushAll(key, items);
            log.info("lLeftPushAll(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 从左端批量推入元素进列表
         * <p>
         * 注: 若redis中不存在对应的key, 那么会自动创建
         * 注: 这一批item中，那个item先从Collection取出来，就先push哪个
         *
         * @param key   定位list的key
         * @param items 要批量推入list的元素集
         * @return 推入后，(key对应的)list的size
         * @since 2020/3/9 11:56:05
         */
        public static long lLeftPushAll(String key, Collection<String> items) {
            log.info("lLeftPushAll(...) => key -> {}, items -> {}", key, items);
            Long size = redisTemplate.opsForList().leftPushAll(key, items);
            log.info("lLeftPushAll(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 如果redis中存在key, 则从左端批量推入元素进列表;
         * 否则，不进行任何操作
         *
         * @param key  定位list的key
         * @param item 要推入list的项
         * @return 推入后，(key对应的)list的size
         * @since 2020/3/9 13:40:08
         */
        public static long lLeftPushIfPresent(String key, String item) {
            log.info("lLeftPushIfPresent(...) => key -> {}, item -> {}", key, item);
            Long size = redisTemplate.opsForList().leftPushIfPresent(key, item);
            log.info("lLeftPushIfPresent(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 若 key 对应的 list 中存在 pivot 项, 那么将 item 放入第一个 pivot 项前(即:放在第一个pivot项左边);
         * 若 key 对应的 list 中不存在 pivot 项, 那么不做任何操作， 直接返回 -1。
         * <p>
         * 注: 若 redis 中不存在对应的 key, 那么会自动创建
         *
         * @param key  定位list的key
         * @param item 要推入list的元素
         * @return 推入后，(key对应的)list的size
         * @since 2020/3/9 11:56:05
         */
        public static long lLeftPush(String key, String pivot, String item) {
            log.info("lLeftPush(...) => key -> {}, pivot -> {}, item -> {}", key, pivot, item);
            Long size = redisTemplate.opsForList().leftPush(key, pivot, item);
            log.info("lLeftPush(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 与{@link RedisHelper.ListOps#lLeftPush(String, String)}类比即可， 不过是从list右侧推入元素
         */
        public static long lRightPush(String key, String item) {
            log.info("lRightPush(...) => key -> {}, item -> {}", key, item);
            Long size = redisTemplate.opsForList().rightPush(key, item);
            log.info("lRightPush(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 与{@link RedisHelper.ListOps#lLeftPushAll(String, String...)}类比即可， 不过是从list右侧推入元素
         */
        public static long lRightPushAll(String key, String... items) {
            log.info("lRightPushAll(...) => key -> {}, items -> {}", key, items);
            Long size = redisTemplate.opsForList().rightPushAll(key, items);
            log.info("lRightPushAll(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 与{@link RedisHelper.ListOps#lLeftPushAll(String, Collection<String>)}类比即可， 不过是从list右侧推入元素
         */
        public static long lRightPushAll(String key, Collection<String> items) {
            log.info("lRightPushAll(...) => key -> {}, items -> {}", key, items);
            Long size = redisTemplate.opsForList().rightPushAll(key, items);
            log.info("lRightPushAll(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 与{@link RedisHelper.ListOps#lLeftPushIfPresent(String, String)}类比即可， 不过是从list右侧推入元素
         */
        public static long lRightPushIfPresent(String key, String item) {
            log.info("lRightPushIfPresent(...) => key -> {}, item -> {}", key, item);
            Long size = redisTemplate.opsForList().rightPushIfPresent(key, item);
            log.info("lRightPushIfPresent(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 与{@link RedisHelper.ListOps#lLeftPush(String, String, String)}类比即可， 不过是从list右侧推入元素
         */
        public static long lRightPush(String key, String pivot, String item) {
            log.info("lLeftPush(...) => key -> {}, pivot -> {}, item -> {}", key, pivot, item);
            Long size = redisTemplate.opsForList().rightPush(key, pivot, item);
            log.info("lLeftPush(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 【非阻塞队列】 从左侧移出(key对应的)list中的第一个元素, 并将该元素返回
         * <p>
         * 注: 此方法是非阻塞的， 即: 若(key对应的)list中的所有元素都被pop移出了，此时，再进行pop的话，会立即返回null
         * 注: 此方法是非阻塞的， 即: 若redis中不存在对应的key,那么会立即返回null
         * 注: 若将(key对应的)list中的所有元素都pop完了，那么该key会被删除
         *
         * @param key 定位list的key
         * @return 移出的那个元素
         * @since 2020/3/9 14:33:56
         */
        public static String lLeftPop(String key) {
            log.info("lLeftPop(...) => key -> {}", key);
            String item = redisTemplate.opsForList().leftPop(key);
            log.info("lLeftPop(...) => item -> {}", item);
            return item;
        }

        /**
         * 【阻塞队列】 从左侧移出(key对应的)list中的第一个元素, 并将该元素返回
         * <p>
         * 注: 此方法是阻塞的， 即: 若(key对应的)list中的所有元素都被pop移出了，此时，再进行pop的话，
         * 会阻塞timeout这么久，然后返回null
         * 注: 此方法是阻塞的， 即: 若redis中不存在对应的key,那么会阻塞timeout这么久，然后返回null
         * 注: 若将(key对应的)list中的所有元素都pop完了，那么该key会被删除
         * <p>
         * 提示: 若阻塞过程中， 目标key-list出现了，且里面有item了，那么会立马停止阻塞, 进行元素移出并返回
         *
         * @param key     定位list的key
         * @param timeout 超时时间
         * @param unit    timeout的单位
         * @return 移出的那个元素
         * @since 2020/3/9 14:33:56
         */
        public static String lLeftPop(String key, long timeout, TimeUnit unit) {
            log.info("lLeftPop(...) => key -> {}, timeout -> {}, unit -> {}", key, timeout, unit);
            String item = redisTemplate.opsForList().leftPop(key, timeout, unit);
            log.info("lLeftPop(...) => item -> {}", item);
            return item;
        }

        /**
         * 与{@link RedisHelper.ListOps#lLeftPop(String)}类比即可， 不过是从list右侧移出元素
         */
        public static String lRightPop(String key) {
            log.info("lRightPop(...) => key -> {}", key);
            String item = redisTemplate.opsForList().rightPop(key);
            log.info("lRightPop(...) => item -> {}", item);
            return item;
        }

        /**
         * 与{@link RedisHelper.ListOps#lLeftPop(String, long, TimeUnit)}类比即可， 不过是从list右侧移出元素
         */
        public static String lRightPop(String key, long timeout, TimeUnit unit) {
            log.info("lRightPop(...) => key -> {}, timeout -> {}, unit -> {}", key, timeout, unit);
            String item = redisTemplate.opsForList().rightPop(key, timeout, unit);
            log.info("lRightPop(...) => item -> {}", item);
            return item;
        }

        /**
         * 【非阻塞队列】 从sourceKey对应的sourceList右侧移出一个item, 并将这个item推
         * 入(destinationKey对应的)destinationList的左侧
         * <p>
         * 注: 若sourceKey对应的list中没有item了，则立马认为(从sourceKey对应的list中pop出来的)item为null,
         * null并不会往destinationKey对应的list中push。
         * 追注: 此时，此方法的返回值是null。
         * <p>
         * 注: 若将(sourceKey对应的)list中的所有元素都pop完了，那么该sourceKey会被删除。
         *
         * @param sourceKey      定位sourceList的key
         * @param destinationKey 定位destinationList的key
         * @return 移动的这个元素
         * @since 2020/3/9 15:06:59
         */
        public static String lRightPopAndLeftPush(String sourceKey, String destinationKey) {
            log.info("lRightPopAndLeftPush(...) => sourceKey -> {}, destinationKey -> {}",
                    sourceKey, destinationKey);
            String item = redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
            log.info("lRightPopAndLeftPush(...) => item -> {}", item);
            return item;
        }

        /**
         * 【阻塞队列】 从sourceKey对应的sourceList右侧移出一个item, 并将这个item推
         * 入(destinationKey对应的)destinationList的左侧
         * <p>
         * 注: 若sourceKey对应的list中没有item了，则阻塞等待, 直到能从sourceList中移出一个非null的item(或等待时长超时);
         * case1: 等到了一个非null的item, 那么继续下面的push操作，并返回这个item。
         * case2: 超时了，还没等到非null的item, 那么pop出的结果就未null,此时并不会往destinationList进行push。
         * 此时，此方法的返回值是null。
         * <p>
         * 注: 若将(sourceKey对应的)list中的所有元素都pop完了，那么该sourceKey会被删除。
         *
         * @param sourceKey      定位sourceList的key
         * @param destinationKey 定位destinationList的key
         * @param timeout        超时时间
         * @param unit           timeout的单位
         * @return 移动的这个元素
         * @since 2020/3/9 15:06:59
         */
        public static String lRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout,
                                                  TimeUnit unit) {
            log.info("lRightPopAndLeftPush(...) => sourceKey -> {}, destinationKey -> {}, timeout -> {},"
                    + " unit -> {}", sourceKey, destinationKey, timeout, unit);
            String item = redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit);
            log.info("lRightPopAndLeftPush(...) => item -> {}", item);
            return item;
        }

        /**
         * 设置(key对应的)list中对应索引位置index处的元素为item
         * <p>
         * 注: 若key不存在，则会抛出org.springframework.data.redis.RedisSystemException
         * 注: 若索引越界，也会抛出org.springframework.data.redis.RedisSystemException
         *
         * @param key   定位list的key
         * @param index 定位list中的元素的索引
         * @param item  要替换成的值
         * @since 2020/3/9 15:39:50
         */
        public static void lSet(String key, long index, String item) {
            log.info("lSet(...) => key -> {}, index -> {}, item -> {}", key, index, item);
            redisTemplate.opsForList().set(key, index, item);
        }

        /**
         * 通过索引index, 获取(key对应的)list中的元素
         * <p>
         * 注: 若key不存在 或 index超出(key对应的)list的索引范围，那么返回null
         *
         * @param key   定位list的key
         * @param index 定位list中的item的索引
         * @return list中索引index对应的item
         * @since 2020/3/10 0:27:23
         */
        public static String lIndex(String key, long index) {
            log.info("lIndex(...) => key -> {}, index -> {}", key, index);
            String item = redisTemplate.opsForList().index(key, index);
            log.info("lIndex(...) => item -> {}", item);
            return item;
        }

        /**
         * 获取(key对应的)list中索引在[start, end]之间的item集
         * <p>
         * 注: 含start、含end。
         * 注: 当key不存在时，获取到的是空的集合。
         * 注: 当获取的范围比list的范围还要大时，获取到的是这两个范围的交集。
         * <p>
         * 提示: 可通过RedisUtil.ListOps.lRange(key, 0, -1)来获取到该key对应的整个list
         *
         * @param key   定位list的key
         * @param start 起始元素的index
         * @param end   结尾元素的index
         * @return 对应的元素集合
         * @since 2020/3/10 0:34:59
         */
        public static List<String> lRange(String key, long start, long end) {
            log.info("lRange(...) => key -> {}, start -> {}, end -> {}", key, start, end);
            List<String> result = redisTemplate.opsForList().range(key, start, end);
            log.info("lRange(...) => result -> {}", result);
            return result;
        }

        /**
         * 获取(key对应的)list
         *
         * @param key 定位list的key
         * @return (key对应的)list
         * @see RedisHelper.ListOps#lRange(String, long, long)
         * @since 2020/3/10 0:46:50
         */
        public static List<String> lWholeList(String key) {
            log.info("lWholeList(...) => key -> {}", key);
            List<String> result = redisTemplate.opsForList().range(key, 0, -1);
            log.info("lWholeList(...) => result -> {}", result);
            return result;
        }

        /**
         * 获取(key对应的)list的size
         * <p>
         * 注: 当key不存在时，获取到的size为0.
         *
         * @param key 定位list的key
         * @return list的size。
         * @since 2020/3/10 0:48:40
         */
        public static long lSize(String key) {
            log.info("lSize(...) => key -> {}", key);
            Long size = redisTemplate.opsForList().size(key);
            log.info("lSize(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 删除(key对应的)list中，前expectCount个值等于item的项
         * <p>
         * 注: 若expectCount == 0， 则表示删除list中所有的值等于item的项.
         * 注: 若expectCount > 0，  则表示删除从左往右进行
         * 注: 若expectCount < 0，  则表示删除从右往左进行
         * <p>
         * 注: 若list中,值等于item的项的个数少于expectCount时，那么会删除list中所有的值等于item的项。
         * 注: 当key不存在时, 返回0。
         * 注: 若lRemove后， 将(key对应的)list中没有任何元素了，那么该key会被删除。
         *
         * @param key         定位list的key
         * @param expectCount 要删除的item的个数
         * @param item        要删除的item
         * @return 实际删除了的item的个数
         * @since 2020/3/10 0:52:57
         */
        public static long lRemove(String key, long expectCount, String item) {
            log.info("lRemove(...) => key -> {}, expectCount -> {}, item -> {}", key, expectCount, item);
            Long actualCount = redisTemplate.opsForList().remove(key, expectCount, item);
            log.info("lRemove(...) => actualCount -> {}", actualCount);
            if (actualCount == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return actualCount;
        }

        /**
         * 裁剪(即: 对list中的元素取交集。)
         * <p>
         * 举例说明: list中的元素索引范围是[0, 8], 而这个方法传入的[start, end]为 [3, 10]，
         * 那么裁剪就是对[0, 8]和[3, 10]进行取交集， 得到[3, 8], 那么裁剪后
         * 的list中，只剩下(原来裁剪前)索引在[3, 8]之间的元素了。
         * <p>
         * 注: 若裁剪后的(key对应的)list就是空的,那么该key会被删除。
         *
         * @param key   定位list的key
         * @param start 要删除的item集的起始项的索引
         * @param end   要删除的item集的结尾项的索引
         * @since 2020/3/10 1:16:58
         */
        public static void lTrim(String key, long start, long end) {
            log.info("lTrim(...) => key -> {}, start -> {}, end -> {}", key, start, end);
            redisTemplate.opsForList().trim(key, start, end);
        }

    }

    /**
     * set相关操作
     * <p>
     * 提示: set中的元素，不可以重复。
     * 提示: set是无序的。
     * 提示: redis中String的数据结构可参考resources/data-structure/Set(集合)的数据结构(示例一).png
     * redis中String的数据结构可参考resources/data-structure/Set(集合)的数据结构(示例二).png
     *
     * @author JustryDeng
     * @since 2020/3/9 11:30:48
     */
    public static class SetOps {

        /**
         * 向(key对应的)set中添加items
         * <p>
         * 注: 若key不存在，则会自动创建。
         * 注: set中的元素会去重。
         *
         * @param key   定位set的key
         * @param items 要向(key对应的)set中添加的items
         * @return 此次添加操作, 添加到set中的元素的个数
         * @since 2020/3/11 8:16:00
         */
        public static long sAdd(String key, String... items) {
            log.info("sAdd(...) => key -> {}, items -> {}", key, items);
            Long count = redisTemplate.opsForSet().add(key, items);
            log.info("sAdd(...) => count -> {}", count);
            if (count == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return count;
        }

        /**
         * 从(key对应的)set中删除items
         * <p>
         * 注: 若key不存在, 则返回0。
         * 注: 若已经将(key对应的)set中的项删除完了，那么对应的key也会被删除。
         *
         * @param key   定位set的key
         * @param items 要移除的items
         * @return 实际删除了的个数
         * @since 2020/3/11 8:26:43
         */
        public static long sRemove(String key, Object... items) {
            log.info("sRemove(...) => key -> {}, items -> {}", key, items);
            Long count = redisTemplate.opsForSet().remove(key, items);
            log.info("sRemove(...) => count -> {}", count);
            if (count == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return count;
        }

        /**
         * 从(key对应的)set中随机移出一个item, 并返回这个item
         * <p>
         * 注: 因为set是无序的，所以移出的这个item,是随机的; 并且，哪怕
         * 是数据一样的set,多次测试移出操作,移除的元素也是随机的。
         * <p>
         * 注: 若已经将(key对应的)set中的项pop完了，那么对应的key会被删除。
         *
         * @param key 定位set的key
         * @return 移出的项
         * @since 2020/3/11 8:32:40
         */
        public static String sPop(String key) {
            log.info("sPop(...) => key -> {}", key);
            String popItem = redisTemplate.opsForSet().pop(key);
            log.info("sPop(...) => popItem -> {}", popItem);
            return popItem;
        }

        /**
         * 将(sourceKey对应的)sourceSet中的元素item, 移动到(destinationKey对应的)destinationSet中
         * <p>
         * 注: 当sourceKey不存在时， 返回false
         * 注: 当item不存在时， 返回false
         * 注: 若destinationKey不存在， 那么在移动时会自动创建
         * 注: 若已经将(sourceKey对应的)set中的项move出去完了，那么对应的sourceKey会被删除。
         *
         * @param sourceKey      定位sourceSet的key
         * @param item           要移动的项目
         * @param destinationKey 定位destinationSet的key
         * @return 移动成功与否
         * @since 2020/3/11 8:43:32
         */
        public static boolean sMove(String sourceKey, String item, String destinationKey) {
            Boolean result = redisTemplate.opsForSet().move(sourceKey, item, destinationKey);
            log.info("sMove(...) => sourceKey -> {}, destinationKey -> {}, item -> {}",
                    sourceKey, destinationKey, item);
            log.info("sMove(...) =>  result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 获取(key对应的)set中的元素个数
         * <p>
         * 注: 若key不存在，则返回0
         *
         * @param key 定位set的key
         * @return (key对应的)set中的元素个数
         * @since 2020/3/11 8:57:19
         */
        public static long sSize(String key) {
            log.info("sSize(...) => key -> {}", key);
            Long size = redisTemplate.opsForSet().size(key);
            log.info("sSize(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 判断(key对应的)set中是否含有item
         * <p>
         * 注: 若key不存在，则返回false。
         *
         * @param key  定位set的key
         * @param item 被查找的项
         * @return (key对应的)set中是否含有item
         * @since 2020/3/11 9:03:29
         */
        public static boolean sIsMember(String key, Object item) {
            log.info("sSize(...) => key -> {}, size -> {}", key, item);
            Boolean result = redisTemplate.opsForSet().isMember(key, item);
            log.info("sSize(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 获取两个(key对应的)Set的交集
         * <p>
         * 注: 若不存在任何交集，那么返回空的集合(, 而不是null)
         * 注: 若其中一个key不存在(或两个key都不存在)，那么返回空的集合(, 而不是null)
         *
         * @param key      定位其中一个set的键
         * @param otherKey 定位其中另一个set的键
         * @return item交集
         * @since 2020/3/11 9:31:25
         */
        public static Set<String> sIntersect(String key, String otherKey) {
            log.info("sIntersect(...) => key -> {}, otherKey -> {}", key, otherKey);
            Set<String> intersectResult = redisTemplate.opsForSet().intersect(key, otherKey);
            log.info("sIntersect(...) => intersectResult -> {}", intersectResult);
            return intersectResult;
        }

        /**
         * 获取多个(key对应的)Set的交集
         * <p>
         * 注: 若不存在任何交集，那么返回空的集合(, 而不是null)
         * 注: 若>=1个key不存在，那么返回空的集合(, 而不是null)
         *
         * @param key       定位其中一个set的键
         * @param otherKeys 定位其它set的键集
         * @return item交集
         * @since 2020/3/11 9:39:23
         */
        public static Set<String> sIntersect(String key, Collection<String> otherKeys) {
            log.info("sIntersect(...) => key -> {}, otherKeys -> {}", key, otherKeys);
            Set<String> intersectResult = redisTemplate.opsForSet().intersect(key, otherKeys);
            log.info("sIntersect(...) => intersectResult -> {}", intersectResult);
            return intersectResult;
        }

        /**
         * 获取两个(key对应的)Set的交集, 并将结果add到storeKey对应的Set中。
         * <p>
         * case1: 交集不为空, storeKey不存在， 则 会创建对应的storeKey，并将交集添加到(storeKey对应的)set中
         * case2: 交集不为空, storeKey已存在， 则 会清除原(storeKey对应的)set中所有的项，然后将交集添加到(storeKey对应的)set中
         * case3: 交集为空, 则不进行下面的操作, 直接返回0
         * <p>
         * 注: 求交集的部分，详见{@link RedisHelper.SetOps#sIntersect(String, String)}
         *
         * @param key      定位其中一个set的键
         * @param otherKey 定位其中另一个set的键
         * @param storeKey 定位(要把交集添加到哪个)set的key
         * @return add到(storeKey对应的)Set后, 该set对应的size
         * @since 2020/3/11 9:46:46
         */
        public static long sIntersectAndStore(String key, String otherKey, String storeKey) {
            log.info("sIntersectAndStore(...) => key -> {}, otherKey -> {}, storeKey -> {}",
                    key, otherKey, storeKey);
            Long size = redisTemplate.opsForSet().intersectAndStore(key, otherKey, storeKey);
            log.info("sIntersectAndStore(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 获取多个(key对应的)Set的交集, 并将结果add到storeKey对应的Set中。
         * <p>
         * case1: 交集不为空, storeKey不存在， 则 会创建对应的storeKey，并将交集添加到(storeKey对应的)set中
         * case2: 交集不为空, storeKey已存在， 则 会清除原(storeKey对应的)set中所有的项，然后将交集添加到(storeKey对应的)set中
         * case3: 交集为空, 则不进行下面的操作, 直接返回0
         * <p>
         * 注: 求交集的部分，详见{@link RedisHelper.SetOps#sIntersect(String, Collection)}
         *
         * @since 2020/3/11 11:04:29
         */
        public static long sIntersectAndStore(String key, Collection<String> otherKeys, String storeKey) {
            log.info("sIntersectAndStore(...) => key -> {}, otherKeys -> {}, storeKey -> {}", key, otherKeys, storeKey);
            Long size = redisTemplate.opsForSet().intersectAndStore(key, otherKeys, storeKey);
            log.info("sIntersectAndStore(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 获取两个(key对应的)Set的并集
         * <p>
         * 注: 并集中的元素也是唯一的，这是Set保证的。
         *
         * @param key      定位其中一个set的键
         * @param otherKey 定位其中另一个set的键
         * @return item并集
         * @since 2020/3/11 11:18:35
         */
        public static Set<String> sUnion(String key, String otherKey) {
            log.info("sUnion(...) => key -> {}, otherKey -> {}", key, otherKey);
            Set<String> unionResult = redisTemplate.opsForSet().union(key, otherKey);
            log.info("sUnion(...) => unionResult -> {}", unionResult);
            return unionResult;
        }

        /**
         * 获取两个(key对应的)Set的并集
         * <p>
         * 注: 并集中的元素也是唯一的，这是Set保证的。
         *
         * @param key       定位其中一个set的键
         * @param otherKeys 定位其它set的键集
         * @return item并集
         * @since 2020/3/11 11:18:35
         */
        public static Set<String> sUnion(String key, Collection<String> otherKeys) {
            log.info("sUnion(...) => key -> {}, otherKeys -> {}", key, otherKeys);
            Set<String> unionResult = redisTemplate.opsForSet().union(key, otherKeys);
            log.info("sUnion(...) => unionResult -> {}", unionResult);
            return unionResult;
        }

        /**
         * 获取两个(key对应的)Set的并集, 并将结果add到storeKey对应的Set中。
         * <p>
         * case1: 并集不为空, storeKey不存在， 则 会创建对应的storeKey，并将并集添加到(storeKey对应的)set中
         * case2: 并集不为空, storeKey已存在， 则 会清除原(storeKey对应的)set中所有的项，然后将并集添加到(storeKey对应的)set中
         * case3: 并集为空, 则不进行下面的操作, 直接返回0
         * <p>
         * 注: 求并集的部分，详见{@link RedisHelper.SetOps#sUnion(String, String)}
         *
         * @param key      定位其中一个set的键
         * @param otherKey 定位其中另一个set的键
         * @param storeKey 定位(要把并集添加到哪个)set的key
         * @return add到(storeKey对应的)Set后, 该set对应的size
         * @since 2020/3/11 12:26:24
         */
        public static long sUnionAndStore(String key, String otherKey, String storeKey) {
            log.info("sUnionAndStore(...) => key -> {}, otherKey -> {}, storeKey -> {}",
                    key, otherKey, storeKey);
            Long size = redisTemplate.opsForSet().unionAndStore(key, otherKey, storeKey);
            log.info("sUnionAndStore(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 获取两个(key对应的)Set的并集, 并将结果add到storeKey对应的Set中。
         * <p>
         * case1: 并集不为空, storeKey不存在， 则 会创建对应的storeKey，并将并集添加到(storeKey对应的)set中
         * case2: 并集不为空, storeKey已存在， 则 会清除原(storeKey对应的)set中所有的项，然后将并集添加到(storeKey对应的)set中
         * case3: 并集为空, 则不进行下面的操作, 直接返回0
         * <p>
         * 注: 求并集的部分，详见{@link RedisHelper.SetOps#sUnion(String, Collection)}
         *
         * @param key       定位其中一个set的键
         * @param otherKeys 定位其它set的键集
         * @param storeKey  定位(要把并集添加到哪个)set的key
         * @return add到(storeKey对应的)Set后, 该set对应的size
         * @since 2020/3/11 12:26:24
         */
        public static long sUnionAndStore(String key, Collection<String> otherKeys, String storeKey) {
            log.info("sUnionAndStore(...) => key -> {}, otherKeys -> {}, storeKey -> {}",
                    key, otherKeys, storeKey);
            Long size = redisTemplate.opsForSet().unionAndStore(key, otherKeys, storeKey);
            log.info("sUnionAndStore(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 获取 (key对应的)Set 减去 (otherKey对应的)Set 的差集
         * <p>
         * 注: 如果被减数key不存在， 那么结果为空的集合(，而不是null)
         * 注: 如果被减数key存在，但减数key不存在， 那么结果即为(被减数key对应的)Set
         *
         * @param key      定位"被减数set"的键
         * @param otherKey 定位"减数set"的键
         * @return item差集
         * @since 2020/3/11 14:03:57
         */
        public static Set<String> sDifference(String key, String otherKey) {
            log.info("sDifference(...) => key -> {}, otherKey -> {}",
                    key, otherKey);
            Set<String> differenceResult = redisTemplate.opsForSet().difference(key, otherKey);
            log.info("sDifference(...) => differenceResult -> {}", differenceResult);
            return differenceResult;
        }

        /**
         * 获取 (key对应的)Set 减去 (otherKeys对应的)Sets 的差集
         * <p>
         * 注: 如果被减数key不存在， 那么结果为空的集合(，而不是null)
         * 注: 如果被减数key存在，但减数key不存在， 那么结果即为(被减数key对应的)Set
         * <p>
         * 提示: 当有多个减数时， 被减数先减去哪一个减数，后减去哪一个减数，是无所谓的，是不影响最终结果的。
         *
         * @param key       定位"被减数set"的键
         * @param otherKeys 定位"减数集sets"的键集
         * @return item差集
         * @since 2020/3/11 14:03:57
         */
        public static Set<String> sDifference(String key, Collection<String> otherKeys) {
            log.info("sDifference(...) => key -> {}, otherKeys -> {}", key, otherKeys);
            Set<String> differenceResult = redisTemplate.opsForSet().difference(key, otherKeys);
            log.info("sDifference(...) => differenceResult -> {}", differenceResult);
            return differenceResult;
        }

        /**
         * 获取 (key对应的)Set 减去 (otherKey对应的)Set 的差集, 并将结果add到storeKey对应的Set中。
         * <p>
         * case1: 差集不为空, storeKey不存在， 则 会创建对应的storeKey，并将差集添加到(storeKey对应的)set中
         * case2: 差集不为空, storeKey已存在， 则 会清除原(storeKey对应的)set中所有的项，然后将差集添加到(storeKey对应的)set中
         * case3: 差集为空, 则不进行下面的操作, 直接返回0
         * <p>
         * 注: 求并集的部分，详见{@link RedisHelper.SetOps#sDifference(String, String)}
         *
         * @param key      定位"被减数set"的键
         * @param otherKey 定位"减数set"的键
         * @param storeKey 定位(要把差集添加到哪个)set的key
         * @return add到(storeKey对应的)Set后, 该set对应的size
         * @since 2020/3/11 14:33:36
         */
        public static long sDifferenceAndStore(String key, String otherKey, String storeKey) {
            log.info("sDifferenceAndStore(...) => key -> {}, otherKey -> {}, storeKey -> {}",
                    key, otherKey, storeKey);
            Long size = redisTemplate.opsForSet().differenceAndStore(key, otherKey, storeKey);
            log.info("sDifferenceAndStore(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 获取 (key对应的)Set 减去 (otherKey对应的)Set 的差集, 并将结果add到storeKey对应的Set中。
         * <p>
         * case1: 差集不为空, storeKey不存在， 则 会创建对应的storeKey，并将差集添加到(storeKey对应的)set中
         * case2: 差集不为空, storeKey已存在， 则 会清除原(storeKey对应的)set中所有的项，然后将差集添加到(storeKey对应的)set中
         * case3: 差集为空, 则不进行下面的操作, 直接返回0
         * <p>
         * 注: 求并集的部分，详见{@link RedisHelper.SetOps#sDifference(String, String)}
         *
         * @param key       定位"被减数set"的键
         * @param otherKeys 定位"减数集sets"的键集
         * @param storeKey  定位(要把差集添加到哪个)set的key
         * @return add到(storeKey对应的)Set后, 该set对应的size
         * @since 2020/3/11 14:33:36
         */
        public static long sDifferenceAndStore(String key, Collection<String> otherKeys, String storeKey) {
            log.info("sDifferenceAndStore(...) => key -> {}, otherKeys -> {}, storeKey -> {}",
                    key, otherKeys, storeKey);
            Long size = redisTemplate.opsForSet().differenceAndStore(key, otherKeys, storeKey);
            log.info("sDifferenceAndStore(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 获取key对应的set
         * <p>
         * 注: 若key不存在, 则返回的是空的set(, 而不是null)
         *
         * @param key 定位set的key
         * @return (key对应的)set
         * @since 2020/3/11 14:49:39
         */
        public static Set<String> sMembers(String key) {
            log.info("sMembers(...) => key -> {}", key);
            Set<String> members = redisTemplate.opsForSet().members(key);
            log.info("sMembers(...) => members -> {}", members);
            return members;
        }

        /**
         * 从key对应的set中随机获取一项
         *
         * @param key 定位set的key
         * @return 随机获取到的项
         * @since 2020/3/11 14:54:58
         */
        public static String sRandomMember(String key) {
            log.info("sRandomMember(...) => key -> {}", key);
            String randomItem = redisTemplate.opsForSet().randomMember(key);
            log.info("sRandomMember(...) => randomItem -> {}", randomItem);
            return randomItem;
        }

        /**
         * 从key对应的set中获取count次随机项(, set中的同一个项可能被多次获取)
         * <p>
         * 注: count可大于set的size。
         * 注: 取出来的结果里可能存在相同的值。
         *
         * @param key   定位set的key
         * @param count 要取多少项
         * @return 随机获取到的项集
         * @since 2020/3/11 14:54:58
         */
        public static List<String> sRandomMembers(String key, long count) {
            log.info("sRandomMembers(...) => key -> {}, count -> {}", key, count);
            List<String> randomItems = redisTemplate.opsForSet().randomMembers(key, count);
            log.info("sRandomMembers(...) => randomItems -> {}", randomItems);
            return randomItems;
        }

        /**
         * 从key对应的set中随机获取count个项
         * <p>
         * 注: 若count >= set的size, 那么返回的即为这个key对应的set。
         * 注: 取出来的结果里没有重复的项。
         *
         * @param key   定位set的key
         * @param count 要取多少项
         * @return 随机获取到的项集
         * @since 2020/3/11 14:54:58
         */
        public static Set<String> sDistinctRandomMembers(String key, long count) {
            log.info("sDistinctRandomMembers(...) => key -> {}, count -> {}", key, count);
            Set<String> distinctRandomItems = redisTemplate.opsForSet().distinctRandomMembers(key, count);
            log.info("sDistinctRandomMembers(...) => distinctRandomItems -> {}", distinctRandomItems);
            return distinctRandomItems;
        }

        /**
         * 根据options匹配到(key对应的)set中的对应的item, 并返回对应的item集
         * <p>
         * <p>
         * 注: ScanOptions实例的创建方式举例:
         * 1、ScanOptions.NONE
         * 2、ScanOptions.scanOptions().match("n??e").build()
         *
         * @param key     定位set的key
         * @param options 匹配set中的item的条件
         *                注: ScanOptions.NONE表示全部匹配。
         *                注: ScanOptions.scanOptions().match(pattern).build()表示按照pattern匹配,
         *                其中pattern中可以使用通配符 * ? 等,
         *                * 表示>=0个字符
         *                ？ 表示有且只有一个字符
         *                此处的匹配规则与{@link RedisHelper.KeyOps#keys(String)}处的一样。
         * @return 匹配到的(key对应的)set中的项
         * @since 2020/3/9 10:49:27
         */
        public static Cursor<String> sScan(String key, ScanOptions options) {
            log.info("sScan(...) => key -> {}, options -> {}", key, JSON.toJSONString(options));
            Cursor<String> cursor = redisTemplate.opsForSet().scan(key, options);
            log.info("sScan(...) => cursor -> {}", JSON.toJSONString(cursor));
            return cursor;
        }
    }

    /**
     * ZSet相关操作
     * <p>
     * 特别说明: ZSet是有序的,
     * 不仅体现在： redis中的存储上有序。
     * 还体现在:   此工具类ZSetOps中返回值类型为Set<?>的方法, 实际返回类型是LinkedHashSet<?>
     * <p>
     * 提示: redis中的ZSet, 一定程度等于redis中的Set + redis中的Hash的结合体。
     * 提示: redis中String的数据结构可参考resources/data-structure/ZSet(有序集合)的数据结构(示例一).png
     * redis中String的数据结构可参考resources/data-structure/ZSet(有序集合)的数据结构(示例二).png
     * 提示: ZSet中的entryKey即为成员项， entryValue即为这个成员项的分值, ZSet根据成员的分值，来堆成员进行排序。
     *
     * @author JustryDeng
     * @since 2020/3/11 15:28:48
     */
    public static class ZSetOps {

        /**
         * 向(key对应的)zset中添加(item, score)
         * <p>
         * 注: item为entryKey成员项， score为entryValue分数值。
         * <p>
         * 注: 若(key对应的)zset中已存在(与此次要添加的项)相同的item项，那么此次添加操作会失败，返回false；
         * 但是！！！ zset中原item的score会被更新为此次add的相同item项的score。
         * 所以, 也可以通过zAdd达到更新item对应score的目的。
         * <p>
         * 注: score可为正、可为负、可为0; 总之, double范围内都可以。
         * <p>
         * 注: 若score的值一样，则按照item排序。
         *
         * @param key   定位set的key
         * @param item  要往(key对应的)zset中添加的成员项
         * @param score item的分值
         * @return 是否添加成功
         * @since 2020/3/11 15:35:30
         */
        public static boolean zAdd(String key, String item, double score) {
            log.info("zAdd(...) => key -> {}, item -> {}, score -> {}", key, item, score);
            Boolean result = redisTemplate.opsForZSet().add(key, item, score);
            log.info("zAdd(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 批量添加entry<item, score>
         * <p>
         * 注: 若entry<item, score>集中存在item相同的项(, score不一样)，那么redis在执行真正的批量add操作前,会
         * 将其中一个item过滤掉。
         * 注: 同样的，若(key对应的)zset中已存在(与此次要添加的项)相同的item项，那么此次批量添加操作中，
         * 对该item项的添加会失败，会失败，成功计数器不会加1；但是！！！ zset中原item的score会被更新为此
         * 次add的相同item项的score。所以, 也可以通过zAdd达到更新item对应score的目的。
         *
         * @param key     定位set的key
         * @param entries 要添加的entry<item, score>集
         * @return 本次添加进(key对应的)zset中的entry的个数
         * @since 2020/3/11 16:45:45
         */
        public static long zAdd(String key, Set<ZSetOperations.TypedTuple<String>> entries) {
            log.info("zAdd(...) => key -> {}, entries -> {}", key, JSON.toJSONString(entries));
            Long count = redisTemplate.opsForZSet().add(key, entries);
            log.info("zAdd(...) => count -> {}", count);
            if (count == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return count;
        }

        /**
         * 从(key对应的)zset中移除项
         * <p>
         * 注:若key不存在，则返回0
         *
         * @param key   定位set的key
         * @param items 要移除的项集
         * @return 实际移除了的项的个数
         * @since 2020/3/11 17:20:12
         */
        public static long zRemove(String key, Object... items) {
            log.info("zRemove(...) => key -> {}, items -> {}", key, items);
            Long count = redisTemplate.opsForZSet().remove(key, items);
            log.info("zRemove(...) => count -> {}", count);
            if (count == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return count;
        }

        /**
         * 移除(key对应的)zset中, 排名范围在[startIndex, endIndex]内的item
         * <p>
         * 注:默认的，按score.item升序排名， 排名从0开始
         * <p>
         * 注: 类似于List中的索引, 排名可以分为多个方式:
         * 从前到后(正向)的排名: 0、1、2...
         * 从后到前(反向)的排名: -1、-2、-3...
         * <p>
         * 注: 不论是使用正向排名，还是使用反向排名, 使用此方法时, 应保证 startRange代表的元素的位置
         * 在endRange代表的元素的位置的前面， 如:
         * 示例一: RedisHelper.ZSetOps.zRemoveRange("name", 0, 2);
         * 示例二: RedisHelper.ZSetOps.zRemoveRange("site", -2, -1);
         * 示例三: RedisHelper.ZSetOps.zRemoveRange("foo", 0, -1);
         * <p>
         * 注:若key不存在，则返回0
         *
         * @param key        定位set的key
         * @param startRange 开始项的排名
         * @param endRange   结尾项的排名
         * @return 实际移除了的项的个数
         * @since 2020/3/11 17:20:12
         */
        public static long zRemoveRange(String key, long startRange, long endRange) {
            log.info("zRemoveRange(...) => key -> {}, startRange -> {}, endRange -> {}",
                    key, startRange, endRange);
            Long count = redisTemplate.opsForZSet().removeRange(key, startRange, endRange);
            log.info("zRemoveRange(...) => count -> {}", count);
            if (count == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return count;
        }

        /**
         * 移除(key对应的)zset中, score范围在[minScore, maxScore]内的item
         * <p>
         * 提示: 虽然删除范围包含两侧的端点(即:包含minScore和maxScore), 但是由于double存在精度问题，所以建议:
         * 设置值时，minScore应该设置得比要删除的项里，最小的score还小一点
         * maxScore应该设置得比要删除的项里，最大的score还大一点
         * 追注: 本人简单测试了几组数据，暂未出现精度问题。
         * <p>
         * 注:若key不存在，则返回0
         *
         * @param key      定位set的key
         * @param minScore score下限(含这个值)
         * @param maxScore score上限(含这个值)
         * @return 实际移除了的项的个数
         * @since 2020/3/11 17:20:12
         */
        public static long zRemoveRangeByScore(String key, double minScore, double maxScore) {
            log.info("zRemoveRangeByScore(...) => key -> {}, startIndex -> {}, startIndex -> {}",
                    key, minScore, maxScore);
            Long count = redisTemplate.opsForZSet().removeRangeByScore(key, minScore, maxScore);
            log.info("zRemoveRangeByScore(...) => count -> {}", count);
            if (count == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return count;
        }

        /**
         * 增/减 (key对应的zset中,)item的分数值
         *
         * @param key   定位zset的key
         * @param item  项
         * @param delta 变化量(正 - 增, 负 - 减)
         * @return 修改后的score值
         * @since 2020/3/12 8:55:38
         */
        public static double zIncrementScore(String key, String item, double delta) {
            log.info("zIncrementScore(...) => key -> {}, item -> {}, delta -> {}", key, item, delta);
            Double scoreValue = redisTemplate.opsForZSet().incrementScore(key, item, delta);
            log.info("zIncrementScore(...) => scoreValue -> {}", scoreValue);
            if (scoreValue == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return scoreValue;
        }

        /**
         * 返回item在(key对应的)zset中的(按score从小到大的)排名
         * <p>
         * 注: 排名从0开始。 即意味着，此方法等价于: 返回item在(key对应的)zset中的位置索引。
         * 注: 若key或item不存在， 返回null。
         * 注: 排序规则是score,item, 即:优先以score排序，若score相同，则再按item排序。
         *
         * @param key  定位zset的key
         * @param item 项
         * @return 排名(等价于 : 索引)
         * @since 2020/3/12 9:14:09
         */
        public static long zRank(String key, Object item) {
            log.info("zRank(...) => key -> {}, item -> {}", key, item);
            Long rank = redisTemplate.opsForZSet().rank(key, item);
            log.info("zRank(...) => rank -> {}", rank);
            if (rank == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return rank;
        }

        /**
         * 返回item在(key对应的)zset中的(按score从大到小的)排名
         * <p>
         * 注: 排名从0开始。补充: 因为是按score从大到小排序的, 所以最大score对应的item的排名为0。
         * 注: 若key或item不存在， 返回null。
         * 注: 排序规则是score,item, 即:优先以score排序，若score相同，则再按item排序。
         *
         * @param key  定位zset的key
         * @param item 项
         * @return 排名(等价于 : 索引)
         * @since 2020/3/12 9:14:09
         */
        public static long zReverseRank(String key, Object item) {
            log.info("zReverseRank(...) => key -> {}, item -> {}", key, item);
            Long reverseRank = redisTemplate.opsForZSet().reverseRank(key, item);
            log.info("zReverseRank(...) => reverseRank -> {}", reverseRank);
            if (reverseRank == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return reverseRank;
        }

        /**
         * 根据索引位置， 获取(key对应的)zset中排名处于[start, end]中的item项集
         * <p>
         * 注: 不论是使用正向排名，还是使用反向排名, 使用此方法时, 应保证 startIndex代表的元素的
         * 位置在endIndex代表的元素的位置的前面， 如:
         * 示例一: RedisHelper.ZSetOps.zRange("name", 0, 2);
         * 示例二: RedisHelper.ZSetOps.zRange("site", -2, -1);
         * 示例三: RedisHelper.ZSetOps.zRange("foo", 0, -1);
         * <p>
         * 注: 若key不存在, 则返回空的集合。
         * <p>
         * 注: 当[start, end]的范围比实际zset的范围大时, 返回范围上"交集"对应的项集合。
         *
         * @param key   定位zset的key
         * @param start 排名开始位置
         * @param end   排名结束位置
         * @return 对应的item项集
         * @since 2020/3/12 9:50:40
         */
        public static Set<String> zRange(String key, long start, long end) {
            log.info("zRange(...) => key -> {}, start -> {}, end -> {}", key, start, end);
            Set<String> result = redisTemplate.opsForZSet().range(key, start, end);
            log.info("zRange(...) => result -> {}", result);
            return result;
        }

        /**
         * 获取(key对应的)zset中的所有item项
         *
         * @param key 定位zset的键
         * @return (key对应的)zset中的所有item项
         * @see RedisHelper.ZSetOps#zRange(String, long, long)
         * @since 2020/3/12 10:02:07
         */
        public static Set<String> zWholeZSetItem(String key) {
            log.info("zWholeZSetItem(...) => key -> {}", key);
            Set<String> result = redisTemplate.opsForZSet().range(key, 0, -1);
            log.info("zWholeZSetItem(...) =>result -> {}", result);
            return result;
        }

        /**
         * 根据索引位置， 获取(key对应的)zset中排名处于[start, end]中的entry集
         * <p>
         * 注: 不论是使用正向排名，还是使用反向排名, 使用此方法时, 应保证 startIndex代表的元素的
         * 位置在endIndex代表的元素的位置的前面， 如:
         * 示例一: RedisHelper.ZSetOps.zRange("name", 0, 2);
         * 示例二: RedisHelper.ZSetOps.zRange("site", -2, -1);
         * 示例三: RedisHelper.ZSetOps.zRange("foo", 0, -1);
         * <p>
         * 注: 若key不存在, 则返回空的集合。
         * <p>
         * 注: 当[start, end]的范围比实际zset的范围大时, 返回范围上"交集"对应的项集合。
         * <p>
         * 注: 此方法和{@link RedisHelper.ZSetOps#zRange(String, long, long)}类似，不过此方法返回的不是item集， 而是entry集
         *
         * @param key   定位zset的key
         * @param start 排名开始位置
         * @param end   排名结束位置
         * @return 对应的entry集
         * @since 2020/3/12 9:50:40
         */
        public static Set<ZSetOperations.TypedTuple<String>> zRangeWithScores(String key, long start, long end) {
            log.info("zRangeWithScores(...) => key -> {}, start -> {}, end -> {}", key, start, end);
            Set<ZSetOperations.TypedTuple<String>> entries = redisTemplate.opsForZSet().rangeWithScores(key, start, end);
            log.info("zRangeWithScores(...) => entries -> {}", JSON.toJSONString(entries));
            return entries;
        }

        /**
         * 获取(key对应的)zset中的所有entry
         *
         * @param key 定位zset的键
         * @return (key对应的)zset中的所有entry
         * @see RedisHelper.ZSetOps#zRangeWithScores(String, long, long)
         * @since 2020/3/12 10:02:07
         */
        public static Set<ZSetOperations.TypedTuple<String>> zWholeZSetEntry(String key) {
            log.info("zWholeZSetEntry(...) => key -> {}", key);
            Set<ZSetOperations.TypedTuple<String>> entries = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
            log.info("zWholeZSetEntry(...) => entries -> {}", JSON.toJSONString(entries));
            return entries;
        }

        /**
         * 根据score， 获取(key对应的)zset中分数值处于[minScore, maxScore]中的item项集
         * <p>
         * 注: 若key不存在, 则返回空的集合。
         * 注: 当[minScore, maxScore]的范围比实际zset中score的范围大时, 返回范围上"交集"对应的项集合。
         * <p>
         * 提示: 虽然删除范围包含两侧的端点(即:包含minScore和maxScore), 但是由于double存在精度问题，所以建议:
         * 设置值时，minScore应该设置得比要删除的项里，最小的score还小一点
         * maxScore应该设置得比要删除的项里，最大的score还大一点
         * 追注: 本人简单测试了几组数据，暂未出现精度问题。
         *
         * @param key      定位zset的key
         * @param minScore score下限
         * @param maxScore score上限
         * @return 对应的item项集
         * @since 2020/3/12 9:50:40
         */
        public static Set<String> zRangeByScore(String key, double minScore, double maxScore) {
            log.info("zRangeByScore(...) => key -> {}, minScore -> {}, maxScore -> {}", key, minScore, maxScore);
            Set<String> items = redisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore);
            log.info("zRangeByScore(...) => items -> {}", items);
            return items;
        }

        /**
         * 根据score， 获取(key对应的)zset中分数值处于[minScore, maxScore]中的, score处于[minScore,
         * 排名大于等于offset的count个item项
         * <p>
         * 特别注意: 对于不是特别熟悉redis的人来说, offset 和 count最好都使用正数， 避免引起理解上的歧义。
         * <p>
         * 注: 若key不存在, 则返回空的集合。
         * <p>
         * 提示: 虽然删除范围包含两侧的端点(即:包含minScore和maxScore), 但是由于double存在精度问题，所以建议:
         * 设置值时，minScore应该设置得比要删除的项里，最小的score还小一点
         * maxScore应该设置得比要删除的项里，最大的score还大一点
         * 追注: 本人简单测试了几组数据，暂未出现精度问题。
         *
         * @param key      定位zset的key
         * @param minScore score下限
         * @param maxScore score上限
         * @param offset   偏移量(即:排名下限)
         * @param count    期望获取到的元素个数
         * @return 对应的item项集
         * @since 2020/3/12 9:50:40
         */
        public static Set<String> zRangeByScore(String key, double minScore, double maxScore,
                                                long offset, long count) {
            log.info("zRangeByScore(...) => key -> {}, minScore -> {}, maxScore -> {}, offset -> {}, "
                    + "count -> {}", key, minScore, maxScore, offset, count);
            Set<String> items = redisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore, offset, count);
            log.info("zRangeByScore(...) => items -> {}", items);
            return items;
        }

        /**
         * 获取(key对应的)zset中的所有score处于[minScore, maxScore]中的entry
         *
         * @param key      定位zset的键
         * @param minScore score下限
         * @param maxScore score上限
         * @return (key对应的)zset中的所有score处于[minScore, maxScore]中的entry
         * @see RedisHelper.ZSetOps#zRangeByScore(String, double, double)
         * <p>
         * 注: 若key不存在, 则返回空的集合。
         * 注: 当[minScore, maxScore]的范围比实际zset中score的范围大时, 返回范围上"交集"对应的项集合。
         * @since 2020/3/12 10:02:07
         */
        public static Set<ZSetOperations.TypedTuple<String>> zRangeByScoreWithScores(String key, double minScore, double maxScore) {
            log.info("zRangeByScoreWithScores(...) => key -> {}, minScore -> {}, maxScore -> {}",
                    key, minScore, maxScore);
            Set<ZSetOperations.TypedTuple<String>> entries = redisTemplate.opsForZSet().rangeByScoreWithScores(key, minScore, maxScore);
            log.info("zRangeByScoreWithScores(...) => entries -> {}", JSON.toJSONString(entries));
            return entries;
        }

        /**
         * 获取(key对应的)zset中, score处于[minScore, maxScore]里的、排名大于等于offset的count个entry
         * <p>
         * 特别注意: 对于不是特别熟悉redis的人来说, offset 和 count最好都使用正数， 避免引起理解上的歧义。
         *
         * @param key      定位zset的键
         * @param minScore score下限
         * @param maxScore score上限
         * @param offset   偏移量(即:排名下限)
         * @param count    期望获取到的元素个数
         * @return [startIndex, endIndex] & [minScore, maxScore]里的entry
         * @since 2020/3/12 11:09:06
         */
        public static Set<ZSetOperations.TypedTuple<String>> zRangeByScoreWithScores(String key, double minScore,
                                                                                     double maxScore, long offset,
                                                                                     long count) {
            log.info("zRangeByScoreWithScores(...) => key -> {}, minScore -> {}, maxScore -> {},"
                            + " offset -> {}, count -> {}",
                    key, minScore, maxScore, offset, count);
            Set<ZSetOperations.TypedTuple<String>> entries = redisTemplate.opsForZSet().rangeByScoreWithScores(key, minScore,
                    maxScore, offset, count);
            log.info("zRangeByScoreWithScores(...) => entries -> {}", JSON.toJSONString(entries));
            return entries;
        }


        /**
         * 获取时, 先按score倒序, 然后根据索引位置， 获取(key对应的)zset中排名处于[start, end]中的item项集
         *
         * @see RedisHelper.ZSetOps#zRange(String, long, long)。 只是zReverseRange这里会提前多一个倒序。
         */
        public static Set<String> zReverseRange(String key, long start, long end) {
            log.info("zReverseRange(...) => key -> {}, start -> {}, end -> {}", key, start, end);
            Set<String> entries = redisTemplate.opsForZSet().reverseRange(key, start, end);
            log.info("zReverseRange(...) => entries -> {}", entries);
            return entries;
        }

        /**
         * 获取时, 先按score倒序, 然后根据索引位置， 获取(key对应的)zset中排名处于[start, end]中的entry集
         *
         * @see RedisHelper.ZSetOps#zRangeWithScores(String, long, long)。 只是zReverseRangeWithScores这里会提前多一个倒序。
         */
        public static Set<ZSetOperations.TypedTuple<String>> zReverseRangeWithScores(String key, long start, long end) {
            log.info("zReverseRangeWithScores(...) => key -> {}, start -> {}, end -> {}", key, start, end);
            Set<ZSetOperations.TypedTuple<String>> entries = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
            log.info("zReverseRangeWithScores(...) => entries -> {}", JSON.toJSONString(entries));
            return entries;
        }

        /**
         * 获取时, 先按score倒序, 然后根据score， 获取(key对应的)zset中分数值处于[minScore, maxScore]中的item项集
         *
         * @see RedisHelper.ZSetOps#zRangeByScore(String, double, double)。 只是zReverseRangeByScore这里会提前多一个倒序。
         */
        public static Set<String> zReverseRangeByScore(String key, double minScore, double maxScore) {
            log.info("zReverseRangeByScore(...) => key -> {}, minScore -> {}, maxScore -> {}",
                    key, minScore, maxScore);
            Set<String> items = redisTemplate.opsForZSet().reverseRangeByScore(key, minScore, maxScore);
            log.info("zReverseRangeByScore(...) => items -> {}", items);
            return items;
        }

        /**
         * 获取时, 先按score倒序, 然后获取(key对应的)zset中的所有score处于[minScore, maxScore]中的entry
         *
         * @see RedisHelper.ZSetOps#zRangeByScoreWithScores(String, double, double)。 只是zReverseRangeByScoreWithScores这里会提前多一个倒序。
         */
        public static Set<ZSetOperations.TypedTuple<String>> zReverseRangeByScoreWithScores(String key, double minScore, double maxScore) {
            log.info("zReverseRangeByScoreWithScores(...) => key -> {}, minScore -> {}, maxScore -> {}",
                    key, minScore, maxScore);
            Set<ZSetOperations.TypedTuple<String>> entries = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key,
                    minScore, maxScore);
            log.info("zReverseRangeByScoreWithScores(...) => entries -> {}", JSON.toJSONString(entries));
            return entries;
        }

        /**
         * 获取时, 先按score倒序, 然后根据score， 获取(key对应的)zset中分数值处于[minScore, maxScore]中的,
         * score处于[minScore,排名大于等于offset的count个item项
         *
         * @see RedisHelper.ZSetOps#zRangeByScore(String, double, double, long, long)。 只是zReverseRangeByScore这里会提前多一个倒序。
         */
        public static Set<String> zReverseRangeByScore(String key, double minScore, double maxScore, long offset, long count) {
            log.info("zReverseRangeByScore(...) => key -> {}, minScore -> {}, maxScore -> {}, offset -> {}, "
                    + "count -> {}", key, minScore, maxScore, offset, count);
            Set<String> items = redisTemplate.opsForZSet().reverseRangeByScore(key, minScore, maxScore, offset, count);
            log.info("items -> {}", items);
            return items;
        }

        /**
         * 统计(key对应的zset中)score处于[minScore, maxScore]中的item的个数
         *
         * @param key      定位zset的key
         * @param minScore score下限
         * @param maxScore score上限
         * @return [minScore, maxScore]中item的个数
         * @since 2020/3/13 12:20:43
         */
        public static long zCount(String key, double minScore, double maxScore) {
            log.info("zCount(...) => key -> {}, minScore -> {}, maxScore -> {}", key, minScore, maxScore);
            Long count = redisTemplate.opsForZSet().count(key, minScore, maxScore);
            log.info("zCount(...) => count -> {}", count);
            if (count == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return count;
        }

        /**
         * 统计(key对应的)zset中item的个数
         * <p>
         * 注: 此方法等价于{@link RedisHelper.ZSetOps#zZCard(String)}
         *
         * @param key 定位zset的key
         * @return zset中item的个数
         * @since 2020/3/13 12:20:43
         */
        public static long zSize(String key) {
            log.info("zSize(...) => key -> {}", key);
            Long size = redisTemplate.opsForZSet().size(key);
            log.info("zSize(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 统计(key对应的)zset中item的个数
         * <p>
         * 注: 此方法等价于{@link RedisHelper.ZSetOps#zSize(String)}
         *
         * @param key 定位zset的key
         * @return zset中item的个数
         * @since 2020/3/13 12:20:43
         */
        public static long zZCard(String key) {
            log.info("zZCard(...) => key -> {}", key);
            Long size = redisTemplate.opsForZSet().zCard(key);
            log.info("zZCard(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 统计(key对应的)zset中指定item的score
         *
         * @param key  定位zset的key
         * @param item zset中的item
         * @return item的score
         * @since 2020/3/13 14:51:43
         */
        public static double zScore(String key, Object item) {
            log.info("zScore(...) => key -> {}, item -> {}", key, item);
            Double score = redisTemplate.opsForZSet().score(key, item);
            log.info("zScore(...) => score -> {}", score);
            if (score == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return score;
        }

        /**
         * 获取两个(key对应的)ZSet的并集, 并将结果add到storeKey对应的ZSet中。
         * <p>
         * 注: 和set一样，zset中item是唯一的， 在多个zset进行Union时, 处理相同的item时， score的值会变为对应的score之和，如：
         * RedisHelper.ZSetOps.zAdd("name1", "a", 1);和RedisUtil.ZSetOps.zAdd("name2", "a", 2);
         * 对(name1和name2对应的)zset进行zUnionAndStore之后，新的zset中的项a,对应的score值为3
         * <p>
         * case1: 交集不为空, storeKey不存在， 则 会创建对应的storeKey，并将并集添加到(storeKey对应的)ZSet中
         * case2: 交集不为空, storeKey已存在， 则 会清除原(storeKey对应的)ZSet中所有的项，然后将并集添加到(storeKey对应的)ZSet中
         * case3: 交集为空, 则不进行下面的操作, 直接返回0
         *
         * @param key      定位其中一个zset的键
         * @param otherKey 定位另外的zset的键
         * @param storeKey 定位(要把交集添加到哪个)set的key
         * @return add到(storeKey对应的)ZSet后, 该ZSet对应的size
         * @since 2020/3/11 12:26:24
         */
        public static long zUnionAndStore(String key, String otherKey, String storeKey) {
            log.info("zUnionAndStore(...) => key -> {}, otherKey -> {}, storeKey -> {}", key, otherKey, storeKey);
            Long size = redisTemplate.opsForZSet().unionAndStore(key, otherKey, storeKey);
            log.info("zUnionAndStore(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 获取两个(key对应的)ZSet的并集, 并将结果add到storeKey对应的ZSet中。
         * <p>
         * 注: 和set一样，zset中item是唯一的， 在多个zset进行Union时, 处理相同的item时， score的值会变为对应的score之和，如：
         * RedisHelper.ZSetOps.zAdd("name1", "a", 1);和RedisUtil.ZSetOps.zAdd("name2", "a", 2);
         * 对(name1和name2对应的)zset进行zUnionAndStore之后，新的zset中的项a,对应的score值为3
         * <p>
         * case1: 并集不为空, storeKey不存在， 则 会创建对应的storeKey，并将并集添加到(storeKey对应的)ZSet中
         * case2: 并集不为空, storeKey已存在， 则 会清除原(storeKey对应的)ZSet中所有的项，然后将并集添加到(storeKey对应的)ZSet中
         * case3: 并集为空, 则不进行下面的操作, 直接返回0
         *
         * @param key       定位其中一个set的键
         * @param otherKeys 定位其它set的键集
         * @param storeKey  定位(要把并集添加到哪个)set的key
         * @return add到(storeKey对应的)ZSet后, 该ZSet对应的size
         * @since 2020/3/11 12:26:24
         */
        public static long zUnionAndStore(String key, Collection<String> otherKeys, String storeKey) {
            log.info("zUnionAndStore(...) => key -> {}, otherKeys -> {}, storeKey -> {}", key, otherKeys, storeKey);
            Long size = redisTemplate.opsForZSet().unionAndStore(key, otherKeys, storeKey);
            log.info("zUnionAndStore(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 获取两个(key对应的)ZSet的交集, 并将结果add到storeKey对应的ZSet中。
         * <p>
         * 注: 和set一样，zset中item是唯一的， 在多个zset进行Intersect时, 处理相同的item时， score的值会变为对应的score之和，如：
         * RedisHelper.ZSetOps.zAdd("name1", "a", 1);
         * RedisHelper.ZSetOps.zAdd("name1", "b", 100);
         * 和R
         * edisUtil.ZSetOps.zAdd("name2", "a", 2);
         * edisUtil.ZSetOps.zAdd("name2", "c", 200);
         * 对(name1和name2对应的)zset进行zIntersectAndStore之后，新的zset中的项a,对应的score值为3
         * <p>
         * case1: 交集不为空, storeKey不存在， 则 会创建对应的storeKey，并将交集添加到(storeKey对应的)ZSet中
         * case2: 交集不为空, storeKey已存在， 则 会清除原(storeKey对应的)ZSet中所有的项，然后将交集添加到(storeKey对应的)ZSet中
         * case3: 交集为空, 则不进行下面的操作, 直接返回0
         *
         * @param key      定位其中一个ZSet的键
         * @param otherKey 定位其中另一个ZSet的键
         * @param storeKey 定位(要把交集添加到哪个)ZSet的key
         * @return add到(storeKey对应的)ZSet后, 该ZSet对应的size
         * @since 2020/3/11 9:46:46
         */
        public static long zIntersectAndStore(String key, String otherKey, String storeKey) {
            log.info("zIntersectAndStore(...) => key -> {}, otherKey -> {}, storeKey -> {}", key, otherKey, storeKey);
            Long size = redisTemplate.opsForZSet().intersectAndStore(key, otherKey, storeKey);
            log.info("zIntersectAndStore(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }

        /**
         * 获取多个(key对应的)ZSet的交集, 并将结果add到storeKey对应的ZSet中。
         * <p>
         * case1: 交集不为空, storeKey不存在， 则 会创建对应的storeKey，并将交集添加到(storeKey对应的)ZSet中
         * case2: 交集不为空, storeKey已存在， 则 会清除原(storeKey对应的)ZSet中所有的项，然后将交集添加到(storeKey对应的)ZSet中
         * case3: 交集为空, 则不进行下面的操作, 直接返回0
         *
         * @param key       定位其中一个set的键
         * @param otherKeys 定位其它set的键集
         * @param storeKey  定位(要把并集添加到哪个)set的key
         * @return add到(storeKey对应的)ZSet后, 该ZSet对应的size
         * @since 2020/3/11 11:04:29
         */
        public static long zIntersectAndStore(String key, Collection<String> otherKeys, String storeKey) {
            log.info("zIntersectAndStore(...) => key -> {}, otherKeys -> {}, storeKey -> {}",
                    key, otherKeys, storeKey);
            Long size = redisTemplate.opsForZSet().intersectAndStore(key, otherKeys, storeKey);
            log.info("zIntersectAndStore(...) => size -> {}", size);
            if (size == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return size;
        }
    }

    /**
     * redis分布式锁(单机版).
     * <p>
     * 使用方式(示例):
     * boolean flag = false;
     * String lockName = "sichuan:mianyang:fucheng:ds";
     * String lockValue = UUID.randomUUID().toString();
     * try {
     * //	非阻塞获取(锁的最大存活时间采用默认值)
     * flag = RedisHelper.LockOps.getLock(lockName, lockValue);
     * //	非阻塞获取e.g.
     * flag = RedisHelper.LockOps.getLock(lockName, lockValue, 3, TimeUnit.SECONDS);
     * // 阻塞获取(锁的最大存活时间采用默认值)
     * flag = RedisHelper.LockOps.getLockUntilTimeout(lockName, lockValue, 2000);
     * // 阻塞获取e.g.
     * flag = RedisHelper.LockOps.getLockUntilTimeout(lockName, lockValue, 2, TimeUnit.SECONDS, 2000);
     * if (!flag) {
     * throw new RuntimeException(" obtain redis-lock[" + lockName + "] fail");
     * }
     * // your logic
     * //	...
     * } finally {
     * if (flag) {
     * RedisHelper.LockOps.releaseLock(lockName, lockValue);
     * }
     * }
     * <p>
     * |--------------------------------------------------------------------------------------------------------------------|
     * |单机版分布式锁、集群版分布式锁，特别说明:                                                                                 |
     * |   - 此锁是针对单机Redis的分布式锁;                                                                                    |
     * |   - 对于Redis集群而言, 此锁可能存在失效的情况。考虑如下情况:                                                              |
     * |         首先，当客户端A通过key-value(假设为key名为key123)在Master上获取到一个锁。                                        |
     * |         然后，Master试着把这个数据同步到Slave的时候突然挂了(此时Slave上没有该分布式锁的key123)。                            |
     * |         接着，Slave变成了Master。                                                                                    |
     * |         不巧的是，客户端B此时也一以相同的key去获取分布式锁；                                                              |
     * |                 因为现在的Master上没有key123代表的分布式锁，                                                            |
     * |                 所以客户端B此时再通过key123去获取分布式锁时，                                                            |
     * |                 就能获取成功。                                                                                       |
     * |         那么此时，客户端A和客户端B同时获取到了同一把分布式锁，分布式锁失效。                                                 |
     * |   - 在Redis集群模式下，如果需要严格的分布式锁的话，可使用Redlock算法来实现。Redlock算法原理简述:                              |
     * |     - 获取分布式锁：                                                                                                 |
     * |           1. 客户端获取服务器当前的的时间t0。                                                                           |
     * |           2. 使用相同的key和value依次向5个实例获取锁。                                                                  |
     * |              注:为了避免在某个redis节点耗时太久而影响到对后面的Redis节点的锁的获取;                                         |
     * |                 客户端在获取每一个Redis节点的锁的时候,自身需要设置一个较小的等待获取锁超时的时间,                             |
     * |                 一旦都在某个节点获取分布式锁的时间超过了超时时间，那么就认为在这个节点获取分布式锁失败，                        |
     * |                 （不把时间浪费在这一个节点上），继续获取下一个节点的分布式锁。                                              |
     * |           3. 客户端通过当前时间(t1)减去t0，计算(从所有redis节点)获取锁所消耗的总时间t2(注：t2=t1-t0)。                      |
     * |              只有t2小于锁本身的锁定时长(注:若锁的锁定时长是1小时， 假设下午一点开始上锁，那么锁会在下午两点                     |
     * |              的时候失效， 而你却在两点后才获取到锁，这个时候已经没意义了)，并且，客户端在至少在多半Redis                        |
     * |              节点上获取到锁, 我们才认为分布式锁获取成功。                                                                |
     * |           5. 如果锁已经获取，那么  锁的实际有效时长 = 锁的总有效时长 - 获取分布式锁所消耗的时长; 锁的实际有效时长 应保证 > 0。    |
     * |              注: 也就是说， 如果获取锁失败，那么                                                                        |
     * |                  A. 可能是   获取到的锁的个数，不满足大多数原则。                                                         |
     * |                  B. 也可能是 锁的实际有效时长不大于0。                                                                  |
     * |      - 释放分布式锁： 在每个redis节点上试着删除锁(, 不论有没有在该节点上获取到锁)。                                          |
     * |   - 集群下的分布式锁，可直接使用现有类库<a href="https://github.com/redisson/redisson"/>                                |
     * |                                                                                                                    |
     * |   注: 如果Redis集群项目能够容忍master宕机导致单机版分布式锁失效的情况的话，那么是直接使用单机版分布式锁在Redis集群的项目中的；     |
     * |       如果Redis集群项目不能容忍单机版分布式锁失效的情况的话，那么请使用基于RedLock算法的集群版分布式锁；                        |
     * |--------------------------------------------------------------------------------------------------------------------|
     *
     * @author JustryDeng
     * @since 2020/3/14 19:23:26
     */
    public static class LockOps {

        /**
         * lua 脚本, 保证 释放锁脚本 的原子性（以避免, 并发场景下, 释放了别人的锁）
         */
        private static final String RELEASE_LOCK_LUA;

        /**
         * 分布式锁默认（最大）存活时长
         */
        public static final long DEFAULT_LOCK_TIMEOUT = 3;

        /**
         * DEFAULT_LOCK_TIMEOUT的单位
         */
        public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

        static {
            // 不论 lua 中 0 是否代表失败; 对于 java 的 Boolean 而言, 返回 0, 则会被解析为 false
            RELEASE_LOCK_LUA = "if redis.call('get',KEYS[1]) == ARGV[1] "
                    + "then "
                    + "    return redis.call('del',KEYS[1]) "
                    + "else "
                    + "    return 0 "
                    + "end ";
        }

        /**
         * 获取(分布式)锁.
         * <p>
         * 注: 获取结果是即时返回的、是非阻塞的。
         *
         * @see RedisHelper.LockOps#getLock(String, String, long, TimeUnit)
         */
        public static boolean getLock(final String key, final String value) {
            return getLock(key, value, DEFAULT_LOCK_TIMEOUT, DEFAULT_TIMEOUT_UNIT);
        }

        /**
         * 获取(分布式)锁。
         * 若成功, 则直接返回;
         * 若失败, 则进行重试, 直到成功 或 超时为止。
         * <p>
         * 注: 获取结果是阻塞的， 要么成功, 要么超时, 才返回。
         *
         * @param retryTimeoutLimit 重试的超时时长(ms)
         *                          其它参数可详见:
         * @return 是否成功
         * @see RedisHelper.LockOps#getLock(String, String, long, TimeUnit)
         */
        public static boolean getLockUntilTimeout(final String key, final String value,
                                                  final long retryTimeoutLimit) {
            return getLockUntilTimeout(key, value, DEFAULT_LOCK_TIMEOUT, DEFAULT_TIMEOUT_UNIT, retryTimeoutLimit);
        }

        /**
         * 获取(分布式)锁。
         * 若成功, 则直接返回;
         * 若失败, 则进行重试, 直到成功 或 超时为止。
         * <p>
         * 注: 获取结果是阻塞的， 要么成功, 要么超时, 才返回。
         *
         * @param retryTimeoutLimit 重试的超时时长(ms)
         *                          其它参数可详见:
         * @return 是否成功
         * @see RedisHelper.LockOps#getLock(String, String, long, TimeUnit, boolean)
         */
        public static boolean getLockUntilTimeout(final String key, final String value,
                                                  final long timeout, final TimeUnit unit,
                                                  final long retryTimeoutLimit) {
            log.info("getLockUntilTimeout(...) => key -> {}, value -> {}, timeout -> {}, unit -> {}, "
                    + "retryTimeoutLimit -> {}ms", key, value, timeout, unit, retryTimeoutLimit);
            long startTime = Instant.now().toEpochMilli();
            long now = startTime;
            do {
                try {
                    boolean alreadyGotLock = getLock(key, value, timeout, unit, false);
                    if (alreadyGotLock) {
                        log.info("getLockUntilTimeout(...) => consume time -> {}ms, result -> true", now - startTime);
                        return true;
                    }
                } catch (Exception e) {
                    log.warn("getLockUntilTimeout(...) => try to get lock failure! e.getMessage -> {}",
                            e.getMessage());
                }
                now = Instant.now().toEpochMilli();
            } while (now < startTime + retryTimeoutLimit);
            log.info("getLockUntilTimeout(...) => consume time -> {}ms, result -> false", now - startTime);
            return false;
        }

        /**
         * 获取(分布式)锁
         * <p>
         * 注: 获取结果是即时返回的、是非阻塞的。
         *
         * @see RedisHelper.LockOps#getLock(String, String, long, TimeUnit, boolean)
         */
        public static boolean getLock(final String key, final String value,
                                      final long timeout, final TimeUnit unit) {
            return getLock(key, value, timeout, unit, true);
        }

        /**
         * 获取(分布式)锁
         * <p>
         * 注: 获取结果是即时返回的、是非阻塞的。
         *
         * @param key       锁名
         * @param value     锁名对应的value
         *                  注: value一般采用全局唯一的值， 如: requestId、uuid等。
         *                  这样， 释放锁的时候, 可以再次验证value值,
         *                  保证自己上的锁只能被自己释放, 而不会被别人释放。
         *                  当然, 如果锁超时时, 会被redis自动删除释放。
         * @param timeout   锁的(最大)存活时长
         *                  注: 一般的， 获取锁与释放锁 都是成对使用的, 在锁在达到(最大)存活时长之前，都会被主动释放。
         *                  但是在某些情况下(如:程序获取锁后,释放锁前,崩了),锁得不到释放, 这时就需要等锁过
         *                  了(最大)存活时长后，被redis自动删除清理了。这样就能保证redis中不会留下死数据。
         * @param unit      timeout的单位
         * @param recordLog 是否记录日志
         * @return 是否成功
         */
        public static boolean getLock(final String key, final String value,
                                      final long timeout, final TimeUnit unit,
                                      boolean recordLog) {
            if (recordLog) {
                log.info("getLock(...) => key -> {}, value -> {}, timeout -> {}, unit -> {}",
                        key, value, timeout, unit);
            }
            Boolean result = redisTemplate.execute((RedisConnection connection) ->
                    connection.set(key.getBytes(StandardCharsets.UTF_8),
                            value.getBytes(StandardCharsets.UTF_8),
                            Expiration.seconds(unit.toSeconds(timeout)),
                            RedisStringCommands.SetOption.SET_IF_ABSENT)
            );
            if (recordLog) {
                log.info("getLock(...) => result -> {}", result);
            }
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

        /**
         * 释放(分布式)锁
         * <p>
         * 注: 此方式能(通过value的唯一性)保证: 自己加的锁, 只能被自己释放。
         * 注: 锁超时时, 也会被redis自动删除释放。
         *
         * @param key   锁名
         * @param value 锁名对应的value
         * @return 释放锁是否成功
         * @since 2020/3/15 17:00:45
         */
        public static boolean releaseLock(final String key, final String value) {
            log.info("releaseLock(...) => key -> {}, lockValue -> {}", key, value);
            Boolean result = redisTemplate.execute((RedisConnection connection) ->
                    connection.eval(RELEASE_LOCK_LUA.getBytes(),
                            ReturnType.BOOLEAN, 1,
                            key.getBytes(StandardCharsets.UTF_8),
                            value.getBytes(StandardCharsets.UTF_8))
            );
            log.info("releaseLock(...) => result -> {}", result);
            if (result == null) {
                BaseErrorEnum.INVALID_REDIS_RESULT_ERROR.throwException();
            }
            return result;
        }

    }

    /**
     * 提供一些基础功能支持
     *
     * @author JustryDeng
     * @since 2020/3/16 0:48:14
     */
    public static class Helper {

        /**
         * 默认拼接符
         */
        public static final String DEFAULT_SYMBOL = ":";

        /**
         * 拼接args
         *
         * @see RedisHelper.Helper#joinBySymbol(String, String...)
         */
        public static String join(String... args) {
            return RedisHelper.Helper.joinBySymbol(DEFAULT_SYMBOL, args);
        }

        /**
         * 使用symbol拼接args
         *
         * @param symbol 分隔符， 如: 【:】
         * @param args   要拼接的元素数组, 如: 【a b c】
         * @return 拼接后的字符串, 如  【a:b:c】
         * @since 2019/9/8 16:11
         */
        public static String joinBySymbol(String symbol, String... args) {
            if (symbol == null || symbol.trim().length() == 0) {
                throw new RuntimeException(" symbol must not be empty!");
            }
            if (args == null || args.length == 0) {
                throw new RuntimeException(" args must not be empty!");
            }
            StringBuilder sb = new StringBuilder(16);
            for (String arg : args) {
                sb.append(arg).append(symbol);
            }
            sb.replace(sb.length() - symbol.length(), sb.length(), "");
            return sb.toString();
        }

    }

}

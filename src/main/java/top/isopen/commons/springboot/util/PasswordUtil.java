package top.isopen.commons.springboot.util;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * 密码工具类
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/5 8:14
 */
public class PasswordUtil {

    private static RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
    private static String algorithmName = "md5";
    private static int hashIterations = 2;

    /**
     * 根据用户名和盐值加密
     *
     * @param password 密码
     * @param salt     盐
     */
    public static String encryptPassword(String password, String salt) {
        return new SimpleHash(algorithmName, password, ByteSource.Util.bytes(salt), hashIterations).toHex();
    }

    public static String generateSalt() {
        return randomNumberGenerator.nextBytes().toHex();
    }

    public static void setAlgorithmName(String algorithmName) {
        PasswordUtil.algorithmName = algorithmName;
    }

    public static void setHashIterations(int hashIterations) {
        PasswordUtil.hashIterations = hashIterations;
    }

    public static void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
        PasswordUtil.randomNumberGenerator = randomNumberGenerator;
    }

    public static boolean validatePassword(String encryptedPassword, String salt, String password) {
        return encryptedPassword.equals(encryptPassword(password, salt));
    }

}

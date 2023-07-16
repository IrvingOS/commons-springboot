package top.isopen.commons.springboot.util;

import top.isopen.commons.logging.Log;
import top.isopen.commons.logging.LogFactory;
import top.isopen.commons.springboot.repository.support.SFunction;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据库字段工具类
 * <p>
 * 将 Lambda 类型属性或 方法名 属性转换为数据库字段
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/6/15 17:35
 */
public class FieldUtil {

    private static final Log log = LogFactory.getLog(FieldUtil.class);

    private static final String WRITE_REPLACE = "writeReplace";
    private static final String[] PREFIXES = new String[]{"is", "get"};

    public static <T> String resolveName(SFunction<T, ?> func) {
        try {
            Method method = func.getClass().getDeclaredMethod(WRITE_REPLACE);
            method.setAccessible(true);

            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(func);
            String methodName = serializedLambda.getImplMethodName();

            return resolveName(methodName);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("error come where resolving entity name of SFunction: {}", e);
        }
        return null;
    }

    public static String resolveName(String methodName) {
        if (methodName == null || methodName.length() == 0) {
            return null;
        }
        for (String prefix : PREFIXES) {
            if (methodName.startsWith(prefix)) {
                String name = methodName.substring(prefix.length());
                return NameUtil.humpToUnderline(name.substring(0, 1).toLowerCase() + name.substring(1));
            }
        }
        return null;
    }

    public static List<Field> resolveDeclaredField(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> result = new ArrayList<>(Arrays.asList(fields));

        Class<?> superclass = clazz.getSuperclass();
        if (!Object.class.equals(superclass)) {
            result.addAll(resolveDeclaredField(superclass));
        }

        return result;
    }

}

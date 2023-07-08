package top.isopen.commons.springboot.repository.enums;

import top.isopen.commons.springboot.enums.BaseErrorEnum;

/**
 * 排序类型枚举
 *
 * @author TimeChaser
 * @since 9/7/2023 下午1:03
 */
public enum OrderByTypeEnum {

    ASC("asc", "正序"), DESC("desc", "逆序"),
    ;

    private static final OrderByTypeEnum[] VALUES;

    static {
        VALUES = values();
    }

    private final String value;
    private final String description;

    OrderByTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static OrderByTypeEnum resolve(String value) {
        String valueLowerCase = value.toLowerCase();
        for (OrderByTypeEnum orderByTypeEnum : VALUES) {
            if (orderByTypeEnum.value.equals(valueLowerCase)) {
                return orderByTypeEnum;
            }
        }
        BaseErrorEnum.INVALID_ORDER_BY_TYPE_ERROR.throwException();
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}

package top.isopen.commons.springboot.repository.enums;

import top.isopen.commons.springboot.enums.BaseErrorEnum;

/**
 * 查询类型枚举
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 16:56
 */
public enum QueryTypeEnum {

    EQ("eq", "等于"),
    LIKE("like", "像"),
    OR("or", "或者"),
    NE("ne", "不等于"),
    LE("le", "小于等于"),
    GE("ge", "大于等于"),
    LT("lt", "小于"),
    GT("gt", "大于"),
    ;

    private static final QueryTypeEnum[] VALUES;

    static {
        VALUES = values();
    }

    private final String value;
    private final String description;

    QueryTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static QueryTypeEnum resolve(String value) {
        String valueLowerCase = value.toLowerCase();
        for (QueryTypeEnum queryTypeEnum : VALUES) {
            if (queryTypeEnum.value.equals(valueLowerCase)) {
                return queryTypeEnum;
            }
        }
        BaseErrorEnum.INVALID_QUERY_TYPE_ERROR.throwException();
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}

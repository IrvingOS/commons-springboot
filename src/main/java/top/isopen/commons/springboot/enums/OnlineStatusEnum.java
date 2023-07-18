package top.isopen.commons.springboot.enums;

/**
 * 上线下线状态枚举
 *
 * @author TimeChaser
 * @version 0.1.0
 * @since 2023/6/14 15:48
 */
public enum OnlineStatusEnum {

    ONLINE("online", "上架"), OFFLINE("offline", "下架");

    private static final OnlineStatusEnum[] VALUES;

    static {
        VALUES = values();
    }

    private final String value;
    private final String description;

    OnlineStatusEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 功能描述: 根据value获取枚举类
     *
     * @param value value
     * @return Object
     **/
    public static OnlineStatusEnum resolve(String value) {
        String valueLowerCase = value.toLowerCase();
        for (OnlineStatusEnum onlineStatusEnum : VALUES) {
            if (onlineStatusEnum.value.equals(valueLowerCase)) {
                return onlineStatusEnum;
            }
        }
        BaseErrorEnum.INVALID_ONLINE_TYPE_ERROR.throwException();
        return OFFLINE;
    }

    public static String resolve(OnlineStatusEnum status) {
        return status != null ? status.getValue() : null;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}

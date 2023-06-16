package top.isopen.commons.springboot.enums;

/**
 * 上线下线状态枚举
 *
 * @author TimeChaser
 * @version 0.1.0
 * @since 2023/6/14 15:48
 */
public enum OnlineStatusEnum {

    ONLINE("online"), OFFLINE("offline");

    private static final OnlineStatusEnum[] VALUES;

    static {
        VALUES = values();
    }

    private final String value;

    OnlineStatusEnum(String value) {
        this.value = value;
    }

    /**
     * 功能描述: 根据value获取枚举类
     *
     * @param value value
     * @return Object
     **/
    public static OnlineStatusEnum resolve(String value) {
        for (OnlineStatusEnum onlineStatusEnum : VALUES) {
            if (onlineStatusEnum.value.equals(value)) {
                return onlineStatusEnum;
            }
        }
        return OFFLINE;
    }

    public String getValue() {
        return value;
    }

}

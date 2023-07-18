package top.isopen.commons.springboot.types;

import top.isopen.commons.springboot.enums.BaseErrorEnum;

/**
 * 主键类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:47
 */
public class PrimaryId extends ValueType<Long> {

    public PrimaryId(Long value) {
        super(value);
        if (value == null) {
            BaseErrorEnum.INVALID_PRIMARY_ID_ERROR.throwException();
        }
    }

}

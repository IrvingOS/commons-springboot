package top.isopen.commons.springboot.types;

import lombok.Value;
import top.isopen.commons.springboot.enums.BaseErrorEnum;

/**
 * 主键类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:47
 */
@Value
public class PrimaryId {

    Long value;

    public PrimaryId(Long value) {
        if (value == null) {
            BaseErrorEnum.INVALID_PRIMARY_ID_ERROR.throwException();
        }
        this.value = value;
    }

}

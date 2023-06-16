package top.isopen.commons.springboot.types;

import lombok.Value;
import top.isopen.commons.springboot.enums.BaseErrorEnum;

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

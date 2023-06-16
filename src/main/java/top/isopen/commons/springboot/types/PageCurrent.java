package top.isopen.commons.springboot.types;

import lombok.Value;
import top.isopen.commons.springboot.enums.BaseErrorEnum;

@Value
public class PageCurrent {

    Integer value;

    public PageCurrent(Integer value) {
        if (value == null || value < 1) {
            BaseErrorEnum.INVALID_PAGE_CURRENT_ERROR.throwException();
        }
        this.value = value;
    }

}

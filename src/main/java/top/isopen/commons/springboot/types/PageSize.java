package top.isopen.commons.springboot.types;

import lombok.Value;
import top.isopen.commons.springboot.enums.BaseErrorEnum;

@Value
public class PageSize {

    Integer value;

    public PageSize(Integer value) {
        if (value == null || value < 1 || value > 100) {
            BaseErrorEnum.INVALID_PAGE_SIZE_ERROR.throwException();
        }
        this.value = value;
    }

}

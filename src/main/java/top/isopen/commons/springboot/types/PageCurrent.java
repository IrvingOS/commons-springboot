package top.isopen.commons.springboot.types;

import lombok.Value;
import top.isopen.commons.springboot.enums.BaseErrorEnum;

/**
 * 当前页面类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:46
 */
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

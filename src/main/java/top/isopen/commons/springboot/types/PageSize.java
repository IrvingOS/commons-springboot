package top.isopen.commons.springboot.types;

import lombok.Value;
import top.isopen.commons.springboot.enums.BaseErrorEnum;

/**
 * 页面大小类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:47
 */
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

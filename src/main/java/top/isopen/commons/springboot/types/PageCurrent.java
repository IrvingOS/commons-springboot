package top.isopen.commons.springboot.types;

import top.isopen.commons.springboot.enums.BaseErrorEnum;

/**
 * 当前页面类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:46
 */
public class PageCurrent extends ValueType<Integer> {

    public PageCurrent(Integer value) {
        super(value);
        if (value == null || value < 1) {
            BaseErrorEnum.INVALID_PAGE_CURRENT_ERROR.throwException();
        }
    }

}

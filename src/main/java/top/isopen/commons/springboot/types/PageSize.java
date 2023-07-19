package top.isopen.commons.springboot.types;

import top.isopen.commons.springboot.enums.BaseErrorEnum;

/**
 * 页面大小类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:47
 */
public class PageSize extends ValueType<Integer> {

    public PageSize(Integer value) {
        super(value);
        if (value == null || value < 0 || value > 100) {
            BaseErrorEnum.INVALID_PAGE_SIZE_ERROR.throwException();
        }
    }

    public PageSize(ValueType<Integer> type) {
        this(type.getValue());
    }

}

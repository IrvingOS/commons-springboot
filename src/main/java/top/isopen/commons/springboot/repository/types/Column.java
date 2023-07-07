package top.isopen.commons.springboot.repository.types;

import lombok.Value;
import top.isopen.commons.springboot.enums.BaseErrorEnum;
import top.isopen.commons.springboot.util.NameUtil;

/**
 * 查询中使用的列类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 16:33
 */
@Value
public class Column {

    String value;

    /**
     * @param value 不能为空且长度不能为 0
     * @author TimeChaser
     * @since 2023/7/7 16:34
     */
    public Column(String value) {
        if (value == null || value.length() == 0) {
            BaseErrorEnum.INVALID_ORDER_BY_COLUMN_ERROR.throwException();
        }
        this.value = NameUtil.humpToUnderline(value);
    }

}
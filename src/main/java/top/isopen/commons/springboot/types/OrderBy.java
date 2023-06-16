package top.isopen.commons.springboot.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import top.isopen.commons.springboot.enums.BaseErrorEnum;
import top.isopen.commons.springboot.support.SFunction;
import top.isopen.commons.springboot.util.FieldUtil;
import top.isopen.commons.springboot.util.NameUtil;

import java.io.Serializable;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class OrderBy implements Serializable {

    private static final long serialVersionUID = 2006033976690108451L;

    private Column column;
    private boolean asc;

    @Value
    public static class Column {

        String value;

        public Column(String value) {
            if (value == null || value.length() == 0) {
                BaseErrorEnum.INVALID_ORDER_BY_COLUMN_ERROR.throwException();
            }
            this.value = NameUtil.humpToUnderline(value);
        }

    }

    public static OrderBy asc(String column) {
        return OrderBy.builder().column(new Column(column)).asc(true).build();
    }

    public static <T> OrderBy asc(SFunction<T, ?> func) {
        return OrderBy.builder().column(new Column(FieldUtil.resolveName(func))).asc(true).build();
    }

    public static OrderBy desc(String column) {
        return OrderBy.builder().column(new Column(column)).asc(false).build();
    }

    public static <T> OrderBy desc(SFunction<T, ?> func) {
        return OrderBy.builder().column(new Column(FieldUtil.resolveName(func))).asc(false).build();
    }

}

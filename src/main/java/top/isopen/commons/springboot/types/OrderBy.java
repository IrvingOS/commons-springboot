package top.isopen.commons.springboot.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import top.isopen.commons.springboot.bean.OrderByRequest;
import top.isopen.commons.springboot.enums.BaseErrorEnum;
import top.isopen.commons.springboot.model.AbstractModel;
import top.isopen.commons.springboot.support.SFunction;
import top.isopen.commons.springboot.util.FieldUtil;
import top.isopen.commons.springboot.util.NameUtil;

import java.io.Serializable;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class OrderBy<T extends AbstractModel<T, ?>> implements Serializable {

    private static final long serialVersionUID = 2006033976690108451L;

    private Column column;
    private boolean asc;

    public OrderBy(String column, boolean asc) {
        this.column = new Column(column);
        this.asc = asc;
    }

    public static <T extends AbstractModel<T, ?>> OrderBy<T> resolve(OrderByRequest orderByRequest) {
        return OrderBy.<T>builder().column(new Column(orderByRequest.getColumn())).asc(orderByRequest.isAsc()).build();
    }

    public OrderBy<T> asc(String column) {
        this.column = new Column(column);
        this.asc = true;
        return this;
    }

    public OrderBy<T> asc(SFunction<T, ?> func) {
        this.column = new Column(FieldUtil.resolveName(func));
        this.asc = true;
        return this;
    }

    public OrderBy<T> desc(String column) {
        this.column = new Column(column);
        this.asc = false;
        return this;
    }

    public OrderBy<T> desc(SFunction<T, ?> func) {
        this.column = new Column(FieldUtil.resolveName(func));
        this.asc = false;
        return this;
    }

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

}

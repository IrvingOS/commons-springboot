package top.isopen.commons.springboot.types;

import lombok.Builder;
import lombok.Value;
import top.isopen.commons.springboot.enums.QueryTypeEnum;
import top.isopen.commons.springboot.support.SFunction;

@Value
@Builder
public class Query<T> {

    QueryTypeEnum type;
    SFunction<T, ?> fieldFunc;
    Object value;

}

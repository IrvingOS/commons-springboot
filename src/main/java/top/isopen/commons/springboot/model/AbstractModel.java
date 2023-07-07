package top.isopen.commons.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import top.isopen.commons.springboot.types.BaseType;

/**
 * 抽象 Model 层
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 16:33
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public abstract class AbstractModel<T extends BaseModel, R extends BaseType> extends BaseModel {

    private static final long serialVersionUID = 219154690246688196L;

    public abstract R toType();

}

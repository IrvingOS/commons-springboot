package top.isopen.commons.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import top.isopen.commons.springboot.repository.annotation.QueryField;
import top.isopen.commons.springboot.repository.enums.QueryTypeEnum;
import top.isopen.commons.springboot.types.BaseType;

/**
 * 是否在线的 Model 层
 * <p>
 * 对应 {@link top.isopen.commons.springboot.types.OnlineType}
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 16:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public abstract class OnlineModel<T extends BaseModel, R extends BaseType> extends AbstractModel<T, R> {

    @QueryField(type = QueryTypeEnum.EQ)
    private String status;

}

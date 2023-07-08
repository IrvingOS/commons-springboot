package top.isopen.commons.springboot.types;

import lombok.experimental.SuperBuilder;
import top.isopen.commons.springboot.model.BaseModel;

import java.util.Date;

/**
 * 抽象类型
 *
 * @author TimeChaser
 * @version 1.0
 * @since 2023/7/7 17:46
 */
@SuperBuilder(toBuilder = true)
public abstract class AbstractType<T extends BaseType, R extends BaseModel> extends BaseType {

    public void fillCreateTime() {
        this.setCreateTime(new Date());
    }

    public void fillUpdateTime() {
        this.setUpdateTime(new Date());
    }

    /**
     * TODO 考虑对 toModel 方法的简化
     */
    public abstract R toModel();

    public abstract void updateAll(T current);

    public abstract void updatePart(T current);

}

package top.isopen.commons.springboot.types;

import lombok.experimental.SuperBuilder;
import top.isopen.commons.springboot.model.BaseModel;

import java.util.Date;

@SuperBuilder(toBuilder = true)
public abstract class AbstractType<T extends BaseType, R extends BaseModel> extends BaseType {

    public void fillCreateTime() {
        this.setCreateTime(new Date());
    }

    public void fillUpdateTime() {
        this.setUpdateTime(new Date());
    }

    public abstract R toPO();

    public abstract void updateAll(T current);

    public abstract void updatePart(T current);

}

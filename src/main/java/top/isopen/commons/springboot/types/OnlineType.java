package top.isopen.commons.springboot.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import top.isopen.commons.springboot.enums.OnlineStatusEnum;
import top.isopen.commons.springboot.model.BaseModel;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public abstract class OnlineType<T extends BaseType, R extends BaseModel> extends AbstractType<T, R> {

    private OnlineStatusEnum status;

    public boolean isOnline() {
        return this.status != null && this.status.equals(OnlineStatusEnum.ONLINE);
    }

    public boolean isOffline() {
        return this.status != null && this.status.equals(OnlineStatusEnum.OFFLINE);
    }

    @Override
    public void updateAll(T current) {
        this.setStatus(((OnlineType<?, ?>) current).getStatus());
    }

    @Override
    public void updatePart(T current) {
        if (((OnlineType<?, ?>) current).getStatus() != null) {
            this.setStatus(((OnlineType<?, ?>) current).getStatus());
        }
    }

}

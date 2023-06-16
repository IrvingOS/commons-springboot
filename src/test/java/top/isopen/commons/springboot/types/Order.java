package top.isopen.commons.springboot.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import top.isopen.commons.springboot.model.OrderModel;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class Order extends AbstractType<Order, OrderModel> {

    private OrderId orderId;
    private String transactionId;
    private String description;
    private String detail;
    private Long userId;
    private Long subscribeId;
    private String payMode;
    private Integer amount;
    private String status;

    @Override
    public OrderModel toPO() {
        return OrderModel.builder()
                .orderId(this.getOrderId() != null ? this.getOrderId().getValue() : null)
                .transactionId(this.getTransactionId())
                .description(this.getDescription())
                .detail(this.getDetail())
                .userId(this.getUserId())
                .subscribeId(this.getSubscribeId())
                .payMode(this.getPayMode())
                .amount(this.getAmount())
                .status(this.getStatus())
                .createTime(this.getCreateTime())
                .updateTime(this.getUpdateTime())
                .build();
    }

    @Override
    public void updateAll(Order current) {
    }

    @Override
    public void updatePart(Order current) {
    }

}

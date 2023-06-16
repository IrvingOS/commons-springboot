package top.isopen.commons.springboot.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import top.isopen.commons.springboot.types.Order;
import top.isopen.commons.springboot.types.OrderId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@TableName(value = "`order`")
public class OrderModel extends AbstractModel<OrderModel, Order> {

    private static final long serialVersionUID = 7291836881073544249L;

    @TableId
    private Long orderId;
    private Long userId;
    private Long subscribeId;
    private String transactionId;
    private String description;
    private String detail;
    private String payMode;
    private Integer amount;
    private String status;

    @Override
    public Order toEntity() {
        return Order.builder()
                .orderId(new OrderId(this.getOrderId()))
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

}

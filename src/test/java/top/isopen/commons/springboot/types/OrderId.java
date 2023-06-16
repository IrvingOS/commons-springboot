package top.isopen.commons.springboot.types;

import lombok.Value;

@Value
public class OrderId {

    Long value;

    public OrderId(Long value) {
        this.value = value;
    }

}

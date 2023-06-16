package top.isopen.commons.springboot.repository;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import top.isopen.commons.logging.Log;
import top.isopen.commons.logging.LogFactory;
import top.isopen.commons.springboot.types.OrderByList;
import top.isopen.commons.springboot.model.OrderModel;
import top.isopen.commons.springboot.types.Order;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@SpringBootApplication
@MapperScan("top.isopen.commons.springboot.dao")
public class OrderRepositoryTest {

    private static final Log log = LogFactory.getLog(OrderRepositoryTest.class);

    @Resource
    private OrderRepository orderRepository;

    @Test
    public void testListOrder() {
        List<Order> orders = orderRepository.listOrder(
                Order.builder().subscribeId(2L).build(),
                new OrderByList().desc("createTime"));
        for (Order order : orders) {
            log.info(order.toString());
        }
    }

}
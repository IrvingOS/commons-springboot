package top.isopen.commons.springboot.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import top.isopen.commons.logging.Log;
import top.isopen.commons.logging.LogFactory;
import top.isopen.commons.springboot.bean.OrderByRequest;
import top.isopen.commons.springboot.helper.RedisHelper;
import top.isopen.commons.springboot.test.model.OrderModel;
import top.isopen.commons.springboot.test.repository.OrderRepository;
import top.isopen.commons.springboot.test.service.LockService;
import top.isopen.commons.springboot.test.types.Order;
import top.isopen.commons.springboot.test.types.User;
import top.isopen.commons.springboot.types.OrderByList;

import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication
@RestController
@MapperScan("top.isopen.commons.springboot.test.dao")
public class TestApplication {

    public static final Log log = LogFactory.getLog(TestApplication.class);

    @Resource
    private LockService lockService;
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private RedisHelper.KeyOps redisKeyOps;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }


    @GetMapping("/{key}")
    public void tryLock(@PathVariable String key) {
        lockService.lock(key);
        lockService.lock(key);
    }

    @GetMapping("/{key1}/{key2}")
    public void tryLock(@PathVariable String key1, @PathVariable String key2) {
        lockService.lock(key1, key2);
    }

    @PostMapping("/user")
    public void tryLock(@RequestBody User user) {
        lockService.lock(user);
    }

    @GetMapping("/orm")
    public List<Order> orm() {
        return orderRepository.listOrder(
                null,
                new OrderByList<OrderModel>().asc(OrderModel::getAmount).desc(OrderModel::getCreateTime));
    }

    @PostMapping("/orm")
    public List<Order> orm(@RequestBody List<OrderByRequest> orderByRequestList) {
        log.info("hasKey aaa: {}", redisKeyOps.hasKey("aaa"));
        return orderRepository.listOrder(
                null,
                OrderByList.resolve(orderByRequestList)
        );
    }

}

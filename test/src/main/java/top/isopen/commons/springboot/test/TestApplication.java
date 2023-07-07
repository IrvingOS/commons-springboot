package top.isopen.commons.springboot.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import top.isopen.commons.logging.Log;
import top.isopen.commons.logging.LogFactory;
import top.isopen.commons.springboot.bean.Result;
import top.isopen.commons.springboot.helper.RedisHelper;
import top.isopen.commons.springboot.repository.types.OrderByList;
import top.isopen.commons.springboot.repository.types.QueryList;
import top.isopen.commons.springboot.test.repository.OrderRepository;
import top.isopen.commons.springboot.test.service.LockService;
import top.isopen.commons.springboot.test.types.Order;
import top.isopen.commons.springboot.test.types.OrderId;
import top.isopen.commons.springboot.test.types.User;

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
    private RedisHelper redisHelper;

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
    public Result<User> tryLock(@RequestBody User user) {
        log.info(user.getUserId().length() + "");
        log.info(user.toString());
        lockService.lock(user);
        return Result.ok(user);
    }

    @GetMapping("/orm")
    public Result<List<Order>> orm() {
        return Result.ok(orderRepository.listOrder(
                Order.builder().subscribeId(3L).amount(1000).orderId(new OrderId(1L)).build(),
                OrderByList.<Order>builder().asc(Order::getAmount).desc(Order::getCreateTime).build()));
    }

    @PostMapping("/orm")
    public Result<List<Order>> orm(@RequestBody OrmRequest ormRequest) {
        log.info("hasKey aaa: {}", redisHelper.hasKey("aaa"));
        return Result.ok(orderRepository.listOrder(
                QueryList.resolve(ormRequest.getQuery()),
                OrderByList.resolve(ormRequest.getOrderBy())
        ));
    }

}

package top.isopen.commons.springboot.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@SpringBootApplication
@RestController
public class TestApplication {

    @Resource
    private ServiceComponent serviceComponent;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }


    @GetMapping("/{key}")
    public void tryLock(@PathVariable String key) {
        serviceComponent.lock(key);
        serviceComponent.lock(key);
    }

    @GetMapping("/{key1}/{key2}")
    public void tryLock(@PathVariable String key1, @PathVariable String key2) {
        serviceComponent.lock(key1, key2);
    }

    @PostMapping("/user")
    public void tryLock(@RequestBody User user) {
        serviceComponent.lock(user);
    }

}

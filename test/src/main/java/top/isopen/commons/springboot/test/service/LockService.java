package top.isopen.commons.springboot.test.service;

import org.springframework.stereotype.Service;
import top.isopen.commons.springboot.lock.annotation.RedLock;
import top.isopen.commons.springboot.lock.annotation.RedLocks;
import top.isopen.commons.springboot.test.types.User;

@Service
public class LockService {

    @RedLock(key = "key:#lock")
    public void lock(String lock) {
    }

    @RedLocks(keys = {"multi:#key1", "multi:#key2"})
    public void lock(String key1, String key2) {
    }

    @RedLocks(keys = {"user:#user.userId:passwordchange", "tenant:#user.tenantId:passwordchange"})
    public void lock(User user) {
    }


}

package com.xqxls;

import com.alibaba.fastjson2.JSON;
import com.xqxls.dao.IUserDao;
import com.xqxls.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author: xqxls
 * @CreateTime: 2024/2/6 15:20
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private IUserDao userDao;

    @Test
    public void test(){
//        List<User> userList = userDao.findAll();
//        log.info("测试结果：{}", JSON.toJSON(userList));

        User user = userDao.queryUserInfoById(4L);
        log.info("测试结果：{}", JSON.toJSON(user));
    }

    @Test
    public void test_insert(){
        User user = new User();
        user.setUserId("126");
        user.setUserName("小兵");
        userDao.insert(user);
        log.info("测试结果：{}", JSON.toJSON(user));
    }

    @Test
    public void test_update(){
        User user = new User();
        user.setId(4L);
        user.setUserId("125");
        user.setUserName("小Q");
        userDao.update(user);
        log.info("测试结果：{}", JSON.toJSON(user));
    }

    @Test
    public void test_delete(){
        userDao.delete(3L);
    }

}

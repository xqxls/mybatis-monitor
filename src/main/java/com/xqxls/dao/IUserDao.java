package com.xqxls.dao;

import com.xqxls.annotation.Watch;
import com.xqxls.enums.Operation;
import com.xqxls.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xqxls
 * @create 2023-08-03 17:11
 * @Description
 */
@Mapper
public interface IUserDao {

    @Watch(operation = Operation.SELECT,remark = "根据主键ID查询用户")
    User queryUserInfoById(@Param("id") Long id);

//    @Watch(operation = Operation.SELECT,remark = "查询所有用户")
    List<User> findAll();

    @Watch(operation = Operation.INSERT,remark = "新增用户")
    void insert(User user);

    @Watch(operation = Operation.UPDATE,remark = "更新用户")
    void update(User user);

    @Watch(operation = Operation.DELETE,remark = "根据主键ID删除用户")
    void delete(@Param("id") Long id);
}

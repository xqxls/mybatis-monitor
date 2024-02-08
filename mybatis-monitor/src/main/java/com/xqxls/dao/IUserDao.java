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

    User queryUserInfoById(@Param("id") Long id);

    List<User> findAll();

    int insert(User user);

//    @Watch(operation = Operation.UPDATE,remark = "更新用户")
    int update(User user);

    @Watch(operation = Operation.DELETE,remark = "根据主键ID删除用户")
    int delete(@Param("id") Long id);
}

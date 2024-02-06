package com.xqxls.dao;


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

    void insert(User user);

    void update(User user);

    void delete(@Param("id") Long id);
}

package com.xqxls.dao;

import com.xqxls.po.LogInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @Author: xqxls
 * @CreateTime: 2024/2/6 21:49
 */
@Mapper
public interface ILogInfoDao {

    void insert(LogInfo logInfo);

}

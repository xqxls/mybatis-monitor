package com.xqxls.event.listener;

import com.alibaba.fastjson2.JSON;
import com.google.common.eventbus.Subscribe;
import com.xqxls.dao.ILogInfoDao;
import com.xqxls.event.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @Description:
 * @Author: xqxls
 * @CreateTime: 2024/2/7 9:10
 */
@Slf4j
@Component
public class LogInfoListener {

    @Resource
    private ILogInfoDao logInfoDao;

    @Subscribe
    public void handle(Message message) {
        log.info("收到消息，message：{}", JSON.toJSON(message));
        log.info("logInfo is：{}", JSON.toJSON(message.getLogInfo()));
        logInfoDao.insert(message.getLogInfo());

    }



}

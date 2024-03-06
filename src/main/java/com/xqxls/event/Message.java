package com.xqxls.event;

import com.xqxls.po.LogInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description:
 * @Author: xqxls
 * @CreateTime: 2024/2/7 9:08
 */
@Data
@AllArgsConstructor
public class Message {

    /** 日志信息 **/
    LogInfo logInfo;
}

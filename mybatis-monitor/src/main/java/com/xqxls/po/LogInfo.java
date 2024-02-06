package com.xqxls.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: xqxls
 * @CreateTime: 2024/2/6 21:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogInfo {

    // 主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 方法名
    private String methodName;
    // 操作类型
    private String type;
    // 执行的sql
    private String executeSql;
    // 入参
    private String parameter;
    // 返回值
    private String returnValue;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;
    // 是否删除
    private Boolean isDeleted;
}

package com.xqxls.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User{

    // 主键ID
    private Long id;
    // 用户ID
    private String userId;
    // 用户名称
    private String userName;
    // 头像
    private String userHead;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;
    // 是否删除
    private Boolean isDeleted;


}

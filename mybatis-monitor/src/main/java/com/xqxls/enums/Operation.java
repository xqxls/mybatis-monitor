package com.xqxls.enums;


/**
 * @Description:
 * @Author: xqxls
 * @CreateTime: 2024/2/6 17:06
 */
public enum Operation {

    INSERT(1, "新增"),
    UPDATE(2, "编辑"),
    DELETE(3, "删除"),
    SELECT(4, "查询"),
    UNKNOWN(-1, "未知类型");

    private Integer code;
    private String desc;

    Operation(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode(){
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }




}

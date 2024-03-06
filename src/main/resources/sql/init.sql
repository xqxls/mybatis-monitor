
CREATE TABLE `log_info` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                            `namespace` varchar(128) DEFAULT NULL COMMENT '接口路径',
                            `method_name` varchar(64) DEFAULT NULL COMMENT '方法名',
                            `type` varchar(16) DEFAULT NULL COMMENT '操作类型',
                            `execute_sql` varchar(1024) DEFAULT NULL COMMENT '执行的sql',
                            `final_sql` varchar(1024) DEFAULT NULL COMMENT '最终sql',
                            `parameter` json DEFAULT NULL COMMENT '入参',
                            `return_value` json DEFAULT NULL COMMENT '返回值',
                            `spend_time` bigint(20) DEFAULT NULL COMMENT '方法耗时',
                            `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                            `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                            `is_deleted` tinyint(1) DEFAULT NULL COMMENT '是否删除',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                        `user_id` varchar(9) DEFAULT NULL COMMENT '用户ID',
                        `user_head` varchar(16) DEFAULT NULL COMMENT '用户头像',
                        `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                        `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                        `user_name` varchar(64) DEFAULT NULL,
                        `is_deleted` tinyint(1) DEFAULT NULL COMMENT '是否删除',
                        `isDeleted` tinyint(1) DEFAULT NULL COMMENT '是否删除',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4;
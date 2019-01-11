DROP TABLE IF EXISTS `xkr_message`;
CREATE TABLE xkr_message(
	`id` bigint(20) UNSIGNED NOT NULL COMMENT '消息id',
	`from_type_code` tinyint(4) NOT NULL COMMENT '消息来源id类型',
	`from_id` bigint(20) UNSIGNED NOT NULL COMMENT '来源消息id',
	`to_type_code` tinyint(4) NOT NULL COMMENT '消息目标id类型',
	`to_id` bigint(20) UNSIGNED NOT NULL COMMENT '目标消息id',
	`content` varchar(255) NOT NULL DEFAULT '' COMMENT '消息内容,长度限制80字内',
	`status` tinyint(4) NOT NULL COMMENT '消息状态',
	`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段',
	PRIMARY KEY (`id`),
	INDEX `idx_tid` (`to_type_code`,`to_id`,`status`,`update_time`),
	INDEX `idx_fid` (`from_type_code`,`from_id`,`status`,`update_time`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息表';

DROP TABLE IF EXISTS `xkr_resource`;
CREATE TABLE xkr_resource(
	`id` bigint(20) UNSIGNED NOT NULL COMMENT '资源id',
	`class_id` bigint(20) UNSIGNED NOT NULL COMMENT '栏目id',
	`user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
	`cost` int(10) UNSIGNED NOT NULL COMMENT '资源积分',
	`status` tinyint(4) NOT NULL COMMENT '资源状态:1为正常,2为未审核,3为冻结,4为删除',
	`report` tinyint(4) NOT NULL COMMENT '举报状态：0为正常,1为被举报',
	`file_size` varchar(64) NOT NULL COMMENT '文件大小',
	`title` varchar(255) NOT NULL COMMENT '文件标题,80字内',
	`detail` TEXT  NOT NULL COMMENT '资源详情，5000字内',
	`resource_url` varchar(255) NOT NULL COMMENT '资源uri,表示资源根目录(md5加密后文件)',
	`download_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '下载量',
	`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段，存储内容简要等,file_size等',
	PRIMARY KEY (`id`),
	INDEX `idx_cla_sta` (`class_id`,`status`,`update_time`),
	INDEX `idx_cla_sta_dc` (`class_id`,`status`,`download_count`),
	INDEX `idx_user_sta` (`user_id`,`status`,`update_time`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资源表';

DROP TABLE IF EXISTS `xkr_resource_recycle`;
CREATE TABLE xkr_resource_recycle(
	`resource_id` bigint(20) UNSIGNED NOT NULL COMMENT '资源id',
	`resource_title`varchar(255) NOT NULL COMMENT '文件标题,80字内',
	`class_name` varchar(64) NOT NULL COMMENT '分类名,长度限制20',
	`user_name` varchar(64) NOT NULL COMMENT '用户名,长度限制20字内',
	`opt_name` varchar(64) NOT NULL COMMENT '管理员名称',
	`create_time` DATETIME COMMENT '创建时间',
	`update_time` DATETIME COMMENT '更新时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段，存储download_count，resource_url等',
	PRIMARY KEY (`resource_id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资源回收站表';

DROP TABLE IF EXISTS `xkr_user`;
CREATE TABLE xkr_user(
	`id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
	`user_name` varchar(64) NOT NULL COMMENT '用户名,长度限制20字内',
	`user_token` varchar(255) NOT NULL COMMENT '用户token',
	`salt` varchar(255) NOT NULL COMMENT 'token的salt',
	`email` varchar(64) NOT NULL DEFAULT '' COMMENT '邮箱,长度限制64',
	`wealth` bigint(20) UNSIGNED NOT NULL COMMENT '财富值',
	`total_recharge` bigint(20) UNSIGNED NOT NULL COMMENT '充值总额',
	`status` tinyint(4) NOT NULL COMMENT '用户状态1为正常,2为未激活,3为冻结',
	`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
	`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段',
	PRIMARY KEY (`id`),
	INDEX `idx_st_up` (`status`,`update_time`),
	INDEX `idx_user_sta` (`user_name`,`status`),
	INDEX `idx_email_sta` (`email`,`status`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

DROP TABLE IF EXISTS `xkr_resource_user`;
CREATE TABLE xkr_resource_user(
	`id` bigint(20) UNSIGNED NOT NULL COMMENT 'id',
	`resource_id` bigint(20) UNSIGNED NOT NULL COMMENT '资源id',
	`user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
	`status` tinyint(4) NOT NULL COMMENT '用户资源状态,1已支付',
	`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段，存储download_count，resource_url等',
	PRIMARY KEY (`id`),
	INDEX `idx_res_sta` (`resource_id`,`status`,`update_time`),
	INDEX `idx_user_sta` (`user_id`,`status`,`update_time`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户资源支付表';


DROP TABLE IF EXISTS `xkr_login_token`;
CREATE TABLE xkr_login_token(
	`id` bigint(20) UNSIGNED NOT NULL COMMENT '登录tokenId',
	`user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
	`login_token` varchar(255) NOT NULL COMMENT '登录token',
	`client_ip` varchar(64) NOT NULL COMMENT '客户端ip',
	`status` tinyint(4) NOT NULL COMMENT 'token状态1正常',
	`login_count` int(10) UNSIGNED NOT NULL COMMENT '登录次数',
	`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `idx_user_sta` (`user_id`,`status`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='登录token表';


DROP TABLE IF EXISTS `xkr_class`;
CREATE TABLE xkr_class(
	`id` bigint(20) UNSIGNED NOT NULL COMMENT '分类id',
	`parent_class_id` bigint(20) UNSIGNED NOT NULL COMMENT '父分类id',
	`path` varchar(255) NOT NULL COMMENT '分类路径,包含该路径,以-分割',
	`class_name` varchar(64) NOT NULL COMMENT '分类名,长度限制20',
	`status` tinyint(4) NOT NULL COMMENT '分类状态',
	`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段',
	PRIMARY KEY (`id`),
	INDEX `idx_sta` (`status`,`update_time`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='栏目表';
insert into xkr_class values('0','0','0','root','1',current_timestamp(),current_timestamp(),'{}');
insert into xkr_class values('1','0','0-1','毕业设计','1',current_timestamp(),current_timestamp(),'{}');
insert into xkr_class values('2','0','0-2','资源','1',current_timestamp(),current_timestamp(),'{}');

DROP TABLE IF EXISTS `xkr_resource_comment`;
CREATE TABLE xkr_resource_comment(
	`id` bigint(20) UNSIGNED NOT NULL COMMENT '评论id',
	`resource_id` bigint(20) UNSIGNED NOT NULL COMMENT '资源id',
	`user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
	`parent_comment_id` bigint(20) UNSIGNED NOT NULL COMMENT '父评论id',
	`root_comment_id` bigint(20) UNSIGNED NOT NULL COMMENT '根评论id',
	`content` TEXT  NOT NULL COMMENT '评论内容，5000字内',
	`client_ip` varchar(64) NOT NULL COMMENT '客户端ip',
	`status` tinyint(4) NOT NULL COMMENT '评论状态，1正常,2未审核,3删除',
	`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段',
	PRIMARY KEY (`id`),
	INDEX `idx_user_sta` (`user_id`,`status`,`update_time`),
	INDEX `idx_res_sta` (`resource_id`,`status`,`update_time`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资源评论表';

DROP TABLE IF EXISTS `xkr_about_remark`;
CREATE TABLE xkr_about_remark(
	`id` bigint(20) UNSIGNED NOT NULL COMMENT '留言d',
	`user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
	`user_type_code` tinyint(4) NOT NULL COMMENT '账号类型,管理员或用户',
	`parent_remark_id` bigint(20) UNSIGNED NOT NULL COMMENT '父留言id',
	`content` TEXT  NOT NULL COMMENT '留言内容，5000字内',
	`status` tinyint(4) NOT NULL COMMENT '留言状态',
	`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段,包含qq号,手机号',
	PRIMARY KEY (`id`),
	INDEX `idx_sta_up` (`status`,`update_time`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='留言表';


DROP TABLE IF EXISTS `xkr_admin_opt_log`;
CREATE TABLE xkr_admin_opt_log(
	`id` bigint(20) UNSIGNED NOT NULL COMMENT '评论id',
	`admin_account_id` bigint(20) UNSIGNED NOT NULL COMMENT '管理员id',
	`opt_module` tinyint(4) NOT NULL COMMENT '操作模块',
	`opt_detail` varchar(255) NOT NULL DEFAULT '' COMMENT '操作内容,长度限制80字内',
	`client_ip` varchar(64) NOT NULL COMMENT '客户端ip',
	`status` tinyint(4) NOT NULL COMMENT '日志状态',
	`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段',
	PRIMARY KEY (`id`),
	INDEX `idx_sta_up_ac` (`status`,`update_time`,`admin_account_id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='操作日志表';

DROP TABLE IF EXISTS `xkr_admin_account`;
CREATE TABLE xkr_admin_account(
	`id` bigint(20) UNSIGNED NOT NULL COMMENT '管理员id',
	`account_name` varchar(64) NOT NULL COMMENT '管理员名称',
	`account_token` varchar(255) NOT NULL COMMENT '管理员token',
	`email` varchar(64) NOT NULL DEFAULT '' COMMENT '邮箱,长度限制64',
	`permission_ids` varchar(255) NOT NULL COMMENT '权限id,以;分割',
	`status` tinyint(4) NOT NULL COMMENT '账号状态',
	`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段',
	PRIMARY KEY (`id`),
	INDEX `idx_sta_up` (`status`,`update_time`),
	INDEX `idx_name_sta` (`account_name`,`status`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='管理员表';
insert into xkr_admin_account values('6476825075076045938','admin','c3284d0f94606de1fd2af172aba15bf3','administrator@sharecoder.cn','1;2;3;4;5;6;7;8;9;10','1',current_timestamp(),current_timestamp(),'{}');

DROP TABLE IF EXISTS `xkr_pay_order`;
CREATE TABLE xkr_pay_order(
	`id` bigint(20) UNSIGNED NOT NULL COMMENT '账单id',
	`user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
	`pay_type_code` tinyint(4) NOT NULL COMMENT '支付类型,微信,支付宝',
	`trade_type` tinyint(4) NOT NULL COMMENT '交易类型,扫码,app等',
	`pay_order_no` varchar(128) NOT NULL COMMENT '业务订单号',
	`pre_pay_id` varchar(128) NOT NULL DEFAULT '' COMMENT '预支付订单号',
	`pay_id` varchar(128) NOT NULL DEFAULT '' COMMENT '支付订单号',
	`client_ip` varchar(64) NOT NULL COMMENT '客户端ip',
	`pay_amount` bigint(20) UNSIGNED NOT NULL COMMENT '支付金额,分为单位',
	`status` tinyint(4) NOT NULL COMMENT '订单状态',
	`code_url` varchar(4) NOT NULL DEFAULT '' COMMENT '二维码url',
	`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`expire_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
	`pay_time` DATETIME DEFAULT NULL COMMENT '付款时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段,包含支付结果信息,买家账号信息,订单详情信息等',
	PRIMARY KEY (`id`),
	INDEX `idx_sta_up` (`status`,`pay_time`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账单信息表';

DROP TABLE IF EXISTS `xkr_database_backup`;
CREATE TABLE xkr_database_backup(
	`id` bigint(20) UNSIGNED NOT NULL COMMENT '数据库备份id',
	`backup_name` varchar(64) NOT NULL COMMENT '数据库备份名',
	`admin_account_id` bigint(20) UNSIGNED NOT NULL COMMENT '管理员id',
	`status` tinyint(4) NOT NULL COMMENT '状态',
	`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`ext` varchar(1024) NOT NULL DEFAULT '{}' COMMENT '扩展字段,包括本地备份命令的存储',
	PRIMARY KEY (`id`),
	INDEX `idx_sta_up` (`status`,`update_time`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据库备份表';
DROP TABLE IF EXISTS xkr_message;
CREATE TABLE xkr_message(
	id bigint(20) UNSIGNED NOT NULL ,
	from_type_code tinyint(4) NOT NULL ,
	from_id bigint(20) UNSIGNED NOT NULL ,
	to_type_code tinyint(4) NOT NULL ,
	to_id bigint(20) UNSIGNED NOT NULL ,
	content varchar(255) NOT NULL DEFAULT '' ,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;


DROP TABLE IF EXISTS xkr_resource;
CREATE TABLE xkr_resource(
	id bigint(20) UNSIGNED NOT NULL ,
	class_id bigint(20) UNSIGNED NOT NULL ,
	user_id bigint(20) UNSIGNED NOT NULL ,
	cost int(10) UNSIGNED NOT NULL ,
	status tinyint(4) NOT NULL ,
	report tinyint(4) NOT NULL ,
	file_size bigint(20) UNSIGNED NOT NULL ,
	title varchar(255) NOT NULL ,
	detail TEXT  NOT NULL ,
	resource_url varchar(255) NOT NULL ,
	download_count int(10) UNSIGNED NOT NULL DEFAULT 0 ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

insert into xkr_resource values('1','10','100','1','1','0','11','title','detail','resource_url',
1,now(),now(),'{}');
insert into xkr_resource values('2','12','102','2','1','0','11','title','detail','resource_url',
																		1,now(),now(),'{}');
insert into xkr_resource values('3','13','103','3','1','0','11','title','detail','resource_url',
																		1,now(),now(),'{}');
insert into xkr_resource values('4','14','104','4','1','0','11','title','detail','resource_url',
																		1,now(),now(),'{}');

DROP TABLE IF EXISTS xkr_user;
CREATE TABLE xkr_user(
	id bigint(20) UNSIGNED NOT NULL ,
	user_name varchar(64) NOT NULL ,
	user_token varchar(255) NOT NULL ,
	salt varchar(255) NOT NULL ,
	email varchar(64) NOT NULL DEFAULT '' ,
	wealth bigint(20) UNSIGNED NOT NULL ,
	total_recharge bigint(20) UNSIGNED NOT NULL ,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

DROP TABLE IF EXISTS xkr_resource_user;
CREATE TABLE xkr_resource_user(
	id bigint(20) UNSIGNED NOT NULL ,
	resource_id bigint(20) UNSIGNED NOT NULL ,
	user_id bigint(20) UNSIGNED NOT NULL ,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

insert into xkr_resource_user VALUES ('1','10','100','1',now(),now(),'{}');
insert into xkr_resource_user VALUES ('2','12','102','1',now(),now(),'{}');
insert into xkr_resource_user VALUES ('3','13','103','1',now(),now(),'{}');
insert into xkr_resource_user VALUES ('4','14','104','1',now(),now(),'{}');

DROP TABLE IF EXISTS xkr_login_token;
CREATE TABLE xkr_login_token(
	id bigint(20) UNSIGNED NOT NULL ,
	user_id bigint(20) UNSIGNED NOT NULL ,
	login_token varchar(255) NOT NULL ,
	client_ip varchar(64) NOT NULL ,
	status tinyint(4) NOT NULL ,
	login_count int(10) UNSIGNED NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;


DROP TABLE IF EXISTS xkr_class;
CREATE TABLE xkr_class(
	id bigint(20) UNSIGNED NOT NULL ,
	parent_class_id bigint(20) UNSIGNED NOT NULL ,
	path varchar(255) NOT NULL ,
	class_name varchar(64) NOT NULL ,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

DROP TABLE IF EXISTS xkr_resouce_comment;
CREATE TABLE xkr_resouce_comment(
	id bigint(20) UNSIGNED NOT NULL ,
	resource_id bigint(20) UNSIGNED NOT NULL ,
	user_id bigint(20) UNSIGNED NOT NULL ,
  parent_comment_id bigint(20) UNSIGNED NOT NULL,
  root_comment_id bigint(20) UNSIGNED NOT NULL ,
	content TEXT  NOT NULL ,
	client_ip varchar(64) NOT NULL ,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

DROP TABLE IF EXISTS xkr_about_remark;
CREATE TABLE xkr_about_remark(
	id bigint(20) UNSIGNED NOT NULL ,
	user_id bigint(20) UNSIGNED NOT NULL ,
	user_type_code tinyint(4) NOT NULL ,
	parent_remark_id bigint(20) UNSIGNED NOT NULL ,
	root_remark_id bigint(20) UNSIGNED NOT NULL ,
	content TEXT  NOT NULL ,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;


DROP TABLE IF EXISTS xkr_admin_opt_log;
CREATE TABLE xkr_admin_opt_log(
	id bigint(20) UNSIGNED NOT NULL ,
	admin_account_id bigint(20) UNSIGNED NOT NULL ,
	opt_module tinyint(4) NOT NULL ,
	opt_detail varchar(255) NOT NULL DEFAULT '' ,
	client_ip varchar(64) NOT NULL ,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

DROP TABLE IF EXISTS xkr_admin_account;
CREATE TABLE xkr_admin_account(
	id bigint(20) UNSIGNED NOT NULL ,
	account_name varchar(64) NOT NULL ,
	account_token varchar(255) NOT NULL ,
	email varchar(64) NOT NULL DEFAULT '' ,
	role_ids varchar(255) NOT NULL ,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

DROP TABLE IF EXISTS xkr_admin_role;
CREATE TABLE xkr_admin_role(
	id int(10) NOT NULL AUTO_INCREMENT ,
	role_name varchar(20) NOT NULL ,
	permission_ids varchar(255) NOT NULL ,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;


DROP TABLE IF EXISTS xkr_admin_permission;
CREATE TABLE xkr_admin_permission(
	id int(10) NOT NULL AUTO_INCREMENT ,
	permission_name varchar(20) NOT NULL ,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

DROP TABLE IF EXISTS xkr_pay_order;
CREATE TABLE xkr_pay_order(
	id bigint(20) UNSIGNED NOT NULL ,
	user_id bigint(20) UNSIGNED NOT NULL ,
	pay_type_code tinyint(4) NOT NULL ,
	trade_type tinyint(4) NOT NULL ,
	pay_order_no varchar(128) NOT NULL ,
	pre_pay_id varchar(128) NOT NULL DEFAULT '' ,
	pay_id varchar(128) NOT NULL DEFAULT '' ,
	client_ip varchar(64) NOT NULL ,
	pay_amount bigint(20) UNSIGNED NOT NULL ,
	status tinyint(4) NOT NULL ,
	code_url varchar(4) NOT NULL DEFAULT '' ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	expire_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	pay_time DATETIME DEFAULT NULL ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

DROP TABLE IF EXISTS xkr_database_backup;
CREATE TABLE xkr_database_backup(
	id bigint(20) UNSIGNED NOT NULL ,
	backup_name varchar(64) NOT NULL ,
	admin_account_id bigint(20) UNSIGNED NOT NULL ,
	type tinyint(4) NOT NULL ,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
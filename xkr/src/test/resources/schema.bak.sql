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
INSERT INTO xkr_message(id,from_type_code,from_id,to_type_code,to_id,content,status) VALUES ('1','1','2','1','3','2给3发了一个消息','2');
INSERT INTO xkr_message(id,from_type_code,from_id,to_type_code,to_id,content,status) VALUES ('2','1','4','1','3','4给3发了一个消息','1');

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

insert into xkr_resource values('1','10','1','1','1','0','11','title','detail','resource_url',
1,now(),now(),'{}');
insert into xkr_resource values('2','100','2','2','1','0','11','title','detail','resource_url',
																		1,now(),now(),'{}');
insert into xkr_resource values('3','10','3','2','1','0','11','title','detail','resource_url',
																		1,now(),now(),'{}');
insert into xkr_resource values('4','10','4','2','1','0','11','title','detail','resource_url',
																		1,now(),now(),'{}');
insert into xkr_resource values('5','100','1','2','1','0','11','title','detail','resource_url',
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
INSERT INTO xkr_user(id,user_name,user_token,salt,email,wealth,total_recharge,status)
VALUES ('1','manatea1','token','salt','a1@b.c','0','0','1');
INSERT INTO xkr_user(id,user_name,user_token,salt,email,wealth,total_recharge,status)
VALUES ('2','manatea2','token','salt','a2@b.c','0','0','1');
INSERT INTO xkr_user(id,user_name,user_token,salt,email,wealth,total_recharge,status)
VALUES ('3','manatea3','token','salt','a3@b.c','0','0','2');
INSERT INTO xkr_user(id,user_name,user_token,salt,email,wealth,total_recharge,status)
VALUES ('4','manatea4','token','salt','a4@b.c','0','0','3');

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

insert into xkr_resource_user VALUES ('1','1','2','1',now(),now(),'{}');
insert into xkr_resource_user VALUES ('2','2','1','1',now(),now(),'{}');
insert into xkr_resource_user VALUES ('3','4','3','1',now(),now(),'{}');
insert into xkr_resource_user VALUES ('4','3','4','1',now(),now(),'{}');
insert into xkr_resource_user VALUES ('5','1','4','1',now(),now(),'{}');

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

insert into xkr_login_token (id,user_id,login_token,client_ip,status,login_count) VALUES ('1','1','token','1','1','1');
insert into xkr_login_token (id,user_id,login_token,client_ip,status,login_count) VALUES ('2','2','token1','1','2','2');
insert into xkr_login_token (id,user_id,login_token,client_ip,status,login_count) VALUES ('3','3','token2','1','1','3');
insert into xkr_login_token (id,user_id,login_token,client_ip,status,login_count) VALUES ('4','2','token1','1','1','3');


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
INSERT INTO xkr_class(id,parent_class_id,path,class_name,status) VALUES ('1','0','0-1','毕设','1');
INSERT INTO xkr_class(id,parent_class_id,path,class_name,status) VALUES ('2','0','0-2','资源','1');
INSERT INTO xkr_class(id,parent_class_id,path,class_name,status) VALUES ('10','1','0-1-10','毕设1-1','1');
INSERT INTO xkr_class(id,parent_class_id,path,class_name,status) VALUES ('100','10','0-1-10-100','毕设1-1-1','1');


DROP TABLE IF EXISTS xkr_resource_comment;
CREATE TABLE xkr_resource_comment(
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
insert into xkr_resource_comment (id,resource_id,user_id,parent_comment_id,root_comment_id,content,client_ip,status)
VALUES ('1','1','1','1','1','1','127.0.0.1',1);
insert into xkr_resource_comment (id,resource_id,user_id,parent_comment_id,root_comment_id,content,client_ip,status)
VALUES ('2','1','2','2','2','1','127.0.0.1',2);
insert into xkr_resource_comment (id,resource_id,user_id,parent_comment_id,root_comment_id,content,client_ip,status)
VALUES ('3','1','3','3','3','1','127.0.0.1',1);
insert into xkr_resource_comment (id,resource_id,user_id,parent_comment_id,root_comment_id,content,client_ip,status)
VALUES ('4','1','1','4','4','1','127.0.0.1',2);
insert into xkr_resource_comment (id,resource_id,user_id,parent_comment_id,root_comment_id,content,client_ip,status)
VALUES ('5','1','1','5','5','1','127.0.0.1',3);


DROP TABLE IF EXISTS xkr_about_remark;
CREATE TABLE xkr_about_remark(
	id bigint(20) UNSIGNED NOT NULL ,
	user_id bigint(20) UNSIGNED NOT NULL ,
	user_type_code tinyint(4) NOT NULL ,
	parent_remark_id bigint(20) UNSIGNED NOT NULL ,
	content TEXT  NOT NULL ,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

insert into xkr_about_remark (id,user_id,user_type_code,parent_remark_id,content,status) VALUES ('1','1','1','0','1','1');
insert into xkr_about_remark (id,user_id,user_type_code,parent_remark_id,content,status) VALUES ('2','2','2','1','2','1');
insert into xkr_about_remark (id,user_id,user_type_code,parent_remark_id,content,status) VALUES ('3','3','1','0','3','2');
insert into xkr_about_remark (id,user_id,user_type_code,parent_remark_id,content,status) VALUES ('4','4','1','0','4','1');


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

insert into xkr_admin_opt_log (id,admin_account_id,opt_module,opt_detail,client_ip,status) VALUES ('1','1','1','C','127.0.0.1','1');
insert into xkr_admin_opt_log (id,admin_account_id,opt_module,opt_detail,client_ip,status) VALUES ('2','1','2','R','127.0.0.1','1');
insert into xkr_admin_opt_log (id,admin_account_id,opt_module,opt_detail,client_ip,status) VALUES ('3','2','3','U','127.0.0.1','2');
insert into xkr_admin_opt_log (id,admin_account_id,opt_module,opt_detail,client_ip,status) VALUES ('4','2','4','D','127.0.0.1','1');



DROP TABLE IF EXISTS xkr_admin_account;
CREATE TABLE xkr_admin_account(
	id bigint(20) UNSIGNED NOT NULL ,
	account_name varchar(64) NOT NULL ,
	account_token varchar(255) NOT NULL ,
	email varchar(64) NOT NULL DEFAULT '' ,
	role_id int(10) NOT NULL,
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

insert into xkr_admin_account (id,account_name,account_token,email,role_id,status) VALUES ('1','zqx','1','e@qq.com','1','1');
insert into xkr_admin_account (id,account_name,account_token,email,role_id,status) VALUES ('2','qxz','12','a@qq.com','1','2');
insert into xkr_admin_account (id,account_name,account_token,email,role_id,status) VALUES ('3','xqx','123','a@163.com','2','1');


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

insert into xkr_admin_role (id,role_name,permission_ids,status) VALUES ('1','role1','1','1');
insert into xkr_admin_role (id,role_name,permission_ids,status) VALUES ('2','role2','1;2','1');
insert into xkr_admin_role (id,role_name,permission_ids,status) VALUES ('3','role3','1;2','2');


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

insert into xkr_admin_permission (id,permission_name,status) VALUES ('1','p1','1');
insert into xkr_admin_permission (id,permission_name,status) VALUES ('2','p2','1');
insert into xkr_admin_permission (id,permission_name,status) VALUES ('3','p3','2');


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
	status tinyint(4) NOT NULL ,
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ,
	ext varchar(1024) NOT NULL DEFAULT '{}' ,
	PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

insert into xkr_database_backup (id,backup_name,admin_account_id,status) VALUES ('1','b1','1','1');
insert into xkr_database_backup (id,backup_name,admin_account_id,status) VALUES ('2','b2','1','1');
insert into xkr_database_backup (id,backup_name,admin_account_id,status) VALUES ('3','b3','2','1');
insert into xkr_database_backup (id,backup_name,admin_account_id,status) VALUES ('4','b4','2','2');

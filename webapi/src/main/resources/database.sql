use hackthon_demo

drop table if exists user_t;
create table user_t(
	id int not null auto_increment primary key,
	name nvarchar(64) not null unique key,
	balance double not null,
	lastModify bigint not null,
	status int not null default 0,
	photo text null,
	feature text null
);

drop table if exists product_t;
create table product_t (
	id int not null auto_increment primary key,
	name nvarchar(255) not null unique key,
	unitPrice double not null,
	lastModify bigint not null,
	code nvarchar(20) not null unique key,
	qrCode text null
);

drop table if exists journal_t;
create table journal_t(
	id int not null auto_increment primary key,
	ts bigint not null,
	logType int not null,
	userId int not null,
	userFeature text not null,
	constraint foreign key user_key(userId)
			   references user_t(id)
);

drop table if exists orderproduct_t;
create table orderproduct_t (
	id int not null auto_increment primary key,
	productid int not null,
	journalid int not null,
	quantity int not null,
	constraint foreign key product_key(productid) 
			   references product_t(id),
	constraint foreign key journal_key(journalid)
			   references journal_t(id)
);

drop table if exists m_dev;
drop table if exists m_rev;

create table m_dev
(
   _id   integer not null primary key,
  dev_uuid varchar(64) NOT NULL,
  dev_mac varchar(24) DEFAULT NULL,
  dev_name varchar(128) DEFAULT NULL,
  wifi_mac varchar(24) DEFAULT NULL ,
  locked smallint(6) DEFAULT NULL,
  locked_phone decimal(16,0) DEFAULT NULL,
  locked_time datetime DEFAULT NULL,
  inet_ip varchar(16) DEFAULT NULL,
  inet_port decimal(6,0) DEFAULT NULL ,
  dev_type varchar(20) NOT NULL DEFAULT 'test' ,
  app_type varchar(20) NOT NULL DEFAULT 'test' 
);

CREATE TABLE m_rev (
   _id     integer not null primary key,
  rev_id integer  NULL ,
  dev_uuid varchar(64) NOT NULL,
  rev_time varchar(14) DEFAULT NULL ,
  measure_value varchar(200) DEFAULT NULL 
);


------------------------------------------------------------------------------

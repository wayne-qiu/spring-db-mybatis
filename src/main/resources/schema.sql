create table employees
(
    id integer not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    email_address varchar(255) not null,
    createBy integer,
    updateBy integer,
    createTime DATETIME,
    updateTime DATETIME,
    version integer not null default 0,
    primary key(id)
);
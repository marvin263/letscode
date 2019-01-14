
drop table if exists g;
create table g(name varchar(10), i int, primary key (name), index idx_i(i)) engine = InnoDB;
insert into g values('a', 5), ('b', 8), ('c', 10), ('d', 10), ('e', 11), ('f', 15);



drop table if exists t_pk_uk;
create table t_pk_uk(a varchar(10), b int, c int, d varchar(10), primary key(a), unique iux_b(b), index idx_c(c)) engine = InnoDB;
insert into t_pk_uk values('a',11,22,'dataa'),('b',111,222,'datab'),('c',1111,2222,'datac'),('d',11111,22222,'datad'),('e',111111,222222,'datae');




drop table if exists t2;
create table t2(c1 varchar(10), c2 int, PRIMARY KEY(c1), key(c2)) engine = InnoDB;
insert into t2 values ('a', 2);




drop table if exists t;
create table t(c1 varchar(10), c2 int, PRIMARY KEY(c1), key(c2)) engine = InnoDB;
insert into t values ('a', 5);
insert into t values ('b', 11);
insert into t values ('c', 18);

select * from t;


-- 列上有或者没有索引时，看锁是什么样的
drop table if exists t_pk;
create table t_pk(a int, b int, primary key (b)) engine = InnoDB;
insert into t_pk values(1, 2), (11,22), (111,222);



drop table if exists t_idx;
create table t_idx(id int, a int, b int, index idx_b(b), primary key (id)) engine = InnoDB;
insert into t_idx values(1, 1, 2), (11, 11,22), (111, 111,222), (1111, 1111,2222), (11111, 11111,22222), (111111, 111111,222222);


drop table if exists t_nokey;
create table t_nokey(a int, b int) engine = InnoDB;
insert into t_nokey values(1, 2), (11,22), (111,222);

drop table if exists t_uk;
create table t_uk(k varchar(10), a int, b int, primary key (k), index idx_a(a), unique iux_b(b)) engine = InnoDB;
insert into t_uk values('a', 1, 6), ('b',7, '14'), ('c',13, '22'), ('d',19, '32'), ('e',23, '46');


drop table if exists t_pk_2;
create table t_pk_2(a int, b int, index idx_a(a), primary key (b)) engine = InnoDB;
insert into t_pk_2 values(1, 2), (11,22), (111,222);



drop table if exists t_idx_2;
create table t_idx_2(k varchar(10), a int, b int, index idx_a(a), index idx_b(b), primary key (k)) engine = InnoDB;
insert into t_idx_2 values('a', 1, 2), ('b', 11,22), ('c', 111,222), ('d', 1111,2222), ('e', 11111,22222), ('f', 111111,222222);


drop table if exists t_nokey_2;
create table t_nokey_2(a int, b int, index idx_a(a)) engine = InnoDB;
insert into t_nokey_2 values(1, 2), (11,22), (111,222);

drop table if exists t_uk_2;
create table t_uk_2(a int, b int, c int, d varchar(10), index idx_a(a), unique iux_b_c(b, c)) engine = InnoDB;
insert into t_uk_2 values(1, 2,3,'a'), (11,22,33,'b'), (111,222,333,'c'), (1111,2222,3333,'d'), (11111,22222,33333,'e'), (111111,222222,333333,'f');

drop table if exists t1;
create table t1 (i int, primary key (i)) engine = InnoDB;


drop table if exists z;
create table z(a int, b int, PRIMARY KEY(a), key(b)) engine = InnoDB;
insert into z values(1,1), (3,1), (5,3),(7,6), (10,8);

drop table if exists ii;
create table ii(a int auto_increment, b int, PRIMARY KEY(a), key(b)) engine = InnoDB;
insert into ii(b) values (4);
insert into ii(b) values (7);

drop table if exists ti;
create table ti(i int, index idx_i(i)) engine = InnoDB;
insert into ti values (4);
insert into ti values (7);

SELECT 
    ENGINE_LOCK_ID as LOCK_ID,
    ENGINE_TRANSACTION_ID as TRX_ID,
    INDEX_NAME,
    LOCK_TYPE,
    LOCK_MODE,
    LOCK_STATUS,
    LOCK_DATA
FROM
    performance_schema.data_locks;



drop table if exists dl_test;
CREATE TABLE dl_test(k varchar(10), i tinyint(4), PRIMARY KEY(k), index idx_k(k)) ENGINE = InnoDB;
INSERT INTO dl_test VALUES('a', 0);


drop table if exists dl;
CREATE TABLE dl (i INT) ENGINE = InnoDB;
INSERT INTO dl (i) VALUES(1);
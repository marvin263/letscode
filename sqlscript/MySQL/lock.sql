
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
create table t_uk(a int, b int, unique iux_b(b)) engine = InnoDB;
insert into t_uk values(1, 2), (11,22), (111,222), (1111,2222), (11111,22222), (111111,222222);


drop table if exists t_pk_2;
create table t_pk_2(a int, b int, index idx_a(a), primary key (b)) engine = InnoDB;
insert into t_pk_2 values(1, 2), (11,22), (111,222);



drop table if exists t_idx_2;
create table t_idx_2(id int, a int, b int, index idx_a(a), index idx_b(b), primary key (id)) engine = InnoDB;
insert into t_idx_2 values(1, 1, 2), (11, 11,22), (111, 111,222), (1111, 1111,2222), (11111, 11111,22222), (111111, 111111,222222);


drop table if exists t_nokey_2;
create table t_nokey_2(a int, b int, index idx_a(a)) engine = InnoDB;
insert into t_nokey_2 values(1, 2), (11,22), (111,222);

drop table if exists t_uk_2;
create table t_uk_2(a int, b int, index idx_a(a), unique iux_b(b)) engine = InnoDB;
insert into t_uk_2 values(1, 2), (11,22), (111,222), (1111,2222), (11111,22222), (111111,222222);




drop table if exists z;
create table z(a int, b int, PRIMARY KEY(a), key(b)) engine = InnoDB;
insert into z values(1,1), (3,1), (5,3),(7,6), (10,8);





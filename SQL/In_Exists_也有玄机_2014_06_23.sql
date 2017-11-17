drop table t1;
drop table t2;
-- create T1 and T2
CREATE TABLE T1 
 (  ID NUMBER,
    C1 VARCHAR2(20 BYTE)
 );

CREATE TABLE T2
 (  ID NUMBER,
    C1 VARCHAR2(20 BYTE)
 );

   
insert into T1(ID, C1) values(1, 'a');
insert into T1(ID, C1) values(2, 'b');
insert into T1(ID, C1) values(3, null);
insert into T1(ID, C1) values(4, 'c');
insert into T1(ID, C1) values(5, 'd');

insert into t2(ID, C1) values(1, 'a');
insert into t2(ID, C1) values(2, 'b');

select * from T1;
select * from t2;

-- 很好理解。就查出了 T1 中的：a, b 两行
select * from T1 where T1.C1 in (select t2.c1 from t2);
-- 很好立即。就查出了 T1 中的：a, b 两行
select * from T1 where EXISTS (select 1 from t2 where t2.C1 = T1.C1);


-- 后面 NOT in 就不好理解了；NOT exists 还是正常的好同志嘛


-- 说不过去了。a, b肯定不在 结果集 中。c, d 肯定出现在结果集中。
-- null 呢？ null 也确实 "not in (a, b)" 的啊，即，null也要出现在 结果集 中的啊？
-- 事实上，null就是 没 出现在结果集中的
select * from T1 where T1.C1 not in (select t2.c1 from t2);


-- 注意：子查询from 中，并没有 T1，但，子查询 确实 用到了 父查询 的表字段
-- （虽然，子查询from 中没有出现 父查询 的那个表）
select * from T1 where NOT EXISTS (select 1 from t2 where t2.C1 = T1.C1);
-- 上面这个，和 下面这个是等价的
select * from T1 left join t2 on T1.c1 = t2.c1 where t2.c1 is null;





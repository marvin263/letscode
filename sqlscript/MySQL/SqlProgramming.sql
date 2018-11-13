
drop table if exists test_customers;
create table test_customers(id int, name varchar(32));

drop table if exists test_orders;
create table test_orders(orderId bigint, customerId int);

insert into test_customers select 1, 'marvin';
insert into test_customers select 2, 'susan';
insert into test_customers select 3, 'doudou';
insert into test_customers select 4, 'maomao';

insert into test_orders select 91, 1;
insert into test_orders select 94, 4;

select * from test_customers C
where not exists (select * from test_orders O where C.id=O.customerId);

select * from test_customers C
where C.id not in (select customerId from test_orders);

-- 上面两个select执行结果是一样的，但，当加入下面这个语句后
-- 再执行以下上面这两个sql，结果就不同了
insert into test_orders select 95, null;

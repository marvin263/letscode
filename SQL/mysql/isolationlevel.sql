
-- varchar 保留末尾空格；char删除末尾空格
DROP TABLE IF EXISTS `test_isolation_level`;
CREATE TABLE `test_isolation_level` (
`a` int(11) NOT NULL,
`b` varchar(10) NOT NULL,
`c` char(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into test_isolation_level() values (1, 'bike1','car1'),(2, 'bike2','car2'),(3, 'bike3','car3');


-- 1.查看 当前 会话隔离级别
select @@tx_isolation;

-- 2.查看 系统当前 隔离级别
select @@global.tx_isolation;

-- 3.设置当前会话隔离级别
set session transaction isolation level repeatable read;

-- 4.设置系统当前隔离级别
set global transaction isolation level repeatable read;

-- 5.命令行，开始事务时
set autocommit=off 或者 start transaction


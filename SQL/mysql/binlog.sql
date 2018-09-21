
-- 启用满查询 slow_log
select * from mysql.slow_log;
set @@global.slow_query_log='ON';
set @@global.slow_query_log_file='C:\mysql-5.6.23-winx64\data\TN-00-50000038-slow.log';
set @@global.log_output='TABLE';
set @@global.long_query_time=1.000000;
set @@session.long_query_time=1.000000;
select sleep(2);
select * from mysql.slow_log;



-- 正儿八经的，执行了哪些 更新的sql
-- （即使更新并没有修改掉数据，例如delete时删除0行数据）\
-- 1. 第一个 binlog 文件的内容
show binlog events;
-- 2. 指定的 binlog 文件的内容
show binlog events in 'binarylog.000001';
-- 3. 所有的 binlog 文件列表
show binary logs;
-- 4. 查看 当前正在写入的 binlog 文件
show master status;
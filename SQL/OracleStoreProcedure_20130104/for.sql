-- (1)循环遍历游标
create or replace procedure if_1() as
Cursor cursor is select name from student;
name varchar(20);
begin
	
	for name in cursor LOOP
		begin
		 dbms_output.putline(name); 
		end;
	end LOOP;
	
end if_1;

-- (2)循环遍历数组
create or replace procedure if_2(varArray in myPackage.TestArray) as
--(输入参数varArray 是自定义的数组类型，定义方式见标题6)
i number;
begin
	i := 1; -- 存储过程数组是起始位置是从1开始的，与java、C、C++等语言不同。
			-- 因为在Oracle中本是没有数组的概念的，数组其实就是一张
			-- 表(Table),每个数组元素就是表中的一个记录，所以遍历数组时就相当于从表中的第一条记录开始遍历
	for i in 1..varArray.count LOOP -- 从 1 开始；有两个点
		dbms_output.putline(&apos;The No.&apos;|| i || &apos;record in varArray is:&apos;||varArray(i));   
	end LOOP;
end if_2;

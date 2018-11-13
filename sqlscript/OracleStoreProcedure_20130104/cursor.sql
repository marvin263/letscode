-- Oracle中Cursor是非常有用的，用于遍历临时表中的查询结果
-- 其相关方法和属性也很多，现仅就常用的用法做一二介绍


-- (1)Cursor型游标(不能用于参数传递)
create or replace procedure DemoCursor() is  
cursor_1 Cursor is select std_name from student where  ...;  --Cursor的使用方式1
cursor_2 Cursor;
begin
	select class_name into cursor_2 from class where ...;  --Cursor的使用方式2
end DemoCursor;


-- (2)SYS_REFCURSOR型游标，该游标是Oracle以预先定义的游标，可作出参数进行传递
create or replace procedure DemoSys_Refcursor(rsCursor out SYS_REFCURSOR) is
cursor SYS_REFCURSOR;
name varhcar(20);
begin
	OPEN cursor FOR select name from student where ... --SYS_REFCURSOR只能通过OPEN方法来打开和赋值
	LOOP
		fetch cursor into name   --SYS_REFCURSOR只能通过 fetch into 来打开和遍历 exit when cursor%NOTFOUND;              --SYS_REFCURSOR中可使用三个状态属性：                                         ---%NOTFOUND(未找到记录信息) %FOUND(找到记录信息)                                         ---%ROWCOUNT(然后当前游标所指向的行位置)
		dbms_output.putline(name);
	end LOOP;
	rsCursor := cursor;
end DemoSys_Refcursor;

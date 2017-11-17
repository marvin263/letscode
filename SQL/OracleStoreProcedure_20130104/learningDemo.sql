-- 下面写一个简单的例子来对以上所说的存储过程的用法做一个应用：

-- 现假设存在两张表：
-- 一张是学生成绩表(studnet)，字段为：stdId,math,article,language,music,sport,total,average,step
-- 一张是学生课外成绩表(out_school),字段为:stdId,parctice,comment

-- 通过存储过程自动计算出每位学生的总成绩和平均成绩，同时，如果学生在课外课程中获得的评价为A，就在总成绩上加20分。



-- （1）创建存储过程 autocomputer
create or replace procedure autocomputer(step in number) is

rsCursor SYS_REFCURSOR;

commentArray myPackage.myArray;

math number;

article number;

language number;

music number;

sport number;

total number;

average number;

stdId varchar(30);

record myPackage.stdInfo;

i number;

begin

i := 1;

get_comment(commentArray); --调用名为get_comment()的存储过程获取学生课外评分信息

OPEN rsCursor for select stdId,math,article,language,music,sport from student t where t.step = step;

LOOP

fetch rsCursor into stdId,math,article,language,music,sport; exit when rsCursor%NOTFOUND;

total := math + article + language + music + sport;

for i in 1..commentArray.count LOOP 

 record := commentArray(i);    

if stdId = record.stdId then  

 begin     

 if record.comment = &apos;A&apos; then     

  begin         

 total := total + 20;   

   go to next; --使用go to跳出for循环       

  end;    

end if;  

end;  

end if;

end LOOP;

<<continue>>  average := total / 5;

 update student t set t.total=total and t.average = average where t.stdId = stdId;

end LOOP;

end;

end autocomputer;



-- （2）取得学生评论信息的存储过程

create or replace procedure get_comment(commentArray out myPackage.myArray) is

rs SYS_REFCURSOR；

record myPackage.stdInfo;

stdId varchar(30);

comment varchar(1);

i number;

begin

open rs for select stdId,comment from out_school

i := 1;

LOOP

 fetch rs into stdId,comment; exit when rs%NOTFOUND;

record.stdId := stdId;

 record.comment := comment;

recommentArray(i) := record;

i:=i + 1;

end LOOP;

end get_comment;


-- （3）定义数组类型myArray
create or replace package myPackage is begin

type stdInfo is record(stdId varchar(30),comment varchar(1));

type myArray is table of stdInfo index by binary_integer;

end myPackage;
-- 有定义 和 执行部分 的 block
declare
	v_ename   varchar2(5); -- 定义字符串 变量
	v_sal     number(7,2); -- 定义字符串 变量
begin
	-- & 表示要接受    从控制台    输入的变量
	select ename, sal into v_ename, v_sal from emp where empno=&no;
	dbms_output.put_line('雇员名：'||v_ename);
end;
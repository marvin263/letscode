create or replace procedure DemoWhile(i in number) as
begin
	--开始 while
	while i < 10 LOOP
		begin    
		 i:= i + 1;
		end;
	end LOOP;
end DemoWhile;
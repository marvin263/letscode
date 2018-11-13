create or replace procedure DemoIf(x in number) is
begin
	-- if 开始
	if x >0 then
		begin
			x := 0 - x;
		end;
	end if;
	-- if 开始	
	if x = 0 then
		begin
			x: = 1;
	    end;
    end if;
    
end DemoIf;
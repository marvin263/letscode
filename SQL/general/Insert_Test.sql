DROP TABLE EI.TEST_INSERT;
CREATE TABLE EI.TEST_INSERT (
	COL_1 SMALLINT ,
	COL_2 VARCHAR (100)  WITH DEFAULT 'MarvinLi, SusanJia' ,
	COL_3 VARCHAR (256)  NOT NULL  WITH DEFAULT 'We''re Truition team' ,
	COL_4 VARCHAR (512)  NOT NULL
) ;
DROP TABLE EI.DESC_TEST_INSERT;
CREATE TABLE EI.DESC_TEST_INSERT (
	COL_1 SMALLINT ,
	COL_2 VARCHAR (100)  WITH DEFAULT 'MarvinLi, SusanJia' ,
	COL_3 VARCHAR (256)  NOT NULL  WITH DEFAULT 'We''re Truition team' ,
	COL_4 VARCHAR (512)  NOT NULL
) ;

--  Failed: 既然没有 明确写出列名，则  VALUES 中 必须包含 所有列的值
--  想想其实也简单，如果 VALUES 不全，天知道哪列应该不给值
INSERT INTO EI.TEST_INSERT VALUES ('COL_2 VALUE', 'Truition', 'COL_4');

--  Failed: COL_3 使得执行出错
--  记住一点：明目张胆的将 NULL 插入 NOT NULL 当然会出错，即使 此列有默认值
INSERT INTO EI.TEST_INSERT VALUES (1, NULL, NULL, 'CDC has been purchased by Vista');

--  Successfully
--  有默认值的列（无论此列 是否NOT NULL），只有 不插入任何值 时，才使用默认值
--  注意：插入 NULL 也是 插入某个值
INSERT INTO EI.TEST_INSERT(COL_1, COL_2, COL_4)	VALUES (2, NULL, 'Vista is good');

--  下面，我们看 “一次插入多条数据”，insert......select
--  注意：这不是奇技淫巧，这是 SQL 标准的规定，就和 select 一样正常
--  Successfully
--    1. INSERT 中 没有VALUES 关键字
--    2. 列名 即可以出现也可以不出现
INSERT INTO EI.DESC_TEST_INSERT(COL_1, COL_2, COL_4)
			SELECT COL_1, COL_2, COL_4 FROM EI.TEST_INSERT ORDER BY COL_1;
			
INSERT INTO EI.DESC_TEST_INSERT
			SELECT * FROM EI.TEST_INSERT ORDER BY COL_1;
			
INSERT INTO EI.DESC_TEST_INSERT(COL_1, COL_2, COL_3, COL_4)
			SELECT * FROM EI.TEST_INSERT ORDER BY COL_1;

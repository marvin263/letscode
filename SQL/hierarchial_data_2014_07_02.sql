-- Create table
CREATE TABLE DEP
  (
    DEPID      NUMBER(10) NOT NULL,
    DEPNAME    VARCHAR2(256),
    UPPERDEPID NUMBER(10)
  );
-- Insert demo data
INSERT INTO DEP(DEPID, DEPNAME, UPPERDEPID) VALUES (0, '总经办', null);
INSERT INTO DEP(DEPID, DEPNAME, UPPERDEPID) VALUES (1, '开发部', 0);
INSERT INTO DEP(DEPID, DEPNAME, UPPERDEPID) VALUES (3, 'Sever开发部', 1);
INSERT INTO DEP(DEPID, DEPNAME, UPPERDEPID) VALUES (4, 'Client开发部', 1);

INSERT INTO DEP(DEPID, DEPNAME, UPPERDEPID) VALUES (2, '测试部', 0);
INSERT INTO DEP(DEPID, DEPNAME, UPPERDEPID) VALUES (5, 'TA测试部', 2);
INSERT INTO DEP(DEPID, DEPNAME, UPPERDEPID) VALUES (6, 'Project测试部', 2);

-- Select hierarchial data
/**
	说明： 
	1. CONNECT_BY_ROOT 返回当前节点的 最顶端节点
	 
	2. CONNECT_BY_ISLEAF 判断是否为叶子节点。如果此节点下面有子节点，则不为叶子节点
	 
	3. LEVEL 伪列，表示节点深度（从 1 开始）
	 
	4. SYS_CONNECT_BY_PATH函数 显示详细路径，并用“/”分隔
 *
*/
SELECT 
    RPAD(' ', 2*(LEVEL-1), '-') || DEPNAME "DEPNAME",
    CONNECT_BY_ROOT DEPNAME "ROOT",
    CONNECT_BY_ISLEAF "ISLEAF",
    LEVEL,
    SYS_CONNECT_BY_PATH(DEPNAME, '/') "PATH"
FROM DEP
START WITH UPPERDEPID IS NULL
CONNECT BY PRIOR DEPID = UPPERDEPID;


-- 再来一个例子
-- 1. with x as ()，生成一个临时表（注意：后面没有逗号）
-- 2. level n，仅仅是 n 这一层，有多少数据？
-- 答案：level(n-1) * 表总行数
with x as (select 'aa' chr from dual UNION ALL select 'bb' chr from dual)
select level, chr, (lpad(' ', (level-1)*2, '-')||chr) other from x connect by level <= 4 order by level;






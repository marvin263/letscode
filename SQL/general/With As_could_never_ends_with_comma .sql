-- 1. 首先是表名    2. 接着是 表字段
WITH EI.MARVIN_1 (M11, M12, M13) AS
    (
        SELECT CSKU_ID, CSKU_NAME, CSKU_CREATETS
        FROM
            EI.CATALOGSKU
        WHERE
            CSKU_CTL_NBR=3295
    ),
-- 3. 也可以加上  第二个  临时表
    EI.MARVIN_2           as (select * from ei.storeItem fetch first 10 rows only),
-- 4. 第三个  临时表    
    EI.MARVIN_3(M31, M32) as (select ord_id, ord_state from ei.orders fetch first 10 rows only) -- 4. 您可看好了，这里绝对 不能有 逗号的
SELECT * FROM EI.MARVIN_1, EI.MARVIN_2, EI.MARVIN_3; -- 5. 到这里整个 WITH AS 就结束了

SELECT COUNT(*) FROM EI.MARVIN_1; -- 6. 因为 WITH AS 已经结束了，此条是 不能一起 运行的

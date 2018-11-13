--  AS别名 “不能用在CASE中，也不能用在子查询中，只能显示结果时用”

-- 1. AS别名 不能 用在 子查询中（注意：不能哦，亲）
SELECT
    csku_id AS id,--AS别名 id
    (
        SELECT
            SI_PRICE
        FROM
            EI.STOREITEM
        WHERE
        	-- 此处的子查询，可以使用  csku_id；
        	-- 此处的子查询，不能使用 AS别名 id
            csku_id=SI_CSKU_ID FETCH FIRST 1 rows only
	) AS store_item_price,
    csku_name
FROM
    ei.catalogsku;
    
-- 2. AS别名也不能 用在CASE中
SELECT
    CNTRY_NAME,
    CNTRY_CONTINENT AS Continent,
    CASE -- 注意：CASE中用的字段 是别名 Continent（这样不对，执行出错）
        WHEN Continent IS NULL THEN 'Continent IS NULL'
        ELSE CNTRY_CONTINENT  -- 注意：CNTRY_CONTINENT 是表的真正字段名，这是对的（这里当然 也不能 用 Continent）
    END AS MARVIN_case_when_then_else_end
FROM
    EI.COUNTRIES;

SELECT
    CNTRY_NAME,
    CNTRY_CONTINENT AS Continent,
    CASE -- 注意：CASE用的字段 是表的真正字段名 CNTRY_CONTINENT（“不是别名 Continent” 哦，亲）
        WHEN CNTRY_CONTINENT IS NULL THEN 'Continent IS NULL'
        ELSE CNTRY_CONTINENT
    END AS MARVIN_case_when_then_else_end -- 此列的列名是：MARVIN_case_when_then_else_end
FROM
    EI.COUNTRIES;

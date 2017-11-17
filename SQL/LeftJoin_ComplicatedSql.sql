SELECT
    si_id,
    csku_name,
    COALESCE((
            CASE
                WHEN cstl.cstl_title IS NULL
                THEN
                    (
                        SELECT
                            cstl2.cstl_title
                        FROM
                            ei.catalogskutitleloc cstl2
                        WHERE
                            cstl2.cstl_csku_id = c0.csku_id
                        ORDER BY
                            cstl2.cstl_idx
                        FETCH
                            FIRST 1 rows only
                    )
                ELSE trim(cstl.cstl_title)
            END ),'') AS MARVIN_si_title,
    CASE
        -- case 中，只能用真的字段名，不能用AS的字段名。即，此处不能用 AS出来的 “MARVIN_si_title”
        WHEN cstl.cstl_title IS NULL
        THEN
            (
                SELECT
                    cstl2.cstl_lang
                FROM
                    ei.catalogskutitleloc cstl2
                WHERE
                    cstl2.cstl_csku_id = c0.csku_id
                ORDER BY
                    cstl2.cstl_idx
                FETCH
                    FIRST 1 rows only
            )
        ELSE cstl.cstl_lang
    END AS cstl_lang,
    COALESCE( (
            CASE
                WHEN si_img_nbr = -1
                THEN
                    (
                        SELECT
                            CASE
                                WHEN img_full IS NULL
                                THEN img_thmb
                                ELSE img_full
                            END AS img_full
                        FROM
                            ei.skuimgrel b ,
                            ei.Images a
                        WHERE
                            c2.si_csku_id=b.csku_id
                        AND a.img_nbr=b.img_nbr
                        ORDER BY
                            sim_index
                        FETCH
                            FIRST 1 rows only
                    )
                ELSE
                    (
                        SELECT
                            CASE
                                WHEN img_full IS NULL
                                THEN img_thmb
                                ELSE img_full
                            END AS img_full
                        FROM
                            ei.Images aa
                        WHERE
                            aa.img_nbr = si_img_nbr
                    )
            END ) , '') AS img_full,
    si_csku_id,
    si_sc_Id ,
    sc_unique_code,
    CASE
        WHEN csku_qunlimited = 'Y'
        THEN 1
        ELSE
            CASE
                WHEN csku_bundle='Y'
                THEN
                    (
                        SELECT
                            MIN(INT(c1.csku_quantity/sbd_req_qty)) AS qty_avail
                        FROM
                            ei.catalogsku c1,
                            ei.skubundles
                        WHERE
                            c1.csku_id=sbd_csku_id
                        AND sbd_bsku_id=c0.csku_id
                    )
                ELSE csku_quantity
            END
    END AS csku_quantity,
    csku_upccode,
    csku_master,
    si_price,
    (
        SELECT
            si_id
        FROM
            ei.storeitem c4
        WHERE
            vsku_csku_id = c4.si_csku_id
    )
    vsku_si_id
FROM
    ei.StoreCategory,
    -- 无论如何， left join 时，反正就只需要两张表进行 join
    ei.catalogsku c0 LEFT JOIN ei.VariantSku ON vsku_msku_id = c0.csku_id,
    -- 1、c2 先和 ei.storeitemtitle 进行 left join
    -- 2、上步得到的 “大的结果集” 再 和 cstl 进行 left join
    (ei.storeitem c2 LEFT JOIN ei.storeitemtitle ON sit_si_id = c2.si_id) LEFT JOIN ei.catalogskutitleloc cstl ON cstl_id = sit_cstl_id

WHERE
    c2.si_csku_id=c0.csku_id
AND si_sc_id = sc_id
AND CSKU_DELETED = 'N'
AND csku_variant = 'N'
AND csku_ctl_nbr = 3295
AND sc_ctl_nbr = 3295
AND
    (
        vsku_csku_id IS NULL
     OR
        (
            vsku_csku_id IS NOT NULL
        AND
            (
                SELECT
                    csku_deleted
                FROM
                    ei.catalogsku c3
                WHERE
                    c3.csku_id = vsku_csku_id
            )
            = 'N'
        )
    )
    
ORDER BY
    si_id,
    vsku_si_id;
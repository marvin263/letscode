SELECT
    ord_delta_prodamt,
    ord_delta_taxamt,
    ord_delta_shipamt,
    ord_delta_insurance,
    vns_id,
    vns_currency,
    vns_curr_sign,
    shp_id,
    ori_bih_id,
    ori_csku_id,
    ord_createts +17 hours +0 minutes  AS ord_createtsAdjusted,
    ord_chargedts +17 hours +0 minutes AS ord_chargedtsAdjusted,
    ord_ref_rmt_id,
    ord_paymethod,
    ord_msc_id,
    ord_promocode,
    ord_discount,
    ord_cod_fee,
    ord_option_amt,
    csku_name,
    CSKU_DIMHEIGHT,
    CSKU_DIMWIDTH,
    CSKU_DIMDEPTH,
    CSKU_DIMUNIT,
    csku_cost,
    shp_cost,
    ori_id,
    ori_bidamt,
    csku_weight,
    ori_insurance,
    ord_taxamt,
    ord_total,
    ord_giftnote,
    ord_instructions,
    csku_wgtunit,
    csku_retail_price,
    vec_name,
    ord_vec_id,
    ord_serial_number,
    ord_csr_nick,
    a1.add_title                   AS add_title,
    a1.add_fname                   AS add_fname,
    a1.add_lname                   AS add_lname,
    a1.add_address1                AS add_address1,
    a1.add_address2                AS add_address2,
    a1.add_city                    AS add_city,
    a1.add_state                   AS add_state,
    a1.add_zip                     AS add_zip,
    a1.add_country                 AS add_country,
    a1.add_phone                   AS add_phone,
    a1.add_email                   AS add_email,
    a1.add_company_name            AS add_company_name,
    a2.add_title                   AS billto_add_title,
    a2.add_fname                   AS billto_add_fname,
    a2.add_lname                   AS billto_add_lname,
    a2.add_address1                AS billto_add_address1,
    a2.add_address2                AS billto_add_address2,
    a2.add_city                    AS billto_add_city,
    a2.add_state                   AS billto_add_state,
    a2.add_zip                     AS billto_add_zip,
    a2.add_country                 AS billto_add_country,
    a2.add_phone                   AS billto_add_phone,
    a2.add_email                   AS billto_add_email,
    a2.add_company_name            AS billto_add_company_name,
    ord_shipts+17 hours +0 minutes AS ord_shiptsAdjusted,
    shp_trackingnum,
    (
        SELECT
            shm_name
        FROM
            ei.shipmethod
        WHERE
        	-- （子查询中没有 ei.OrderShipment 的，但是，我们依然可以引用shp_shm_id）
        	-- 这是因为： 子查询 可以引用 FROM 子句 的 ei.OrderShipment
            shm_id=shp_shm_id
    ) AS shm_code,
    csku_location,
    csku_binlocation,
    ori_csn_id
FROM
    ei.OrderShipment,
    ei.OrderItem,
    ei.CatalogSku,
    ei.Orders,
    ei.VenueSites,
    ei.VenCustomer,
    ei.VenueCustAddress v1,
    ei.AddressDetails a1,
    ei.VenueCustAddress v2,
    ei.AddressDetails a2
WHERE
    shp_id=ori_shp_id
AND ord_vns_id=vns_id
AND ori_csku_id=csku_id
AND ori_ord_id=ord_id
AND ord_vec_id=vec_id
AND ord_vca_id=v1.vca_id
AND v1.vca_add_nbr=a1.add_nbr
AND ord_billto_vca_id=v2.vca_id
AND v2.vca_add_nbr=a2.add_nbr
AND ord_id = 35330;
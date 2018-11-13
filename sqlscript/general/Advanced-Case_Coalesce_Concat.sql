-- 第一种  CASE语句
SELECT
    CASE SI_CSKU_ID --1. CASE 后面有字段名
        WHEN 99550 THEN 'Marvin 99550'
        WHEN 99560 THEN 'Marvin 99560'
        --2. CASE 不能再用判断了，这句是错误的。（难道只能相等了？）
        WHEN (SI_CSKU_ID<99570) THEN 'Marvin 99570'
        ELSE 'DO NOT TELL YOU'
    END AS String_CSKU_ID
FROM EI.STOREITEM WHERE SI_CSKU_ID>99540

-- Case, Coalesce, ||连接符
SELECT ord_id,
		ord_createts,
		-- 1. 每一个 WHEN... ... THEN 构成一个条件
		-- 2. 每一个 WHEN... ... THEN（包括ELSE） 后面 都没有   逗号（都没有 逗号 哦，亲）
		CASE WHEN ord_state = 'N' THEN 'Created (Not Paid For)'
		     WHEN ord_state = 'A' THEN 'Created (Payment Auth)'
             WHEN ord_state = 'C' THEN 'Paid For (Not Printed)'
             WHEN ord_state = 'P' THEN 'Printed'
             WHEN ord_state = 'E' THEN 'Exported'
             WHEN ord_state = 'S' THEN 'Shipped'
             WHEN ord_state = 'C' THEN 'Canceled'
             WHEN ord_state = 'W' THEN 'Waiting For Payment'
             WHEN ord_state = 'F' THEN 'Fulfilled'
        -- 3. 总有个 ELSE
             ELSE ''
        -- 4. 最后总以 END 结束
          END AS ord_state,
		vns_currency,
		ord_total,
	  	ord_delta_prodamt + ord_prodamt as ord_prodamt,
	  	ord_taxamt + ord_delta_taxamt as ord_taxamt,
	   	ord_shipamt + ord_delta_shipamt as ord_shipamt,
	   	ord_insurance + ord_delta_insurance as ord_insurance,
	   	ord_discount,
		ord_vat_amt,
		ord_vat_ship,
		CASE 
                    WHEN ord_cnv_total IS NULL THEN 'N/A'
        -- 总有个 ELSE
                    ELSE char(ord_ex_rate)
        -- 最后总以 END 结束
                    END AS ord_ex_rates,
		CASE 
                    WHEN ord_cnv_total IS NULL THEN 'N/A' 
                    ELSE char(ord_cnv_total) 
                    END AS ord_cnv_total,
		CASE 
                    WHEN ord_cnv_total IS NULL THEN 'N/A' 
                    ELSE cnv_curr 
                    END AS ord_cnv_curr,
	  	ord_printts,
	   	ord_exportts,
	   	ord_shipts,
	 	vec_id, 
	   	vec_name,
	   	add_email,
	   	add_fname,
	   	add_lname,
	   	add_address1,
	   	add_address2,
	   	add_city,
	   	add_state,
	   	add_zip,
	   	add_country,
	    CASE 
                WHEN ord_paymethod ='CA' THEN 'Cash' 
                WHEN ord_paymethod ='CH' THEN 'Check'
                WHEN ord_paymethod ='MO' THEN 'Money Order'
                WHEN ord_paymethod ='PP' THEN 'PayPal'
                WHEN ord_paymethod ='DD' THEN 'DirectDebit'
                WHEN ord_paymethod ='CC' THEN 'Credit Card'
                ELSE 'N/A'
                END as ord_paymethod,
            CASE WHEN pft_cctype = 'bd' THEN 'Bank Card'
                 WHEN pft_cctype = 'dn' THEN 'Diner/EnRoute'
                 WHEN pft_cctype = 'ae' THEN 'American Express'
                 WHEN pft_cctype = 'dv' THEN 'Discover'
                 WHEN pft_cctype = 'jb' THEN 'JCB'
                 WHEN pft_cctype = 'mc' THEN 'MasterCard'
                 WHEN pft_cctype = 'Other' THEN 'Other'
                 WHEN pft_cctype = 'sw' THEN 'Maestro Debit'
                 WHEN pft_cctype = 'um' THEN 'UM'
                 WHEN pft_cctype = 'vi' THEN 'Visa'
                 ELSE ''
                 END as ccType,
        -- ||, DB2中，字符串连接是 ||
		'cc: ' || SUBSTR(pft_ccnum, 1,4) ||' XXXX XXXX '||SUBSTR(pft_ccnum,13,4) || ' exp:' || SUBSTR(pft_ccexp, 1,2) ||'-'|| SUBSTR(pft_ccexp,3,2) as ccList,
	   	pft_amount,
	    -- Coalesce, 使用第一个    不是NULL  的表达式的值
	   	coalesce(pft_authcode,'') as pft_authcode,
		(SELECT (case when pft_txntype = 'CAPONLY' then pft_fullpnref else null end )as msg_cap FROM ei.payflowtxn  WHERE PFT_RESULT = '0'  
			AND pft_ord_id= ord_id order by pft_createts desc FETCH FIRST 1 ROWS ONLY) as msg_cap ,
	   	(SELECT (case when pft_txntype = 'CREDIT' then pft_fullpnref else null end )as msg_credit FROM ei.payflowtxn WHERE PFT_RESULT = '0' 
   	        AND pft_ord_id = ord_id order by pft_createts desc FETCH FIRST 1 ROWS ONLY) as msg_credit
   FROM ei.orders left join 
   		(select pft_ord_id, pft_cctype, pft_ccnum, pft_ccexp, pft_amount, pft_authcode 
   		   from ei.payflowtxn 
   		  where pft_id in (select max(pft_id) 
   		                     from ei.payflowtxn, ei.orders
   		                    where pft_ord_id = ord_id
   		                      and ord_ctl_nbr = #{catid}
   		                      and #{W2} #{W1}
   		                      and pft_ctl_id = #{catid}
   		                      and pft_txntype in ('SALE','AUTHONLY','CAPONLY') 
   		                      and pft_result = '0'
   		                    group by pft_ord_id)
   		) as payflow on ord_id = payflow.pft_ord_id,
   	    ei.venuesites,
   	    ei.addressdetails,
   	    ei.vencustomer,
   	    ei.venuecustaddress,
   	    (SELECT CO_VALUE as cnv_curr FROM EI.CTLOPTIONS WHERE CO_NAME = 'currency' and co_nbr = (SELECT CASE WHEN (SELECT COUNT(*) FROM EI.CTLOPTIONS WHERE co_name = 'currency' and co_nbr = #{catid}) = 0 THEN 141 ELSE #{catid} END FROM EI.CTLOPTIONS FETCH FIRST 1 ROWS ONLY)) AS CtlCurrency
  WHERE ord_vca_id = vca_id
    and ord_vns_id = vns_id
    and vca_vec_id = vec_id
    and add_nbr = vca_add_nbr
    and ord_ctl_nbr = #{catid}
    and #{W2} #{W1};


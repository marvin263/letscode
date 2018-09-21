SELECT
    *
FROM
    (
        SELECT
            *
        FROM
            (
                SELECT
                    *
                FROM
                    (
                        SELECT
                            *
                        FROM
                            (
                                SELECT
                                    *
                                FROM
                                    (
                                        SELECT
                                            *
                                        FROM
                                            (
                                                SELECT
                                                    *
                                                FROM
                                                    (
                                                        SELECT
                                                            *
                                                        FROM
                                                            (
                                                                SELECT
                                                                    *
                                                                FROM
                                                                    (
                                                                        SELECT
                                                                            VNS_CURR_SIGN AS
                                                                            cur_sign,
                                                                            ppn_curr AS
                                                                            pay_currency,
                                                                            ''                 AS ccissue,
                                                                            PPN_TXNID          AS id,
                                                                            'ppn'              AS src,
                                                                            CHAR(ppn_createts) AS
                                                                            createts,
                                                                            CHAR(ppn_createts) AS
                                                                            created,
                                                                            'PayPal'            AS type,
                                                                            ppn_amount          AS amount,
                                                                            CHAR(ppn_txnid, 30) AS
                                                                            refnum,
                                                                            ppn_paystatus AS
                                                                            paystatus,
                                                                            'N/A' AS ccnumber,
                                                                            'N/A' AS ccexp,
                                                                            CHAR(ppn_processstatus,
                                                                            200)          AS msg,
                                                                            PPN_PAYSTATUS AS result
                                                                            ,
                                                                            ppn_paystatus AS
                                                                            txntype,
                                                                            'CUST' AS createdby
                                                                        FROM
                                                                            ei.paypalnotification,
                                                                            ei.Orders,
                                                                            ei.VenueSites
                                                                        WHERE
                                                                            vns_id = ord_vns_id
                                                                        AND ord_id = ppn_ord_id
                                                                        AND ppn_ord_id=16018386
                                                                        AND ppn_processstatus IN (
                                                                            'processed_notif',
                                                                            'valid_notif',
                                                                            'new_notif',
                                                                            'man_verify_notif')
                                                                        AND ppn_amount>0
                                                                    ) AS t1
                                                                UNION ALL
                                                                    (
                                                                        SELECT
                                                                            VNS_CURR_SIGN AS
                                                                            cur_sign,
                                                                            pft_cur AS pay_currency
                                                                            ,
                                                                            pft_ccissue        AS ccissue,
                                                                            CHAR(pft_id)       AS id,
                                                                            'pft'              AS src,
                                                                            CHAR(pft_createts) AS
                                                                            createts,
                                                                            CHAR(pft_createts) AS
                                                                            created,
                                                                            'Credit Card' AS type,
                                                                            pft_amount    AS amount
                                                                            ,
                                                                            'pft ' || CHAR(pft_id)
                                                                                          AS refnum,
                                                                            ofp_paystatus AS
                                                                            paystatus,
                                                                            SUBSTR(pft_ccnum, 1,4)
                                                                            ||' XXXX XXXX '||SUBSTR
                                                                            (pft_ccnum,13,4) AS
                                                                            ccnumber,
                                                                            SUBSTR(pft_ccexp, 1,2)
                                                                            ||'-'|| SUBSTR(
                                                                            pft_ccexp,3,2) AS ccexp
                                                                            ,
                                                                            CHAR(pft_respmsg ||
                                                                            ' / '||pft_authcode,
                                                                            200)        AS msg,
                                                                            PFT_RESULT  AS result,
                                                                            pft_txntype AS txntype,
                                                                            CASE
                                                                                WHEN
                                                                                    ofp_created_custfl
                                                                                    = 'N'
                                                                                THEN
                                                                                    (
                                                                                        SELECT
                                                                                            usr_nick
                                                                                        FROM
                                                                                            ei.users
                                                                                        WHERE
                                                                                            usr_nbr
                                                                                            =
                                                                                            ofp_createdby
                                                                                    )
                                                                                ELSE 'CUST'
                                                                            END AS createdby
                                                                        FROM
                                                                            ei.Orders,
                                                                            ei.VenueSites,
                                                                            ei.payflowtxn
                                                                        LEFT JOIN ei.offlinepayment
                                                                        ON
                                                                            CHAR(pft_id)=ofp_refnum
                                                                        AND ofp_ord_id=pft_ord_id
                                                                        WHERE
                                                                            vns_id = ord_vns_id
                                                                        AND ord_id = pft_ord_id
                                                                        AND pft_ord_id=16018386
                                                                        AND pft_txntype IN ('SALE',
                                                                            'AUTHONLY', 'CAPONLY')
                                                                    )
                                                            ) AS t2
                                                        UNION ALL
                                                            (
                                                                SELECT
                                                                    VNS_CURR_SIGN      AS cur_sign,
                                                                    vns_currency       AS pay_currency,
                                                                    ''                 AS ccissue,
                                                                    CHAR(ofp_id)       AS id,
                                                                    'ofp'              AS src,
                                                                    CHAR(ofp_createts) AS createts,
                                                                    CHAR(ofp_createts) AS created,
                                                                    ofp_type           AS type,
                                                                    ofp_amount         AS amount,
                                                                    ofp_refnum         AS refnum,
                                                                    ofp_paystatus      AS paystatus
                                                                    ,
                                                                    'N/A'         AS ccnumber,
                                                                    'N/A'         AS ccexp,
                                                                    ofp_paystatus AS msg,
                                                                    ''            AS result,
                                                                    'SALE'        AS txntype,
                                                                    CASE
                                                                        WHEN ofp_created_custfl =
                                                                            'N'
                                                                        THEN
                                                                            (
                                                                                SELECT
                                                                                    usr_nick
                                                                                FROM
                                                                                    ei.users
                                                                                WHERE
                                                                                    usr_nbr=
                                                                                    ofp_createdby
                                                                            )
                                                                        ELSE 'CUST'
                                                                    END AS createdby
                                                                FROM
                                                                    ei.offlinepayment,
                                                                    ei.Orders,
                                                                    ei.VenueSites
                                                                WHERE
                                                                    vns_id = ord_vns_id
                                                                AND ord_id = ofp_ord_id
                                                                AND ofp_ord_id=16018386
                                                                AND ofp_type<>'Credit Card'
                                                            )
                                                    ) AS t3
                                                UNION ALL
                                                    (
                                                        SELECT
                                                            vns_curr_sign      AS cur_sign,
                                                            vns_currency       AS pay_currency,
                                                            ''                 AS ccissue,
                                                            CHAR(edt_gutid)    AS id,
                                                            'edt'              AS src,
                                                            CHAR(edt_createts) AS createts,
                                                            CHAR(edt_createts) AS created,
                                                            'easyDebit'        AS type,
                                                            edt_amount         AS amount,
                                                            edt_gutid          AS refnum,
                                                            edt_status         AS paystatus,
                                                            'N/A'              AS ccnumber,
                                                            'N/A'              AS ccexp,
                                                            edt_status_msg     AS msg,
                                                            ''                 AS result,
                                                            edt_paymethod      AS txntype,
                                                            'CUST'             AS createdby
                                                        FROM
                                                            ei.easydebittxn,
                                                            ei.orders,
                                                            ei.venuesites
                                                        WHERE
                                                            vns_id=ord_vns_id
                                                        AND ord_id=edt_ord_id
                                                        AND edt_ord_id=16018386
                                                    )
                                            ) AS t4
                                        UNION ALL
                                            (
                                                SELECT
                                                    vns_curr_sign      AS cur_sign,
                                                    vns_currency       AS pay_currency,
                                                    ''                 AS ccissue,
                                                    CHAR(pst_id)       AS id,
                                                    'pst'              AS src,
                                                    CHAR(pst_createts) AS createts,
                                                    CHAR(pst_createts) AS created,
                                                    'Paysignet'        AS type,
                                                    pst_amount         AS amount,
                                                    CHAR(pst_id)       AS refnum,
                                                    pst_status         AS paystatus,
                                                    'N/A'              AS ccnumber,
                                                    'N/A'              AS ccexp,
                                                    pst_pay_msg        AS msg,
                                                    pst_status         AS result,
                                                    'SALE'             AS txntype,
                                                    'CUST'             AS createdby
                                                FROM
                                                    ei.paysignettxn,
                                                    ei.orders,
                                                    ei.venuesites
                                                WHERE
                                                    vns_id=ord_vns_id
                                                AND ord_id=pst_ord_id
                                                AND pst_ord_id=16018386
                                            )
                                    ) AS t5
                                UNION ALL
                                    (
                                        SELECT
                                            vns_curr_sign     AS cur_sign,
                                            vns_currency      AS pay_currency,
                                            ''                AS ccissue,
                                            CHAR(wc_guwid)    AS id,
                                            'wc'              AS src,
                                            CHAR(wc_createts) AS createts,
                                            CHAR(wc_createts) AS created,
                                            'Wirecard'        AS type,
                                            wc_amount         AS amount,
                                            wc_guwid          AS refnum,
                                            wc_status         AS paystatus,
                                            'N/A'             AS ccnumber,
                                            'N/A'             AS ccexp,
                                            wc_status_msg     AS msg,
                                            ''                AS result,
                                            wc_paymethod      AS txntype,
                                            'CUST'            AS createdby
                                        FROM
                                            ei.wirecardtxn,
                                            ei.orders,
                                            ei.venuesites
                                        WHERE
                                            vns_id=ord_vns_id
                                        AND ord_id=wc_ord_id
                                        AND wc_ord_id=16018386
                                    )
                            ) AS t6
                        UNION ALL
                            (
                                SELECT
                                    vns_curr_sign      AS cur_sign,
                                    vns_currency       AS pay_currency,
                                    ''                 AS ccissue,
                                    CHAR(bdc_id)       AS id,
                                    'bd'               AS src,
                                    CHAR(bdc_createts) AS createts,
                                    CHAR(bdc_createts) AS created,
                                    'Bankdatencheck'   AS type,
                                    bdc_amount         AS amount,
                                    CHAR(bdc_ord_id)   AS refnum,
                                    bdc_status         AS paystatus,
                                    'N/A'              AS ccnumber,
                                    'N/A'              AS ccexp,
                                    'Inh.:' || bdc_accountholder || '<br>KNr.:' ||
                                    bdc_accountnumber || '<br>BLZ.:' || bdc_bankcode AS msg,
                                    ''                                               AS result,
                                    'VALIDATION'                                     AS txntype,
                                    'CUST'                                           AS createdby
                                FROM
                                    ei.bankdatenchecktxn,
                                    ei.orders,
                                    ei.venuesites
                                WHERE
                                    vns_id=ord_vns_id
                                AND ord_id=bdc_ord_id
                                AND bdc_ord_id=16018386
                            )
                    ) AS t7
                UNION ALL
                    (
                        SELECT
                            vns_curr_sign      AS cur_sign,
                            vns_currency       AS pay_currency,
                            ''                 AS ccissue,
                            CHAR(tk_id)        AS id,
                            'tk'               AS src,
                            CHAR(tk_createts)  AS createts,
                            CHAR(tk_createts)  AS created,
                            'TopKredit'        AS type,
                            tk_amount          AS amount,
                            CHAR(tk_ord_id)    AS refnum,
                            tk_status          AS paystatus,
                            'N/A'              AS ccnumber,
                            'N/A'              AS ccexp,
                            tk_status_full_msg AS msg,
                            '0'                AS result,
                            CASE tk_status
                                WHEN '5'
                                THEN 'SALE'
                                ELSE 'WAITING'
                            END    AS txntype,
                            'CUST' AS createdby
                        FROM
                            ei.topkredittxn,
                            ei.orders,
                            ei.venuesites
                        WHERE
                            vns_id=ord_vns_id
                        AND ord_id=tk_ord_id
                        AND tk_ord_id=16018386
                    )
            ) AS t8
        UNION ALL
            (
                SELECT
                    vns_curr_sign       AS cur_sign,
                    vns_currency        AS pay_currency,
                    ''                  AS ccissue,
                    CHAR(cpt_kontaktid) AS id,
                    'cp'                AS src,
                    CHAR(cpt_createts)  AS createts,
                    CHAR(cpt_createts)  AS created,
                    'ClickPay'          AS type,
                    cpt_amount          AS amount,
                    cpt_kontaktid       AS refnum,
                    cpt_status          AS paystatus,
                    'N/A'               AS ccnumber,
                    'N/A'               AS ccexp,
                    cpt_status_msg      AS msg,
                    cpt_status          AS result,
                    cpt_paymethod       AS txntype,
                    'CUST'              AS createdby
                FROM
                    ei.clickpaytxn,
                    ei.orders,
                    ei.venuesites
                WHERE
                    vns_id=ord_vns_id
                AND ord_id=cpt_ord_id
                AND cpt_ord_id=16018386
            )
    ) AS t9
UNION ALL
    (
        SELECT
            vns_curr_sign        AS cur_sign,
            vns_currency         AS pay_currency,
            ''                   AS ccissue,
            CHAR(cft_paid)       AS id,
            'cf'                 AS src,
            CHAR(cft_createts)   AS createts,
            CHAR(cft_createts)   AS created,
            'Citibank Financing' AS type,
            cft_amount           AS amount,
            CHAR(cft_ord_id)     AS refnum,
            cft_status           AS paystatus,
            'N/A'                AS ccnumber,
            'N/A'                AS ccexp,
            cft_status_msg       AS msg,
            '0'                  AS result,
            CASE
                WHEN ord_state IN ('N','A' ,'W')
                THEN 'WAIT'
                ELSE 'SALE'
            END    AS txntype,
            'CUST' AS createdby
        FROM
            ei.citibankfinancingtxn,
            ei.orders,
            ei.venuesites
        WHERE
            vns_id=ord_vns_id
        AND ord_id=cft_ord_id
        AND cft_ord_id=16018386
    )
ORDER BY
    createts DESC;
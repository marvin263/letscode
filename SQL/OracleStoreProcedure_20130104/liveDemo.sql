 declare
v_retcode    varchar2(200);
v_retinfo    varchar2(200);
v_date       varchar2(8);
begin
for v_day in '20110301'..'20110331' loop
v_date:=to_char(v_day);
 p_ca_activity_monitor_92981(v_date,v_retcode,v_retinfo);
end loop;

for v_day in '20110401'..'20110424' loop
v_date:=to_char(v_day);
 p_ca_activity_monitor_92981(v_date,v_retcode,v_retinfo);
end loop;
end;

---------------------------------------


declare
v_retcode    varchar2(200);
v_retinfo    varchar2(200);
v_date       varchar2(8);
begin
  for v_day in '20110101'..'20110131' loop
v_date:=to_char(v_day);
 p_ca_activity_monitor_92921(v_date,v_retcode,v_retinfo);
end loop;

for v_day in '20110201'..'20110228' loop
v_date:=to_char(v_day);
 p_ca_activity_monitor_92921(v_date,v_retcode,v_retinfo);
end loop;
for v_day in '20110301'..'20110331' loop
v_date:=to_char(v_day);
 p_ca_activity_monitor_92921(v_date,v_retcode,v_retinfo);
end loop;

for v_day in '20110401'..'20110424' loop
v_date:=to_char(v_day);
 p_ca_activity_monitor_92921(v_date,v_retcode,v_retinfo);
end loop;
end;

-----------------




declare
v_retcode    varchar2(200);
v_retinfo    varchar2(200);
v_date       varchar2(8);
begin
for v_day in '20110401'..'20110424' loop
v_date:=to_char(v_day);
 P_CA_ACTIVITY_MONITOR_92681(v_date,v_retcode,v_retinfo);
end loop;

for v_day in '20110401'..'20110424' loop
v_date:=to_char(v_day);
 P_CA_ACT_MONITOR_ITEMS_WX92681(v_date,v_retcode,v_retinfo);
end loop;
end;




CREATE OR REPLACE PROCEDURE P_CA_ACTIVITY_ANA_92761(V_MONTH   VARCHAR2,
                                                    V_RETCODE OUT VARCHAR2,
                                                    V_RETINFO OUT VARCHAR2) IS
  /*****************************************************************
    *名称 --%NAME:5101－2季度连锁渠道推进方案
    *功能描述 --%COMMENT:
    *执行周期 --%PERIOD:月
    *参数 --%PARAM:V_RETCODE返回信息（SUCCESS/WAIT/FAIL）
    *参数 --%PARAM:V_RETINFO返回错误信息
    *创建人 --%
    *创建时间 --%CREATED_TIME:20110422
    *备注 --%REMARK:
    *修改记录 --%MODIFY:
    *来源表 --%CA_USER_DEV_INFO_M
    *目标表 --%TO:CA_ACTIVITY_ANA
  *******************************************************************/
  V_STARTDATE DATE;
  V_PROCNAME  VARCHAR2(40);
  V_PKG       VARCHAR2(40);
  V_CNT       NUMBER;
  V_ROWS      NUMBER;

BEGIN

  V_STARTDATE := SYSDATE;
  V_PROCNAME  := 'P_CA_ACTIVITY_ANA_92761';
  V_PKG       := 'CA';

  --日志部分
  DW.P_INSERT_LOG(V_MONTH, V_PKG, V_PROCNAME, '12', V_STARTDATE);

  IF 1 = 1 THEN
    DELETE FROM CA_ACTIVITY_ANA T
     WHERE T.ACCT_MONTH = V_MONTH
       AND T.ACTIVITY_ID = '92761';
    COMMIT;

    INSERT /* + APPEND */
    INTO CA_ACTIVITY_ANA
      select /*+PARALLEL(C,8)*/
       V_MONTH,
       AREA_NO,
       CITY_NO,
       ACTIVITY_ID,
       DEPT_DESC,
       AREA_NO,
       DINNER_ID,
       DEV_USER_NUM,
       INCOME_USER_NUM,
       REAL_INCOME,
       ACTIVE_USER_NUM,
       CALL_ACTIVITY_USER_NUM,
       /* case
                when INCOME_USER_NUM > 0 and
                     (trunc(MONTHS_BETWEEN(TO_DATE(V_MONTH, 'yyyy-mm'),
                                           TO_DATE(dev_month, 'yyyy-mm'))) + 1) > 0 then
                 REAL_INCOME / INCOME_USER_NUM /
                 (trunc(MONTHS_BETWEEN(TO_DATE(V_MONTH, 'yyyy-mm'),
                                       TO_DATE(dev_month, 'yyyy-mm'))) + 1)
                else
                 0
              end ARPU,
              case
                when CALL_NUMS > 0 and
                     (trunc(MONTHS_BETWEEN(TO_DATE(V_MONTH, 'yyyy-mm'),
                                           TO_DATE(dev_month, 'yyyy-mm'))) + 1) > 0 then
                 CALL_MOU / CALL_NUMS /
                 (trunc(MONTHS_BETWEEN(TO_DATE(V_MONTH, 'yyyy-mm'),
                                       TO_DATE(dev_month, 'yyyy-mm'))) + 1)
                else
                 0
              end MOU,*/
       NULL ARPU,
       NULL MOU,
       ONNET_USER_NUM,
       SPEED_USER_NUM,
       CALL_NUMS

        FROM (select AREA_NO,
                     NULL CITY_NO,
                     '92761' ACTIVITY_ID,
                     '嘉定分公司' DEPT_DESC,
                     A.CHANNEL_NO CHANNEL_ID,
                     A.USER_DINNER DINNER_ID,
                     COUNT(DISTINCT CASE
                             WHEN A.dev_month = V_MONTH THEN
                              A.USER_NO
                             ELSE
                              NULL
                           END) DEV_USER_NUM,
                     COUNT(CASE
                             WHEN B.TOTAL_FEE > 0 THEN
                              B.USER_NO
                             ELSE
                              NULL
                           end) INCOME_USER_NUM,
                     SUM(CALL_NUMS) CALL_NUMS,
                     A.dev_month,
                     SUM(B.TOTAL_FEE) REAL_INCOME,
                     SUM(A.CALL_DURATION) CALL_MOU,
                     NULL ONNET_USER_NUM,
                     NULL SPEED_USER_NUM,
                     count(case
                           --        when CALL_NUMS > 0 and SMS_NUMS > 0 and GPRS_NUMS > 0 then
                             when is_active = 1 then
                              a.user_no
                             else
                              null
                           end) ACTIVE_USER_NUM,

                     count(case
                             when CALL_NUMS > 0 then
                              a.user_no
                             else
                              null
                           end) CALL_ACTIVITY_USER_NUM
                FROM (SELECT T.*
                        FROM CA.CA_USER_DEV_INFO_M T
                       WHERE T.ACCT_MONTH = V_MONTH
                         AND T.IS_3G = '1'
                         AND T.PAY_MODE = '01'
                         and to_date(dev_date,'yyyymmdd' )>=to_date('20110401','yyyy-mm-dd')
                         and to_date(dev_date,'yyyymmdd' )<=to_date('20110701','yyyy-mm-dd')
                         and t.area_no = 'A08') A,
                     (select T.USER_NO, SUM(T.TOTAL_FEE) TOTAL_FEE
                        from DW.DW_V_USER_CHARGE_SEC_HOR T
                       WHERE T.ACCT_MONTH = V_MONTH
                       GROUP BY T.USER_NO) B

               WHERE A.USER_NO = B.USER_NO(+)

               GROUP BY A.AREA_NO, A.CHANNEL_NO, A.USER_DINNER, A.dev_month);

    V_ROWS := SQL%ROWCOUNT;

    COMMIT;

    EXECUTE IMMEDIATE 'ALTER INDEX index_ACTIVITY_ID REBUILD';
    --更新日志
    V_RETCODE := 'SUCCESS';
    DW.P_UPDATE_LOG(V_MONTH,
                    V_PKG,
                    V_PROCNAME,
                    '结束',
                    V_RETCODE,
                    SYSDATE,
                    V_ROWS);

  ELSE
    V_RETCODE := 'WAIT';
    DW.P_UPDATE_LOG(V_MONTH,
                    V_PKG,
                    V_PROCNAME,
                    '等待',
                    V_RETCODE,
                    SYSDATE,
                    V_ROWS);
  END IF;
EXCEPTION
  WHEN OTHERS THEN
    --更新操作日志
    V_RETCODE := 'FAIL';
    V_RETINFO := SQLERRM;
    DW.P_UPDATE_LOG(V_MONTH,
                    V_PKG,
                    V_PROCNAME,
                    V_RETINFO,
                    V_RETCODE,
                    SYSDATE,
                    V_ROWS);

END;

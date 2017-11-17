DROP TABLE EI.ORDERS;
CREATE TABLE EI.ORDERS(
	ORD_ID INTEGER,
	ORD_STATE VARCHAR (60),
	ORD_BUYER VARCHAR (100),
	ORD_BUYER_IP VARCHAR (60),
	ORD_DESC VARCHAR (512)
);
-- Just insert sample datas
INSERT INTO EI.ORDERS(ORD_ID, ORD_STATE, ORD_BUYER, ORD_BUYER_IP, ORD_DESC)
		VALUES(1, 'Waiting for Payment', 'Ming', '199.168.0.1', 'Ipad2 is excellent');
INSERT INTO EI.ORDERS(ORD_ID, ORD_STATE, ORD_BUYER, ORD_BUYER_IP, ORD_DESC)
		VALUES(2, 'Exported', 'Daniel', '199.168.0.2', 'Badminton');
INSERT INTO EI.ORDERS(ORD_ID, ORD_STATE, ORD_BUYER, ORD_BUYER_IP, ORD_DESC)
		VALUES(3, 'Paid For (Not Printed)', 'Chilli', '199.168.0.3', 'Galaxy Note is so cool');
INSERT INTO EI.ORDERS(ORD_ID, ORD_STATE, ORD_BUYER, ORD_BUYER_IP, ORD_DESC)
		VALUES(25, 'Created (Payment Auth)', 'Marvin', '199.168.0.100', 'iMac, I finally bought it');
		
		
--  UPDATE/DELETE 实在是简单到不行啊
--  注意：如果没有 WHERE 字句，则 update/delete 表中 所有元组
DELETE FROM EI.ORDERS WHERE ORD_ID = 26;  --这两个狗屁字 (DELETE FROM)中间不会出现任何东西 
UPDATE
    EI.ORDERS
SET  -- 1.SET关键字只出现一次    2.多个 column 用 , 分割
    ORD_STATE = 'Paid',
    ORD_BUYER = 'SusanJia',
    ORD_BUYER_IP = '192.168.0.101',
    ORD_DESC = 'Marvin modified Susan''s order description'
WHERE
    ORD_ID = 25;
    
    
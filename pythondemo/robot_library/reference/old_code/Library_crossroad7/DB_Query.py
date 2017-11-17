__author__ = 'g705360'

import cx_Oracle
from robot.api import logger

class DB_Query():

    db_conn = None
    db_cursor = None

    def conn(self, db_name='crossroad7'):


        if db_name=='crossroad7':
            self.db_conn = cx_Oracle.connect('xroads_app', 'XRDS4T!4app', 'hw-dbt03.syniverse.com:1521/xrds4t')
            # db2 = cx_Oracle.connect('xroads_app/XRDS4T!4app@hw-dbt03.syniverse.com:1521/xrds4t')
            # dsn = cx_Oracle.makedsn('hw-dbt03.syniverse.com', 1521, 'xrds4t')
            #print dsn

        # cursor = db1.cursor()
        # cursor.execute('select * from RESOURCE_TYPE_CODE')
        # result = cursor.fetchall()
        # for row in result:
        #     print row
        #
        # cursor.close()
        # db1.close()

    def close(self):
        self.db_cursor.close()
        self.db_conn.close()


    def find_implied_permission(self, company_permission_id=None):
        pass

    def find_presentation_label(self, resource_id):
        query = "select PRSNTTN_LABEL from presentation where PRSNTTN_ID in (" \
                "select PRSNTTN_ID from resourc where rsrc_id in ('" \
                + resource_id +\
                "'));"
        self.db_cursor.execute(query)
        result = self.db_cursor.fetchall()
        if len(result)==0:
            logger.warn("Sorry, no result")
        else:
            for cell in result:
                return cell





    def get_user_password(self, user_login_id):
        if self.db_conn == None:
            logger.warn("Please use conn to initialize the connection first")
            return False
        self.db_cursor = self.db_conn.cursor()
        query = ''
        self.cursor.execute()








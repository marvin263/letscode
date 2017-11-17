__author__ = 'g705360'
# coding:utf-8
import random

from DatabaseLibrary import DatabaseLibrary
from robot.api import logger


class Xroads6DBUtil():
    conn = None
    QUERY_PERM_GROUP_BY_PERM_ID = 'SELECT  ' \
                                  '  R.RSRC_ID AS PERM_ID, ' \
                                  '  P.PRSNTTN_LABEL AS PERM_LABEL, ' \
                                  '  R2.RSRC_ID AS PERM_GROUP_ID, ' \
                                  '  P2.PRSNTTN_LABEL AS PERM_GROIUP_LABEL ' \
                                  'FROM RESOURC R  ' \
                                  '  INNER JOIN PRESENTATION P ON P.PRSNTTN_ID = R.PRSNTTN_ID ' \
                                  '  INNER JOIN RSRC_TO_RSRC_REL RREL ON RREL.RSRC_ID1 = R.RSRC_ID ' \
                                  ' AND UPPER(RREL.REL_TYP_CD) = UPPER(\'PermissionGroup\') ' \
                                  '  INNER JOIN RESOURC R2 ON R2.RSRC_ID = RREL.RSRC_ID2 ' \
                                  '  INNER JOIN PRESENTATION P2 ON P2.PRSNTTN_ID = R2.PRSNTTN_ID ' \
                                  'WHERE  ( R.RSRC_ID IN ( %s ) OR P.PRSNTTN_LABEL IN ( %s) ) ' \
                                  ' AND R.RSRC_TYP_CD = \'2\' AND R2.RSRC_TYP_CD = \'3\' '

    def set_connection_info(self, user, password, connection_url):
        self.conn = DatabaseLibrary()
        self.conn.connect_to_database_using_custom_params('cx_Oracle',
                                                          "'%s','%s','%s'" % (user, password, connection_url))

    def get_permission_group(self, perm_id_label_list):
        """

        :param perm_id_label_list:    the list of permissions to query
        :return: a map of from permission id to tuple of (permission_lable, permission_group_id, permission_group_label)
        """
        perm_id_str = ', '.join([("'%s'" % id_label) for id_label in perm_id_label_list])
        # for perm_id in perm_id_label_list:
        #    perm_id_str += "'%s', " % perm_id
        #perm_id_str = perm_id_str[:len(perm_id_str) - 2]
        logger.info('Permission detail query: \n' + self.QUERY_PERM_GROUP_BY_PERM_ID % ( perm_id_str, perm_id_str))
        perm_to_group_map_rs = self.conn.query(self.QUERY_PERM_GROUP_BY_PERM_ID % (perm_id_str, perm_id_str))
        result = {}
        for row in perm_to_group_map_rs:
            logger.debug(row)
            result[row[0]] = row[1:]
        logger.info('Permission detail query result: ')
        logger.info(result)
        return result

    def get_permission_group_by_label(self, perm_grp_label):
        query = "SELECT R.RSRC_ID FROM RESOURC R INNER JOIN PRESENTATION P ON P.PRSNTTN_ID = R.PRSNTTN_ID " \
                "WHERE P.PRSNTTN_LABEL = '%s' AND R.RSRC_TYP_CD = '3'"
        perm_grp_rs = self.conn.query(query)
        if 0 == len(perm_grp_rs):
            return None
        else:
            return (perm_grp_rs[0][0], perm_grp_label)

    def get_permission_group_by_id(self, perm_grp_id):
        query = "SELECT P.PRSNTTN_LABEL FROM RESOURC R INNER JOIN PRESENTATION P ON P.PRSNTTN_ID = R.PRSNTTN_ID " \
                "WHERE R.RSRC_ID = '%s' AND R.RSRC_TYP_CD = '3'"
        perm_grp_rs = self.conn.query(query)
        if 0 == len(perm_grp_rs):
            return None
        else:
            return (perm_grp_id, perm_grp_rs[0][0])

    def company_should_have_resource_in_xroads6_db(self, company_id, resource_id, resource_type):
        """
        Verify that the specified company have resource in the company resource table
        :param company_id:
        :param resource_id:
        :param resource_type:
        :return:
        """
        query = "SELECT * FROM XROADS_OWNER.COMPANY_RESOURCE WHERE CMPNY_ID = '%s' " \
                " AND RSRC_ID = '%s' AND RSRC_TYP_CD = '%s'" % (
                    company_id, resource_id, resource_type)
        self.conn.check_if_exists_in_database(query)

    def xroads6_db_should_have_resource(self, resource_id, resource_type):
        """
        Verify that the specified company have resource in the company resource table
        :param resource_id:
        :param resource_type:
        :return:
        """
        query = "SELECT * FROM XROADS_OWNER.RESOURC R WHERE R.RSRC_ID = '%s' AND R.RSRC_TYP_CD = '%s' " % (
            resource_id, resource_type)
        self.conn.check_if_exists_in_database(query)

    def get_usable_alternate_id(self):
        prefix = 'alt_id_'
        random_len = 8
        for i in range(random_len):
            prefix += chr(random.randint(97, 122))
        query = "SELECT USR_ID FROM USER_XROADS UX WHERE UX.USR_ALTERNATE_ID = '%s'" % prefix
        if self.conn.row_count(query) > 0:
            return self.get_usable_alternate_id()
        return prefix

    def close_db_connection(self):
        if self.conn is not None:
            self.conn.disconnect_from_database()


if __name__ == "__main__":
    a = Xroads6DBUtil("xroads_app", "xroads", r'rhrac1scan.syniverse.com:1521/XRDSD1')
    # a.verify_resource_id_has_attributes_in_DB('ICMMS_Mobile_Data', 'ICMMS', '0010', '4316')
    # a.verify_resource_relationship('ICMMS_Mobile_Data', 'MD-1', 'Product')
    # a.verify_resource_id_has_attributes_in_DB('ICMMS_Mobile_Data_Co', 'ICMMS', '0010', '4316')
    a.get_permission_group(['AccNA_AccEU', 'ACC_ACCESS_Invoices_Co', 'SWIFT_Co'])
    a.close_db_connection()
    # b = Call_LDAP()
    # #a.login_ldap()
    # b.login_ODSEE()
    # #a.search_company('A_-50')
    # list_b = b.get_company_permissions_from_ldap('A-7')
    # b.close_conn()
    # a.compare_two_permission_list(list_a, list_b)
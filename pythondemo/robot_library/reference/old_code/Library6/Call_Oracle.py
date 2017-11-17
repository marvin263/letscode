__author__ = 'g705360'
#coding:utf-8
from DatabaseLibrary import DatabaseLibrary
from robot.api import logger
from Call_LDAP import Call_LDAP
import string

class Call_Oracle():
    conn = None

    def __init__(self):
        self.conn = DatabaseLibrary()

    def login_db(self, db_name):
        if 'test' in db_name:
            self.login_to_test_db()
        elif 'dev' in db_name:
            self.login_to_dev_db()
        else:
            raise AssertionError("No such connection DB name %s"%db_name)

    def login_to_test_db(self):
        #self.conn = DatabaseLibrary()
        #logger.info("Start to connect to Oracle...")
        self.conn.connect_to_database_using_custom_params('cx_Oracle', r"'XROADS_RO','xroads_r34d', 'rhrac1scan.syniverse.com:1521/xrds3t'")
        #logger.info("Successfully connected!")

    def login_to_dev_db(self):
        #self.conn = DatabaseLibrary()
        #logger.info("Start to connect to Oracle...")
        self.conn.connect_to_database_using_custom_params('cx_Oracle', r"'xroads_app','xroads', 'rhrac1scan.syniverse.com:1521/XRDSD1'")
        #logger.info("Successfully connected!")


    def verify_company_has_permission_in_DB(self, company_name, permission):
        query = "select * from company_resource where cmpny_id in " \
                "(select cmpny_cd from company_xroads where cmpny_name='%s') and rsrc_id= '%s' and RSRC_TYP_CD='2'"%(company_name, permission)
        logger.debug("Verify Company has Permission by:\n %s" %query)
        try:
            self.conn.check_if_exists_in_database(query)
        except AssertionError:

            raise AssertionError("Incorect, we got: %s, by query \n %s"%(self.conn.query(query), query))

        logger.info("==Correct! Company '%s' has '%s' in DB"%(company_name, permission))

    def verify_company_has_no_such_permission_in_DB(self, company_name, permission):
        query = "select * from company_resource where cmpny_id in " \
                "(select cmpny_cd from company_xroads where cmpny_name='%s') and rsrc_id= '%s' and RSRC_TYP_CD='2'"%(company_name, permission)
        logger.debug("Verify Company has no Permission by:\n %s"%query)
        self.conn.check_if_not_exists_in_database(query)
        logger.info("==Correct! Company %s does not has '%s' in DB"%(company_name, permission))

    def verify_user_has_permission_in_DB(self, user_id, permission_id):
        query = "select * from access_auth where RSRC_ID = '%s'  and accessor_id in " \
                "(select accessor_id from accessor where usr_id ='%s')"%(permission_id,  user_id)
        self.conn.check_if_exists_in_database(query)
        logger.info("==Correct, User %s has permission '%s' in DB"%(user_id, permission_id))

    def verify_user_has_no_such_permission_in_DB(self, user_id, permission_id):
        query = "select * from access_auth where RSRC_ID = '%s'  and accessor_id in " \
                "(select accessor_id from accessor where usr_id ='%s')"%(permission_id, user_id)
        logger.debug("Verify User has no such Permission by:\n %s"%query)
        self.conn.check_if_not_exists_in_database(query)
        logger.info("==Correct, user '%s' has not permissoin '%s' in DB"%(user_id, permission_id))

    def get_permission_id_from_label(self, label_text, label_type, label_usage):
        '''
        get the permission id from the label text and type.
        :param label_text: The text value of the label
        :param label_type: 'group' or 'single'
        :param label_usage: 'company' or 'user'
        :param element_type: 'check_box' or 'radio_button'
        :return:
        '''
        self.statement = None
        label_text = string.strip(label_text)
        logger.info("Get permission ID for lable type = '%s', and label_usage = '%s'"%(label_type, label_usage))
        if label_type =='group' and label_usage=='company':
            self.statement = "select RSRC_ID  from resourc where RSRC_TYP_CD='3' and RSRC_GROUPTYPE='company' and PRSNTTN_ID in " \
                    "(select PRSNTTN_ID from presentation where PRSNTTN_LABEL in " \
                    "('%s'))"%label_text
        elif label_type =='group' and label_usage=='user':
            self.statement = "select RSRC_ID  from resourc where RSRC_TYP_CD='3' and RSRC_GROUPTYPE='user' and PRSNTTN_ID in " \
                    "(select PRSNTTN_ID from presentation where PRSNTTN_LABEL in " \
                    "('%s'))"%label_text
        elif label_type == 'single' and label_usage == 'company':
            self.statement = "select RSRC_ID  from resourc where RSRC_TYP_CD='2' " \
                        "and PRSNTTN_ID in (select PRSNTTN_ID from presentation where PRSNTTN_LABEL in ('%s'))"%label_text
        elif label_type == 'single' and label_usage == 'user':
            self.statement = "select RSRC_ID1 from RSRC_TO_RSRC_REL where REL_TYP_CD = 'Copermission' " \
                             "and RSRC_ID2 in (SELECT RSRC_ID  FROM resourc  WHERE RSRC_TYP_CD=2 " \
                             "and PRSNTTN_ID IN (SELECT PRSNTTN_ID  FROM presentation  " \
                             "WHERE PRSNTTN_LABEL IN = '%s'))"%label_text
        else:
            raise Exception("Sorry, no such label_type: '%s' and usage: '%s'"%(label_type, label_usage))

        ds = self.conn.query(self.statement)
        if len(ds)==0:
            raise AssertionError("No result of %s, please check if you input label text correctly : '%s'"%(self.statement, label_text))
        logger.info("Get '%s' id: '%s' "%(label_text, ds[0][0]))
        return ds[0][0]

    def calculate_company_permission(self, label_text, company_id):
        base_query = "SELECT RSRC_ID FROM resourc  WHERE RSRC_TYP_CD='2' AND PRSNTTN_ID  IN  " \
                 "(SELECT PRSNTTN_ID FROM presentation  WHERE PRSNTTN_LABEL IN ('%s'))"%label_text
        ext_query = "SELECT RSRC_ID FROM resourc WHERE CMPNY_TYP_CD<>'admin' AND RSRC_ID  IN" \
                    "(SELECT distinct RSRC_ID2  FROM RSRC_TO_RSRC_REL  WHERE REL_TYP_CD IN ('Implied', 'Copermission')" \
                    "  AND RSRC_ID1  IN  " \
                    "(SELECT RSRC_ID  FROM resourc  WHERE RSRC_TYP_CD='2' AND PRSNTTN_ID  IN " \
                    "(SELECT PRSNTTN_ID  FROM presentation  WHERE PRSNTTN_LABEL IN " \
                    "('%s'))))"%label_text
        print base_query
        print ext_query

        base_permission_ds = self.conn.query(base_query)
        ext_permission_ds = self.conn.query(ext_query)

        company_permission_list=[]


        if len(base_permission_ds) == 2:
            query_state = "select REL_TYP_CD from RSRC_TO_RSRC_REL where RSRC_ID1='%s' and RSRC_ID1='%s'"%(base_permission_ds[0][0], base_permission_ds[1][0])
            #print "" + query_state
            try:
                self.conn.check_if_exists_in_database(query_state)
            except AssertionError, ex:
                # print "Base Permission : %s"%base_permission_ds[0]
                company_permission_list +=base_permission_ds[0]

        for each in ext_permission_ds:
            company_permission_list +=each

        logger.info("Company permission list:")
        for each in company_permission_list:
            logger.info(each)
        return company_permission_list

    def get_direct_company_permission_from_DB(self, company_id):
        query_state = "select RSRC_ID from company_xroads join  COMPANY_RESOURCE on company_xroads.CMPNY_CD=COMPANY_RESOURCE.CMPNY_ID where " \
                      "COMPANY_RESOURCE.CMPNY_ID='%s' and COMPANY_RESOURCE.RSRC_TYP_CD='2'"%company_id
        print query_state
        result_ds = self.conn.query(query_state)
        permission_list = []
        if len(result_ds) == 0:
            logger.warn("No permission get for this company %s, please check again"%company_id)
        else:
            for each in result_ds:
                permission_list += each
        return permission_list

    def __compare_two_permission_list(self, perm_list1, perm_list2):
        set_a = set(perm_list1)
        set_b = set(perm_list2)
        diff = set_a ^ set_b
        if len(diff)==0:
            #print "The two permission list are exactly same!"
            #logger.info("The two permission list are exactly same!")
            return True
        else:
            #print "The two permission list are different: %s"%diff
            raise AssertionError("The two permission list are different: %s"%diff)

    def get_company_id(self, company_name):
        query_statement = "select CMPNY_CD from company_xroads where CMPNY_NAME = '%s'"%company_name
        result = self.conn.query(query_statement)
        if len(result)==0:
            raise AssertionError("No such company '%s'"%company_name)
        else:
            return result[0][0]

    def verify_company_permission_DB_against_ODSEE(self, company_name):
        self.login_to_xroad6_dev()
        company_id = self.get_company_id(company_name)
        perm1 = self.get_direct_company_permission_from_DB(company_id)
        ldap = Call_LDAP()
        ldap.login_ODSEE()
        perm2 = ldap.get_company_permissions_from_ldap(company_id)
        if self.__compare_two_permission_list(perm1, perm2)== True:
            logger.info("The permissions in ODSEE are same as in DB")


    def verify_resource_id_has_attributes_in_DB(self, resource_id, label, order='', label_tag='', company_typ_cd=''):
        if company_typ_cd!='':
            statement = "Select * from Presentation where PRSNTTN_LABEL='%s' and PRSNTTN_ORDER in ('%s')" \
                    "and PRSNTTN_LABEL_TAG in ('%s') and PRSNTTN_ID in " \
                    "(select PRSNTTN_ID from RESOURC where RSRC_ID='%s' and CMPNY_TYP_CD ='%s')"%(label, order, label_tag, resource_id, company_typ_cd)
        elif order!='' and label_tag!='':
            statement = "Select * from Presentation where PRSNTTN_LABEL='%s' and PRSNTTN_ORDER in ('%s')" \
                    "and PRSNTTN_LABEL_TAG in ('%s') and PRSNTTN_ID in " \
                    "(select PRSNTTN_ID from RESOURC where RSRC_ID='%s')"%(label, order, label_tag, resource_id)
        else:
            statement = "Select * from Presentation where PRSNTTN_LABEL='%s' and PRSNTTN_ID in " \
                    "(select PRSNTTN_ID from RESOURC where RSRC_ID='%s')"%(label, resource_id)

        logger.debug("fun: verify_resource_id_has_attributes_in_DB")
        logger.debug(statement)

        try:
            self.conn.check_if_exists_in_database(statement)
            msg = "==Verified Resource '%s' has attributes in DB Succeed! \nRSRC_ID %s has Label-->'%s' + Order-->'%s' + Tag-->'%s' and Company_Type_CD-->'%s'\n %s"%(resource_id, resource_id, label, order, label_tag, company_typ_cd, statement)
            logger.info(msg)
        except AssertionError, e:
            query = "select PRSNTTN_LABEL, PRSNTTN_ORDER, PRSNTTN_LABEL_TAG from Presentation where PRSNTTN_ID in "\
                    "(select PRSNTTN_ID from RESOURC where RSRC_ID='%s')"%(resource_id)
            result = self.conn.query(query)
            admin = self.conn.query("SELECT CMPNY_TYP_CD FROM RESOURC WHERE RSRC_ID='%s'"%resource_id)
            msg = "!!!Expected:['%s', '%s', '%s', '%s'] --> Actual: [%s, %s]"%(label, order, label_tag, company_typ_cd, result[0], admin[0][0])

            logger.warn(e.message)
            raise AssertionError(msg)

    def verify_menu_id_has_attributes_in_db(self, menu_present_id, label, present_permissoin, onclick_url=''):
        if onclick_url=='':
            query="Select * from Presentation where PRSNTTN_ID='%s' and PRSNTTN_LABEL='%s' and " \
                  "PRSNTTN_TYPE='menuitem' and PRSNTTN_PERMISSIONID='%s'"%(menu_present_id, label, present_permissoin)
        else:
            query="Select * from Presentation where PRSNTTN_ID='%s' and PRSNTTN_LABEL='%s' and " \
                  "PRSNTTN_TYPE='menuitem' and PRSNTTN_PERMISSIONID='%s' and onclick='%s'"%(menu_present_id, label, present_permissoin, onclick_url)
        self.conn.check_if_exists_in_database(query)
        msg = "==Verified menu '%s' has label -->'%s', visible permission->'%s'"%(menu_present_id, label, present_permissoin)
        logger.info(msg)

    def verify_menu_item_belongs_to_group(self, menu_present_id, menu_group_id):
        query = "select * from RSRC_TO_RSRC_REL where RSRC_ID1 in " \
                "(select RSRC_ID from resourc where PRSNTTN_ID = '%s') and RSRC_ID2='%s' and REL_TYP_CD='PresGroup'"%(menu_present_id, menu_group_id)
        msg = "==Verified menu item id '%s' in group -->'%s'"%(menu_present_id, menu_group_id)
        logger.info(msg)


    def verify_presentation_label_in_radio_group_in_DB(self, presentation_label, radio_group_name, offpermissionid=''):
        '''
        Verify
        '''
        query_state = "select * from presentation where PRSNTTN_LABEL='%s' and PRSNTTN_RADIO_GRP='%s' and PRSNTTN_RADIO_BUTTON ='%s'"%(presentation_label, radio_group_name, offpermissionid)
        self.conn.check_if_exists_in_database(query_state)
        self.conn.row_count_is_equal_to_x(query_state, 1)
        msg = "Correct! Presentation_label: '%s' has -->radio_group_name: '%s' and radio button_offpermissionid = %s "%(presentation_label, radio_group_name, offpermissionid)
        logger.info(msg)

    def verify_resource_relationship(self, rsrc_id1, rsrc_id2, relation):
        statement = "select * from RSRC_TO_RSRC_REL where RSRC_ID1='%s' and RSRC_ID2='%s' and Rel_Typ_CD='%s'"%(rsrc_id1, rsrc_id2, relation)
        logger.debug(statement)
        msg = "==Succeed of resource relation in DB! -->'%s' | ' %s ' | '%s'\n"%(rsrc_id1, rsrc_id2, relation)
        self.conn.check_if_exists_in_database(statement)
        logger.info(msg)

    def verify_resource_to_company_relation(self, resource_id, company_id, relation):
        '''
        Verify if the company allow or deny the permission. relation = 'deny'
        '''
        statement = "select * from RSRC_TO_COMPANY_REL where  COMPANY_ID='%s' and REL_TYPE_CODE='%s' and RSRC_ID='%s'"%(company_id, relation, resource_id)
        msg = "====Succeed! '%s' is %s by company '%s'"%(resource_id, relation, company_id)
        self.conn.check_if_exists_in_database(statement)
        logger.info(msg)

    def verify_company_permission_restrict(self, rsrc_id, company_id, relation_type):
        statement = "select * from RSRC_TO_COMPANY_REL where  COMPANY_ID='%s' and REL_TYPE_CODE='%s' and RSRC_ID='%s'"
        msg = "====Verify if %s and %s has %s relation:'%s'\n %s"%(rsrc_id, company_id, relation_type, statement)
        logger.info(msg)
        self.conn.check_if_exists_in_database(statement)

    def close_db_connection(self):
        self.conn.disconnect_from_database()


if __name__=="__main__":
    a = Call_Oracle()
    a.login_to_dev_db()
    #a.verify_resource_id_has_attributes_in_DB('ICMMS_Mobile_Data', 'ICMMS', '0010', '4316')
    #a.verify_resource_relationship('ICMMS_Mobile_Data', 'MD-1', 'Product')
#     a.verify_resource_id_has_attributes_in_DB('ICMMS_Mobile_Data_Co', 'ICMMS', '0010', '4316')
    a.verify_resource_id_has_attributes_in_DB('MIP_Server_None', 'No MIP Access','0005','4314')
    a.close_db_connection()
    # b = Call_LDAP()
    # #a.login_ldap()
    # b.login_ODSEE()
    # #a.search_company('A_-50')
    # list_b = b.get_company_permissions_from_ldap('A-7')
    # b.close_conn()
    # a.compare_two_permission_list(list_a, list_b)
__author__ = 'g705360'

import ldap
from robot.api import logger
import string

class Call_LDAP():
    conn = None
    perm_list = []

    def login_ldap(self, ldap_name=''):
        if ldap_name=='' or ldap_name=='test_ldap' or ldap_name=='ldap' or ldap_name=='LDAP':
            self.login_ldap_test()
        elif ldap_name == 'ODSEE' or 'dev_ldap':
            logger.info("Login to develop ODSEE enviroment")
            self.login_ldap_dev()
        else:
            raise AssertionError("No such ldap name : %s"%ldap_name)

    def login_ldap_test(self, server=None, username=None, password=None):
        if server==None:
            server = "ldap://mudfish.syniverse.com:3889"
            dn = "uid=ldapbrowser,o=crossroads"
            pw = "#readonly#"

        try:
            self.conn =ldap.initialize('ldap://mudfish.syniverse.com:3889')
            self.conn.simple_bind_s(dn, pw)
        except ldap.LDAPError, e:
            print e.message['info']
            if type(e.message) == dict and e.message.has_key('desc'):
                print e.message['desc']

    def login_ldap_dev(self, server=None, username=None, password=None):
        if server==None:
            server = "crossroads-lb-dev.syniverse.com:3899"
            dn = "uid=xroads,o=crossroads"
            pw = "!xroads!"

        try:
            self.conn =ldap.initialize('ldap://crossroads-lb-dev.syniverse.com:3899')
            self.conn.simple_bind_s(dn, pw)
        except ldap.LDAPError, e:
            print e.message['info']
            if type(e.message) == dict and e.message.has_key('desc'):
                print e.message['desc']
                     
    def get_company_permissions_from_ldap(self, company_id):
        search_DN = "ou=Carriers,o=Crossroads"
        filter = "carrieruid="+company_id
        attribute = ["permissionid"]
        result = self.conn.search_s(search_DN, ldap.SCOPE_SUBTREE, filter, attribute)
        if len(result)==0:
            raise AssertionError("No permission result get from LDAP for company '%s'"%company_id)
        org_perm_list = result[0][1]['permissionid']
        #print "Permission List is : %s"%org_perm_list
        perm_list = []

        for each in org_perm_list:
            each_elem = string.split(each, '^')[0]
            #print each_elem
            perm_list.append(each_elem)
        logger.debug("The permission list is %s"%perm_list)
        return perm_list

    def get_permission_id(self, permission_lable):
        search_DN = "ou=Permission,o=Crossroads"
        filter = "permissiongroupid="+permission_lable
        attribute = ["presentationlabel"]
        result = self.conn.search_s(search_DN, ldap.SCOPE_SUBTREE, filter)
        if len(result[0][1]) == 0:
            return AssertionError("Sorry can not find this permission group")
        return result[0][1]

    def get_user_permissions_from_ldap(self, user_id):
        search_DN = "ou=People,o=Crossroads"
        filter = "dnqualifier="+user_id
        attribute = ["permissionid"]
        result = self.conn.search_s(search_DN, ldap.SCOPE_SUBTREE, filter)
        #print result
        return result[0][1]
    
    def verify_permission(self, permission, permission_set):
        if permission in str(permission_set):
            return True
        else:
            return False

    def verify_company_has_permission_in_ldap(self, company_id, permission_id):
        result = self.get_company_permissions_from_ldap(company_id)
        if self.verify_permission(permission_id, result):
            logger.info("==Correct! Found: company '%s' has ---> '%s' in LDAP"%(company_id, permission_id))
        else:
            logger.info("Sorry, company '%s' does not has permission -->%s"%(company_id, permission_id))



    def verify_company_has_no_such_permission_in_ldap(self, company_id, permission_id):
        try:
            result = self.get_company_permissions_from_ldap(company_id)
            if self.verify_permission(permission_id, result) == True:
                return AssertionError("The permission '%s' is in LDAP"%permission_id)
        except AssertionError:
            logger.info("==Correct! Company '%s' has no permission %s in LDAP"%(company_id, permission_id))
            return True

    def verify_user_has_permission_in_ldap(self, user_id, permission_id):
        result_set = self.get_user_permissions_from_ldap(user_id)
        if user_id in result_set:
            return True
        else:
            msg = "==Sorry, user '%s' does not has permission --> '%s' in LDAP"%(user_id, permission_id)

    def verify_user_has_no_such_permission_in_ldap(self, user_id, permission_id):

        try:
            result_set = self.get_user_permissions_from_ldap(user_id)
            if self.verify_permission(permission_id, result_set) == True:
                return AssertionError("The permission '%s' is in LDAP"%permission_id)
        except AssertionError:

            return True

    def verify_company_has_permissions_in_ldap(self, company_id, permission_id_list):
        result = self.get_company_permissions_from_ldap(company_id)
        for each in permission_id_list:
            return self.verify_permission(each, result)

    def verify_user_has_permissions_in_ldap(self, user_id, permission_id_list):
        result_set = self.get_user_permissions_from_ldap()
        for permission in permission_id_list:
            self.verify_permission(permission)

    def verify_resource_id_has_attributes_in_ldap(self, group_or_single, perm_id, label, order='', lab_tag=''):
        if group_or_single == 'group':
            search_DN = "ou=PermissionGroups,o=Crossroads"
            filter = "permissiongroupid=%s"%perm_id
        elif group_or_single=='single':
            search_DN = "ou=PermissionCode,o=Crossroads"
            filter = "permissionid=%s"%perm_id
        else:
            raise AssertionError("Sorry, no such type %s"%group_or_single)

        act_label = self.conn.search_s(search_DN, ldap.SCOPE_SUBTREE, filter, ["presentationlabel"])[0][1]["presentationlabel"][0]
        act_order = self.conn.search_s(search_DN, ldap.SCOPE_SUBTREE, filter, ["presentationorder"])[0][1]["presentationorder"][0]
        if lab_tag!='':
            act_label_tag = self.conn.search_s(search_DN, ldap.SCOPE_SUBTREE, filter, ["presentationlabeltag"])[0][1]["presentationlabeltag"][0]
        if label!=act_label:
            raise AssertionError("Sorry, Expect label: '%s' -->Actual: '%s'"%(label, act_label))
        elif order!='' and order!=act_order:
            raise AssertionError("Sorry, Expect order: '%s' -->Actual: '%s'"%(order, act_order))
        elif lab_tag!='' and lab_tag!=act_label_tag:
            raise AssertionError("Sorry, Expect tag: '%s' -->Actual: '%s'"%(order, act_label_tag))
        msg = "==Verify Resource %s has attributes Correct in LDAP! \n %s has lable-->'%s' order-->'%s' and tag-->'%s' in LDAP"%(perm_id, perm_id, label, order, lab_tag)
        print msg
        logger.info(msg)

    def __get_permission_attribute_values_in_ldap(self, permissionid, attribute_key):
        search_DN = "ou=PermissionCode,o=Crossroads"
        filter = "permissionid=%s"%permissionid
        value_dict = self.conn.search_s(search_DN, ldap.SCOPE_SUBTREE, filter, [attribute_key])[0][1]
        return value_dict[attribute_key]

    def verify_resource_relation_from_ldap(self, rsrc_id1, rsrc_id2, relation):
        actual_values = self.__get_permission_attribute_values_in_ldap(rsrc_id1, relation)
        if len(actual_values)==1:
            if rsrc_id2 == actual_values[0]:
                msg = "==Correct, '%s' has attribute '%s' = '%s'"%(rsrc_id1, relation, rsrc_id2)
                print msg
                logger.info(msg)
                return True
            else:
                raise AssertionError("Expect: '%s' actually has %s of '%s' instead of %s"%(rsrc_id1, relation, rsrc_id2, actual_values))
        elif len(actual_values)==0:
            raise AssertionError("Expect: '%s' actually has %s of '%s' but we got EMPTY Result"%(rsrc_id1, relation, rsrc_id2))
        else:
            if rsrc_id2 in actual_values:
                msg = "==Correct, '%s' has attribute '%s' = '%s'"%(rsrc_id1, relation, rsrc_id2)
                print msg
                logger.info(msg)
                return True
            else:
                raise AssertionError("Expect: '%s' should has %s of '%s' instead of %s"%(rsrc_id1, relation, rsrc_id2, actual_values))

    def __get_object_attribute(self, search_DN, filter, attribute):
        #search_DN = "ou=PresentationObjects,o=Crossroads"
        #filter = "uid=%s"%presentation_id
        value_dict = self.conn.search_s(search_DN, ldap.SCOPE_SUBTREE, filter, [attribute])[0][1]
        return value_dict[attribute]

    def verify_menu_id_has_attributes_in_ldap(self, presentation_id, presentation_label, presentaion_permission, group_id='', onclick_url=''):
        search_DN = "ou=PresentationObjects,o=Crossroads"
        filter = "uid=%s"%presentation_id
        actual_pres_label = self.__get_object_attribute(search_DN, filter, 'presentationlabel')
        actual_pres_perm = self.__get_object_attribute(search_DN, filter, 'permissionid')
        actual_onclick = self.__get_object_attribute(search_DN, filter, 'onclick')
        actual_group_id = self.__get_object_attribute(search_DN, filter, 'presentationgroupid')

        if actual_pres_label[0] != presentation_label:
            raise AssertionError("Presentation Label Expect: '%s' -->Actual: '%s'"%(actual_pres_label[0], presentation_label))
        if actual_pres_perm[0] != presentaion_permission:
            raise AssertionError("Pesentation Permission Expect: '%s' -- >Actual: '%s'"%(actual_pres_perm[0], presentaion_permission))
        if onclick_url!='' and actual_onclick[0]!=onclick_url:
            raise AssertionError("onclick URL Expect: '%s' -- >Actual: '%s'"%(actual_onclick[0], onclick_url))
        if group_id!='' and actual_group_id[0]!=group_id:
            raise AssertionError("Group ID Expect: '%s' -- >Actual: '%s'"%(actual_group_id[0], group_id))

        logger.info("== Correct! All menu attributes in LDAP are found")


    def close_ldap_connection(self):
        self.conn.unbind()


if __name__=="__main__":
    a = Call_LDAP()
    a.login_ldap('dev_ldap')
    #a.verify_resource_id_has_attributes_in_ldap('group', 'MIP_Subscriber_Group_Co', 'Subscriber - MIP', '0206', '4320')
    a.verify_menu_id_has_attributes_in_ldap('MIP_Services_Report', 'MIP Services Report', 'MIP_Subscriber_Implied', 'Subscriber_Menu')
    a.close_ldap_connection()

#     a.verify_permission_attribute_in_ldap('group','ICMMS_Mobile_Data_Grp_User','Mobile Data ICMMS','', '4315')
#     a.close_conn()
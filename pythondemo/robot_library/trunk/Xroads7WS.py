from suds.wsse import *
from suds.client import *
from robot.api import logger


class Xroads7WS(object):
    ROBOT_LIBRARY_SCOPE = 'GLOBAL'
    def __init__(self, init_param="something dummy"):
        #removed code that initializes the connection in the constructor, because it's taking long time
        logger.warn("Xroads7WS created!")
        pass

    def _create_client(self):
        logger.debug("WS client creation start!")
        self.clt = Client(self.wsdl_file)
        logger.debug("WS client created!")
        security = Security()
        token = UsernameToken(self.ws_security_username, self.ws_security_password)
        security.tokens.append(token)
        self.clt.set_options(wsse=security)
        logger.debug("WS client creation finish!")


    def init_ws_client(self, wsdl_file_url, username, password):
        self.wsdl_file = wsdl_file_url
        self.ws_security_username = username
        self.ws_security_password = password
        self._create_client()


    def get_all_company_perms(self, companyid):
        return self.clt.service.getAllCompanyPerms(companyid)

    def get_all_role_perms(self, userloginid):
        return self.clt.service.getAllRolePerms(userloginid)

    def get_all_user_perms(self, userloginid):
        return self.clt.service.getAllUserPerms(userloginid)

    def get_company_details(self, userloginid):
        return self.clt.service.getCompanyDetails(userloginid)

    def get_company_viewobject(self, userloginid):
        return self.clt.service.getCompanyViewObject(userloginid)

    def get_spids_by_company(self, companyid):
        return self.clt.service.getSpidsByCompany(companyid)

    def get_user_details(self, userloginid):
        return self.clt.service.getUserDetails(userloginid)

    def get_user_permissions_by_prefix(self, userloginid, perm_prefix):
        return self.clt.service.getUserPermissionsByPrefix(userloginid, perm_prefix)

    def get_user_timezone(self, userloginid):
        return self.clt.service.getUserTimeZone(userloginid)

    def get_user_timezone_object(self, userloginid):
        return self.clt.service.getUserTimeZoneObject(userloginid)

    def get_users_with_perms_by_ctpid(self, userloginid):
        return self.clt.service.getUsersWithPermsByCTPID(userloginid)

    def has_company_permission(self, userloginid):
        return self.clt.service.hasCompanyPermission(userloginid)

    def has_permission(self, userloginid):
        return self.clt.service.hasPermission(userloginid)

    def validate_user_session(self, userloginid):
        return self.clt.service.validateUserSession(userloginid)

    def company_should_have_permissions_in_xroads7_web_service(self, company_id, *permissions):
        all_permission = self.clt.service.getAllCompanyPerms(company_id)
        actually_not_in = Xroads7WS.all_in_long(list(permissions), all_permission)
        if len(actually_not_in) > 0:
            raise AssertionError("Company %s should have these permissions %s. But actually %s are lacked"
                                 % (company_id, list(permissions), actually_not_in))

    def company_should_not_have_permissions_in_xroads7_web_service(self, company_id, *permissions):
        all_permission = self.clt.service.getAllCompanyPerms(company_id)
        actually_in = Xroads7WS.none_in_long(list(permissions), all_permission)
        if len(actually_in) > 0:
            raise AssertionError("Company %s should NOT have any of permissions %s. But actually %s are contained"
                                 % (company_id, list(permissions), actually_in))

    def user_should_have_permissions_in_xroads7_web_service(self, user_login_id, *permissions):
        all_permission = self.clt.service.getAllUserPerms(user_login_id)
        logger.debug("All permissions: %s " % ', '.join(all_permission))
        actually_not_in = Xroads7WS.all_in_long(list(permissions), all_permission)
        logger.debug("Permissions expected but missing: %s " % ', '.join(actually_not_in))
        if len(actually_not_in) > 0:
            raise AssertionError("User %s should have these permissions %s. But actually %s are not available"
                                 % (user_login_id, list(permissions), actually_not_in))

    def user_should_not_have_permissions_in_xroads7_web_service(self, user_login_id, *permissions):
        all_permission = self.clt.service.getAllUserPerms(user_login_id)
        logger.debug("All permissions: %s " % ', '.join(all_permission))
        actually_in = Xroads7WS.none_in_long(list(permissions), all_permission)
        logger.debug("Permissions not expected but assigned : %s " % ', '.join(actually_in))
        if len(actually_in) > 0:
            raise AssertionError("User %s should NOT have any of permissions %s. But actually %s are available"
                                 % (user_login_id, list(permissions), actually_in))

    @staticmethod
    def all_in_long(short_list, long_list):
        actually_not_in = []
        for s in short_list:
            if s not in long_list:
                actually_not_in.append(s)
        return actually_not_in

    @staticmethod
    def none_in_long(short_list, long_list):
        actually_in = []
        for s in short_list:
            if s in long_list:
                actually_in.append(s)
        return actually_in


if __name__ == "__main__":
    ws = Xroads7WS()
    ws.init_ws_client('http://vapp011-hw1.syniverse.com:7777/CrossroadsWS/CrossroadsWebServiceAppModuleService?wsdl',
                      'YS00003', 'Word1@dev7')

    # print ws.user_should_have_permissions_in_xroads7_web_service('BS00006', 'VisPro_Diameter_Alert_Pod_View_userP',
    # 'VisPro_Diameter_Heat_Map_Pod_View_userP',
    #                                                              'abcd', 'xyz')

    #print ws.user_should_not_have_permissions_in_xroads7_web_service('BS00006', 'VisPro_Diameter_Alert_Pod_View_userP',
    #'abcd', 'xyz')
    print ws.get_all_user_perms('C000289')
    # print ws.get_spids_by_company('57')
    # print ws.get_all_company_perms('57')
    # print ws.get_user_permissions_by_prefix("C000274", "")

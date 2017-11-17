__author__ = 'g705360'
from Page import Page
from robot.api import logger
from Call_Oracle import Call_Oracle
from Call_LDAP import Call_LDAP
import time
from selenium.common.exceptions import *
import string

class Page_Modify_User_Profile():
    '''
    This test is used for daily testing, since I can not get the User Permission ID from DB.
    To keep consistant, i let company permission ID get from test case as well.
    '''
    def __init__(self):
        self.page = Page()
        self.company_name = None
        self.carrier_id = None

    def navigate_to_page_modify_user_profile(self, driver):
        '''
        Navigate to Modify User Profile Page
        '''
        ext_url = r"/servlet/UserSelect?action=modify_user_profile"
        full_url = self.page.get_base_url(driver) + ext_url

        time.sleep(6)
        driver.get(full_url)
        logger.info("==Navigated to Modify User Profile Page")

    def select_user_from_company(self, driver, company_name, user_id, first_name=None, last_name=None, alternate_id=None):
        '''
        This key word has not imply first_name, last_name into test now.
        '''

        self.page.find_element_by_xpath(driver, "//input[@name='searchStr']").send_keys(company_name)
        self.page.find_element_by_xpath(driver, "//input[@name='dnqualifier']").send_keys(user_id)
        self.page.find_element_by_xpath(driver, "//input[@value='Search']").click()
        logger.info("==Selected User: '%s' from Company: '%s'"%(user_id, company_name))

    def __click_button_update_permissions(self, driver):
        time.sleep(2)
        self.page.find_element_by_id(driver, 'permButton').click()


    def grant_user_permission(self, driver, group_label, group_perm_id, user_label, user_perm_id):
        self.__click_button_update_permissions(driver)
        group_xpath = self.page.get_xpath(group_perm_id, 'group')
        user_xpath = self.page.get_xpath(user_perm_id, 'single')
        driver.switch_to_frame('MiddleFrame')
        try:
            comp_elem = self.page.find_element_by_xpath(driver, user_xpath)
            if comp_elem.get_attribute('checked') is None:
                time.sleep(1)
                comp_elem.click()
                logger.info("====Assigned User permission: '%s/%s'"%(group_label, user_label))
            else:
                logger.warn("..The permission '%s' was already assigned. Continue without change"%user_label)
        except (NoSuchElementException, ElementNotVisibleException):
            group_elem = self.page.find_element_by_xpath(driver, group_xpath)
            logger.info("Find group label and click on %s"%group_elem)
            group_elem.click()
            comp_elem = self.page.find_element_by_xpath(driver, user_xpath)
            if comp_elem.get_attribute('checked') is None:
                time.sleep(1)
                comp_elem.click()
                logger.info("====Assigned User permission: '%s/%s'"%(group_label, user_label))
            else:
                logger.warn("..The permission '%s' was already assigned. Continue without change"%user_label)


    def verify_user_has_no_such_permission_on_webpage(self, driver, group_label, group_perm_id, user_label, user_perm_id):
        try:
            self.grant_user_permission( driver, group_label, group_perm_id, user_label, user_perm_id)
        except (NoSuchElementException, ElementNotVisibleException):
            logger.info("==Correct! Current User can not find permission %s in permission Tree")
            return True
        raise AssertionError("==Incorrect! Current User should not find permission %s in permission Tree")


    def revoke_user_permission(self, driver, group_label, group_perm_id, user_label, user_perm_id):
        self.__click_button_update_permissions(driver)
        group_elem_xpath = self.page.get_xpath(group_perm_id, 'group')
        comp_elem_xpath = self.page.get_xpath(user_perm_id, 'single')
        driver.switch_to_frame('MiddleFrame')
        try:
            comp_elem = self.page.find_element_by_xpath(driver, comp_elem_xpath)
            if comp_elem.get_attribute('checked') is None:
                logger.warn("..The permission '%s' was NOT checked before. Continue without change"%user_label)
            else:
                time.sleep(1)
                comp_elem.click()
                logger.info("====Revoked the company permission '%s/%s'"%(group_label, user_label))

        except (NoSuchElementException, ElementNotVisibleException):
            group_elem = self.page.find_element_by_xpath(driver, group_elem_xpath)
            group_elem.click()
            comp_elem = self.page.find_element_by_xpath(driver, comp_elem_xpath)
            if comp_elem.get_attribute('checked') is None:
                logger.warn("..The permission '%s' was NOT checked before. Continue without change"%user_label)
            else:
                time.sleep(1)
                comp_elem.click()
                logger.info("====Revoked the company permission '%s/%s'"%(group_label, user_label))

    def verify_user_has_permission_in_DB(self, user_id, permission_id, db_name='xroad6_test'):
        a = Call_Oracle()       
        a.login_db(db_name)
        a.verify_user_has_permission_in_DB(user_id, permission_id)
        a.close_db_connection()

    def verify_user_has_no_such_permission_in_DB(self, user_id, permission_id, db_name='xroad6_test'):
        a = Call_Oracle()   
        a.login_db(db_name)
        a.verify_user_has_no_such_permission_in_DB(user_id, permission_id)
        a.close_db_connection()

    def verify_user_has_permission_in_ldap(self, user_id, permission_id, ldap_name='LDAP'):
        a = Call_LDAP()
        a.login_ldap(ldap_name)
        a.verify_user_has_permission_in_ldap(user_id, permission_id)
        a.close_ldap_connection()

    def verify_user_has_no_such_permission_in_ldap(self, user_id, permission_id, ldap_name='LDAP'):
        a = Call_LDAP()
        a.login_ldap(ldap_name)
        a.verify_user_has_no_such_permission_in_ldap(user_id, permission_id)
        a.close_ldap_connection()


    def verify_user_has_product_link(self, driver, product_label):
        time.sleep(10)
        driver.switch_to_frame("NavFrame")
        elem = self.page.find_element_by_link_text(driver, product_label)
        elem.click()
        logger.info("==Find product %s"%product_label)
        driver.switch_to_window(driver.window_handles[1])

    def verify_user_has_no_such_product_link(self, driver, product_label):
        try:
            self.verify_user_has_product_link(driver, product_label)
        except (NoSuchElementException, ElementNotVisibleException):
            logger.info("==Correct, user don't have product %s"%product_label)
            return True
        raise AssertionError("==Correct, user should NOT has product %s"%product_label)

    def verify_user_has_menu_link(self, driver, menu_link, sub_menu=''):
        time.sleep(10)
        if sub_menu=='':
            try:
                driver.switch_to.default_content
                driver.switch_to_frame("MenuFrame")
                elems = self.page.find_elements_by_xpath(driver, "//span")
                found_flag=0
                for each in elems:
                    if each.text == menu_link:
                        logger.info("Found the menu link: '%s'"%menu_link)
                        found_flag=1
                        return True
                if found_flag==0:
                    raise AssertionError("Sorry, not abel to find the menu link '%s'"%menu_link)
            except NoSuchFrameException:
                elems = self.page.find_elements_by_xpath(driver, "//span")
                found_flag=0
                for each in elems:
                    if each.text == menu_link:
                        logger.info("Found the menu link: '%s'"%menu_link)
                        found_flag=1
                        return True
                if found_flag==0:
                    raise AssertionError("Sorry, not abel to find the menu link '%s'"%menu_link)
        else:
            self.verify_user_has_menu_link(driver,menu_link)
            self.verify_user_has_menu_link(driver, sub_menu)


    # def verify_user_has_product_and_menu_link_on_webpage(self, driver, product_label, first_level_menu, second_level_menu=''):
    #     time.sleep(10)
    #     link=string.replace(product_label, ' ','')
    #     ext_url = '/servlet/MenuTree?menuid=%s'%link
    #     full_url = self.page.get_base_url(driver) + ext_url
    #     driver.get(full_url)
    #     elems = self.page.find_elements_by_xpath(driver, '//span')
    #     for each in elems:
    #         if each.text == first_level_menu:
    #             logger.info("==Success, found the first level menu '%s'"%first_level_menu)
    #             each.click()
    #             if second_level_menu=='':
    #                 return True
    #             else:
    #                 elems = self.page.find_elements_by_xpath(driver, '//span')
    #                 for each in elems:
    #                     if each.text == second_level_menu:
    #                         logger.info("==Success, found the first level menu '%s'"%first_level_menu)
    #                         each.click()
    #                     else:
    #                         raise NoSuchElementException("Not able to find the second level menu '%s'"%second_level_menu)
    #         else:
    #             raise NoSuchElementException("Not able to find the first level menu '%s'"%first_level_menu)

    def verify_user_has_no_product_or_menu_link_on_webpage(self, driver, product_label, first_level_menu, second_level_menu=''):
        try:
            self.verify_user_has_product_and_menu_link_on_webpage(driver, product_label, first_level_menu,second_level_menu)
        except NoSuchElementException:
            logger.info("==Correct, User is not able to see the %s and %s"%(product_label, first_level_menu))
            return True
        raise AssertionError("==Wrong! User should not able to find product %s and menu %s"(product_label, second_level_menu))

    def select_menu_on_big_frame(self, driver, menu_text):
        #driver.switch_to_frame("NavFrame")
        driver.switch_to.default_content()
        driver.switch_to_frame('MenuFrame')
        elems = self.page.find_elements_by_xpath(driver, "//span")
        found_flag = 0
        for each in elems:
            if each.text == menu_text:
                found_flag=1
                each.click()
                logger.info("Click on menu '%s'"%menu_text)
        if found_flag==0:
            raise AssertionError("==Not able to find the menu %s"%menu_text)


    def save_user_permission(self, driver):
        driver.switch_to.default_content()
        driver.switch_to_frame('BottomFrame')
        self.page.find_element_by_id(driver, 'continue').click()

if __name__=='__main__':
    a = Page_Modify_User_Profile()
    a.verify_user_has_permission_in_ldap('P002661', 'ICMMS_Mobile_Data')
    a.verify_user_has_no_such_permission_in_ldap('P002661', 'ICMMS_Mobile_Data')
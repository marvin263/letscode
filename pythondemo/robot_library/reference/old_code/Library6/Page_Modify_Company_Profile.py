__author__ = 'g705360'

from Page import Page
from robot.api import logger
from Call_Oracle import Call_Oracle
from Call_LDAP import Call_LDAP
import time
from selenium.common.exceptions import *

class Page_Modify_Company_Profile():
    '''
    This test is used for daily testing, since I can not get the User Permission ID from DB.
    To keep consistant, i let company permission ID get from test case as well.
    '''
    def __init__(self):
        self.page = Page()
        self.company_name = None
        self.carrier_id = None
        self.db_conn = Call_Oracle()
        self.ldap_conn = Call_LDAP()

    def navigate_to_page_modify_company_profile(self, driver):
        '''
        Navigate to Company Permission Modification Page
        '''
        ext_url = r"/servlet/CompanySelect?action=modify_company_profile"
        full_url = self.page.get_base_url(driver) + ext_url
        time.sleep(6)
        driver.get(full_url)
        logger.info("==Navigated to Company Permission Modification Page")

    def select_company(self, driver, company_name, carrier_id=None):
        '''
        Use the exact full company_name to find the company,
        '''
        self.page.find_element_by_xpath(driver, "//input[@name='searchStr']").send_keys(company_name)
        time.sleep(1)
        self.page.find_element_by_xpath(driver, "//button[@id='submitPermBtn']").click()
        logger.info("====Selected Company %s for permission change"%company_name)

    def assign_company_permission(self, driver, group_label, group_id, company_label, company_id):
        '''
        From the Design document, you already know the group_label, group_id, company_label, company_id
        So, we use this parameter to select the new permission and check it.
        '''

        group_elem_xpath = self.page.get_xpath(group_id, 'group')
        comp_elem_xpath = self.page.get_xpath(company_id, 'single')
        try:
            comp_elem = self.page.find_element_by_xpath(driver, comp_elem_xpath)
            if comp_elem.get_attribute('checked') is None:
                time.sleep(1)
                comp_elem.click()
                time.sleep(1)
                logger.info("====Assigned company permission: '%s/%s'"%(group_label, company_label))
            else:
                logger.warn("..The permission '%s' was already assigned. Continue without change"%company_label)
        except (NoSuchElementException, ElementNotVisibleException):
            group_elem = self.page.find_element_by_xpath(driver, group_elem_xpath)
            group_elem.click()
            time.sleep(1)
            comp_elem = self.page.find_element_by_xpath(driver, comp_elem_xpath)
            if comp_elem.get_attribute('checked') is None:
                time.sleep(1)
                comp_elem.click()
                time.sleep(1)
                logger.info("====Assigned company permission: '%s/%s'"%(group_label, company_label))
            else:
                time.sleep(1)
                logger.warn("..The permission '%s' was already assigned. Continue without change"%company_label)

    def verify_permission_invisible_to_company(self, driver, group_label, group_id, company_label, company_id):
        group_elem_xpath = self.page.get_xpath(group_id, 'group')
        comp_elem_xpath = self.page.get_xpath(company_id, 'single')
        try:
            group_elem = self.page.find_element_by_xpath(driver, group_elem_xpath)
            group_elem.click()
            # The page's find_element_by_xpath will not throw out the exception. So use the driver's method.
            comp_elem = driver.find_element_by_xpath(comp_elem_xpath)
            comp_elem.click()
        except NoSuchElementException, ex:
            logger.info("==Correct!, current company unable to find permission '%s/%s'"%(group_label,company_label))
        except ElementNotVisibleException, ex:
            logger.info("==Correct!, current company unable to see permission '%s/%s'"%(group_label,company_label))
        else:
            raise AssertionError("Incorrect! Current company should not able to see the permission '%s/%s'!"%(group_label,company_label))


    def revoke_company_permission(self, driver, group_label, group_id, company_label, company_id):
        '''
        From the Design document, you already know the group_label, group_id, company_label, company_id
        So, we use this parameter to select the new permission and check it.
        '''
        group_elem_xpath = self.page.get_xpath(group_id, 'group')
        comp_elem_xpath = self.page.get_xpath(company_id, 'single')
        try:
            comp_elem = self.page.find_element_by_xpath(driver, comp_elem_xpath)
            if comp_elem.get_attribute('checked') is None:
                logger.warn("..The permission '%s' was NOT checked before. Continue without change"%company_label)
            else:
                time.sleep(1)
                comp_elem.click()
                logger.info("====Revoked the company permission '%s/%s'"%(group_label, company_label))

        except (NoSuchElementException, ElementNotVisibleException):
            group_elem = self.page.find_element_by_xpath(driver, group_elem_xpath)
            group_elem.click()
            comp_elem = self.page.find_element_by_xpath(driver, comp_elem_xpath)
            if comp_elem.get_attribute('checked') is None:
                logger.warn("..The permission '%s' was NOT checked before. Continue without change"%company_label)
            else:
                time.sleep(1)
                comp_elem.click()
                logger.info("====Revoked the company permission '%s/%s'"%(group_label, company_label))

    def save_company_permissions(self, driver):
        self.page.find_element_by_xpath(driver, "//input[@value='Continue']").click()
        text = self.page.find_element_by_xpath(driver, "//td[@align='left']").text
        if "Success!" in text:
            logger.info("====Got below information from webpage, after save permission changes")
            print text
            return True
        else:
            raise AssertionError(text)

    def verify_company_has_permission_in_DB(self, company_name, permission, db_name='test_db'):
        if db_name == 'test_db':
            self.db_conn.login_to_test_db()
        elif db_name == 'dev_db':
            self.db_conn.login_to_dev_db()
        self.db_conn.verify_company_has_permission_in_DB(company_name, permission)
        self.db_conn.close_db_connection()

    def verify_company_has_no_such_permission_in_DB(self, company_name, permission, db_name='test_db'):
        self.db_conn.login_db(db_name)
        self.db_conn.verify_company_has_no_such_permission_in_DB(company_name, permission)
        self.db_conn.close_db_connection()

    def verify_company_has_permission_in_ldap(self, company_id, permission_id, ldap_name='test_ldap'):
        self.ldap_conn.login_ldap(ldap_name)
        self.ldap_conn.verify_company_has_permission_in_ldap(company_id,permission_id)
        self.ldap_conn.close_ldap_connection()


    def verify_company_has_no_such_permission_in_ldap(self, company_id, permission_id, ldap_name='test_ldap'):
        self.ldap_conn.login_ldap(ldap_name)
        self.ldap_conn.verify_company_has_no_such_permission_in_ldap( company_id, permission_id)
        self.ldap_conn.close_ldap_connection()





if __name__ == '__main__':
    a = Page_Modify_Company_Profile()
    a.verify_company_has_permission_in_ldap('A-7', 'MIP_Enterprise_Co','dev_ldap')
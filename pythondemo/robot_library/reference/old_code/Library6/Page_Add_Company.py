__author__ = 'g705360'

from Page import Page
from robot.api import logger
from Call_Oracle import Call_Oracle
import time

class Page_Add_Company():

    def __init__(self):
        self.page = Page()
        self.company_name = None
        self.carrier_id = None

    def add_company(self, driver, company_name, type=None):
        ext_url = r"/servlet/AddNewCompanyProfile?action=add_company_profile"
        full_url = self.page.get_base_url(driver) + ext_url
        time.sleep(10)
        logger.info("Go directly to add company page")
        driver.get(full_url)
        self.page.find_element_by_xpath(driver, "//input[@tabindex='1']").send_keys(company_name)
        if type!=None:
            option = "//option[@value='%s']",type
            self.page.find_element_by_xpath(driver, option).click()
        self.page.find_element_by_tag_name(driver, "button").click()
        logger.info("Save company info")
        result_title = self.page.find_element_by_xpath(driver, "//strong")
        logger.info(result_title)

        #Get the carrier_id for the new company
        self.carrier_id = self.page.find_elements_by_xpath(driver, "//input[@name='carrieruid']").get_attribute("value")


    def get_new_carrier_id(self):
        if self.carrier_id==None:
            logger.warn("Sorry, you need add company to get the new carrier_id")
            return False
        else:
            return self.carrier_id

    def set_new_company_permission(self, driver, permission_group_label, permission_label, element_type='checkbo'):

        #click on the "Update Permission" button on the page
        logger.info("To update company permissions....")
        self.page.find_element_by_xpath("//input[@type='submit']").click()

        #get permission id, which will be used to find the label in permission change page
        db_conn = Call_Oracle()
        db_conn.login_to_xroad6()
        perm_group_id = db_conn.get_permission_id_from_label(permission_group_label, 'group', element_type)
        perm_id = db_conn.get_permission_id_from_label(permission_label, 'company_permission', element_type)
        db_conn.close_connection()

        perm_group_tag = perm_group_id + '_span'
        perm_id_tag = perm_id

        perm_group_xpath = "//span[@id='%s']"%perm_group_tag
        per_id_xpath = "//input[@id='%s']"%perm_id_tag

        self.page.find_element_by_xpath(driver, perm_group_xpath).click()
        self.page.find_elements_by_xpath(driver, per_id_xpath).click()

    
    def save_company_permission(self, driver):
        logger.info("To save permissions....")
        time.sleep(5)
        self.page.find_element_by_xpath(driver, "//input[@type='button']").click()






__author__ = 'g705360'

from Page import Page
from robot.api import logger
from Call_Oracle import Call_Oracle
import time

class Page_Add_User():

    def __init__(self):
        self.page = Page()
        self.user_name = None
        self.user_id = None

    def navigate_to_add_user_page(self, driver):
        ext_url = r"/servlet/UserSelect?action=add_user_profile"
        full_url = self.page.get_base_url(driver) + ext_url

        time.sleep(10)
        logger.info("Go directly to add company page")
        driver.get(full_url)

    def select_user_company(self, driver, company_name):
        input_box = self.page.find_element_by_xpath(driver, "//input[@name='searchStr']")
        input_box.send_keys(company_name)
        time.sleep(1)

        self.page.find_element_by_xpath(driver, "//input[@type='submit']").click()

    def select_user_type(self, driver, type):
        if type == 'End User':
            self.page.find_element_by_xpath(driver, "//option[@value='0']").click()
        elif type == 'Crossroads Administrator':
            self.page.find_element_by_xpath(driver, "//option[@value='20']").click()
        elif type == 'Super Tester':
            self.page.find_element_by_xpath(driver, "//option[@value='77']").click()
        elif type == 'Associate Administrator':
            self.page.find_element_by_xpath(driver, "//option[@value='associateadmin']").click()
        else:
            raise AssertionError("Sorry, does not support such type of user: %s"%type)

    def add_user(self, driver, user_type, last_name, first_name, phone='12345', email='alan.zhang@syniverse.com', contact_last_name='Zhang', contact_first_name='Alan', contact_phone='12345'):
        self.select_user_type(driver, user_type)
        self.page.find_element_by_xpath(driver, "//input[@name='sn']").send_keys(last_name)
        self.page.find_element_by_xpath(driver, "//input[@name='givenname']").send_keys(first_name)
        self.page.find_element_by_xpath(driver, "//input[@name='telephonenumber']").send_keys(phone)
        self.page.find_element_by_xpath(driver, "//input[@name='mail']").send_keys(email)
        self.page.find_element_by_xpath(driver, "//input[@name='contactsn']").send_keys(contact_last_name)
        self.page.find_element_by_xpath(driver, "//input[@name='contactgivenname']").send_keys(contact_first_name)
        self.page.find_element_by_xpath(driver, "//input[@name='contacttelephonenumber']").send_keys(contact_phone)

        self.page.find_element_by_xpath(driver, "//input[@id='submitBtn']").click()

        driver.switch_to_alert_window().accept()
        if self.page.find_element_by_xpath(driver, "//div[id='error']"):
            raise AssertionError("Sorry, can not create user, saving error")


__author__ = 'g705360'
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from robot.api import logger
from urlparse import urlparse
import time
from Call_Oracle import Call_Oracle
from selenium.common.exceptions import *

class Page():

    def find_element(self, driver, method, value, time_out=20):
        try:
            elem = WebDriverWait(driver, time_out).until(EC.presence_of_element_located((method, value)))
        except:
            msg = "Sorry, can not find specific element: " + value
            logger.warn(msg)
            raise AssertionError(msg)
        return elem

    def find_element_by_id(self, driver, value):
        return self.find_element(driver, By.ID, value)

    def find_element_by_name(self, driver, value):
        return self.find_element(driver, By.NAME, value)

    def find_element_by_xpath(self, driver, value):
        return self.find_element(driver, By.XPATH, value)

    def find_element_by_css_selector(self, driver, value):
        return self.find_element(driver, By.CSS_SELECTOR, value)

    def find_element_by_class_name(self, driver, value):
        return self.find_element(driver, By.CLASS_NAME, value)

    def find_element_by_link_text(self, driver, value):
        return self.find_element(driver, By.LINK_TEXT, value)

    def find_element_by_tag_name(self, driver, value):
        return self.find_element(driver, By.TAG_NAME, value)

    def find_elements_by_xpath(self, driver, value):
        if self.find_element(driver, By.XPATH, value):
            return driver.find_elements_by_xpath(value)

    def get_base_url(self, driver):
        url = driver.current_url
        result = urlparse(url)
        base_url = result.scheme + r"://" + result.netloc
        return base_url

    def get_xpath(self, perm_id, label_type):
        if label_type == 'group':
            perm_group_tag = perm_id + '_span'
            perm_group_xpath = "//span[@id='%s']"%perm_group_tag
            return perm_group_xpath

        elif label_type == 'single':
            perm_id_tag = perm_id
            perm_id_xpath = "//input[@id='%s']"%perm_id_tag
            return perm_id_xpath
        else:
            raise Exception("Sorry, no such label_type: '%s' and perm_id: '%s'"%(perm_id, label_type))

    def navigate_to_page(self, driver, url):
        time.sleep(5)
        driver.get(url)


    def check_on_permission(self, db_name, driver, group_label, single_label, type):
        '''
        If you just know the presentation label and want to select it.
        '''
        db_conn = Call_Oracle()
        db_conn.login(db_name)
        group_perm_id = db_conn.get_permission_id_from_label(group_label, 'group', type)
        single_perm_id = db_conn.get_permission_id_from_label(single_label, 'single', type)
        db_conn.close_connection()

        group_elem_xpath = self.get_xpath(group_perm_id, 'group')
        single_elem_xpath = self.get_xpath(single_perm_id, 'single')
        try:
            comp_elem = self.page.find_element_by_xpath(driver, single_elem_xpath)
            if comp_elem.get_attribute('checked') is None:
                time.sleep(1)
                comp_elem.click()
                logger.info("====Assigned %s permission: '%s/%s'"%(type, group_label, single_label))
            else:
                logger.warn("..The permission '%s' was already assigned. Continue without change"%single_label)
        except (NoSuchElementException, ElementNotVisibleException):
            group_elem = self.page.find_element_by_xpath(driver, group_elem_xpath)
            group_elem.click()
            comp_elem = self.page.find_element_by_xpath(driver, single_elem_xpath)
            if comp_elem.get_attribute('checked') is None:
                time.sleep(1)
                comp_elem.click()
                logger.info("====Assigned %s permission: '%s/%s'"%(type, group_label, single_label))
            else:
                logger.warn("..The permission '%s' was already assigned. Continue without change"%single_label)


    def check_off_permission(self, db_name, driver, group_label, single_label, type):
        db_conn = Call_Oracle()
        db_conn.login(db_name)
        group_perm_id = db_conn.get_permission_id_from_label(group_label, 'group', type)
        single_perm_id = db_conn.get_permission_id_from_label(single_label, 'single', type)
        db_conn.close_connection()

        group_elem_xpath = self.page.get_xpath(group_perm_id, 'group')
        single_elem_xpath = self.page.get_xpath(single_perm_id, 'single')
        try:
            comp_elem = self.page.find_element_by_xpath(driver, single_elem_xpath)
            if comp_elem.get_attribute('checked') is None:
                logger.warn("..The permission '%s' was NOT checked before. Continue without change"%single_label)
            else:
                time.sleep(1)
                comp_elem.click()
                logger.info("====Revoked %s permission '%s/%s'"%(type, group_label, single_label))

        except (NoSuchElementException, ElementNotVisibleException):
            group_elem = self.page.find_element_by_xpath(driver, group_elem_xpath)
            group_elem.click()
            comp_elem = self.page.find_element_by_xpath(driver, single_elem_xpath)
            if comp_elem.get_attribute('checked') is None:
                logger.warn("..The permission '%s' was NOT checked before. Continue without change"%single_label)
            else:
                time.sleep(1)
                comp_elem.click()
                logger.info("====Revoked the %s permission '%s/%s'"%(type, group_label, single_label))


    def close_browser(self, driver, wait_time = 10):
        time.sleep(wait_time)
        driver.quit()
        logger.info("==Closed Browser")


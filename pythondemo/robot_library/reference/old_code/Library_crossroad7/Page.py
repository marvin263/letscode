__author__ = 'g705360'
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from robot.api import logger


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

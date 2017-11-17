__author__ = 'g705360'

from Page import Page
from selenium import webdriver
import time
from robot.api import logger

class Page_Login():

    def __init__(self):
        self.a = Page()

    def get_driver(self, browser='Ie'):
        '''
        Set the browser to test
        '''
        if browser == 'Ie':
            return webdriver.Ie()
        elif browser == 'Chrome':
            return webdriver.Chrome()
        return webdriver.Firefox()


    def login(self, driver, user_name, password, url="https://crossroads-test.syniverse.com"):
        '''
        Login to the system with provided user_name and password.
        '''
        driver.get(url)
        driver.maximize_window()
        self.a.find_element_by_id(driver, 'username').send_keys(user_name)
        self.a.find_element_by_id(driver, 'password').send_keys(password)
        self.a.find_element_by_xpath(driver, "//button[@name='Submit']").click()
        logger.info("==Logged into %s"%url)


    def navigate_to_admin_page(self, driver):
        '''
        navigate to the admin page
        '''
        time.sleep(10)
        driver.switch_to_frame("NavFrame")
        elem = self.a.find_element_by_id(driver, "navcenterAdmin")
        elem.click()
        driver.switch_to_window(driver.window_handles[1]) #Switch to new admin window

    def click_on_product(self, driver, product_name):
        time.sleep(6)
        driver.switch_to_frame("NavFrame")
        elem = self.a.find_element_by_link_text(driver, product_name)
        elem.click()
        driver.switch_to_window(driver.window_handles[1]) #Switch to new admin window


    def close_browser(self, driver, wait_time=10):
        self.a.close_browser(driver, int(wait_time))





__author__ = 'g705360'

from Page import Page
from selenium import webdriver

class Page_Login(Page):

    def get_driver(self, browser='Firefox'):
        if browser == 'Ie':
            return webdriver.Ie()
        elif browser == 'Chrome':
            return webdriver.Chrome()
        return webdriver.Firefox()


    def login(self, driver, user_name, password):
        url = "https://mysyniverse-test.syniverse.com"
        driver.get(url)
        self.find_element_by_id(driver, 'username').send_keys(user_name)
        self.find_element_by_id(driver, 'password').send_keys(password)
        self.find_element_by_xpath(driver, '//input[@type="submit"]').click()


    def select_product(self, driver, product_name):
        default_product_list =('My Home', 'Tools', 'Administration', 'Help')
        if product_name == 'Administration':
            self.find_element_by_link_text(driver, 'Administration').click()


if __name__ == "__main__":
    a = Page_Login()
    driver = a.get_driver()
    a.login(driver, 'AA00022', 'Synitest1!')

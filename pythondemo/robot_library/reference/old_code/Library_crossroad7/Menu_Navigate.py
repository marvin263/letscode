__author__ = 'g705360'

from Page import Page
from robot.api import logger

class Menu_Navigate(Page):
    def click_on_menu(self, driver, menu_name):
        if menu_name == 'Users':
            self.find_element_by_link_text(driver, 'Users').click()
        elif menu_name == 'Companies':
            self.find_element_by_link_text(driver, 'Companies').click()
        elif menu_name == 'Roles':
            self.find_element_by_link_text(driver, 'Roles').click()
        elif menu_name == 'Groups':
            self.find_element_by_link_text(driver, 'Groups').click()
        elif menu_name == 'User Class':
            self.find_element_by_link_text(driver, 'User Class').click()
        else:
            msg = "Sorry, can not find the menu: " +menu_name
            logger.warn(msg)

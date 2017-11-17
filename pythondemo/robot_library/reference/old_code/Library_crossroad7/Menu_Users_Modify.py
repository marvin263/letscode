__author__ = 'g705360'
from Page import Page
from robot.api import logger

class Menu_Users_Modify(Page):
    tab_name_list = ('Additional Details','User Roles','User Groups','User Permissions','Accessible Companies')

    def select_tab(self, driver, tab_name):
        if tab_name in self.tab_name_list:
            elem = self.find_element_by_link_text(driver, tab_name)
            elem.click()
        else:
            msg = 'Sorry, the tab name ' + tab_name + 'is not defined in this page'
            logger.warn(msg)
            raise AssertionError(msg)

    def check_on_user_permissions(self, driver, permission_group, permission):
        selector = r"//a[@title='Expand " + permission_group + r"']"
        self.find_element_by_xpath(driver, selector).click()
        sub_selctor = r"//input[@name='" + permission + r"']"
        item = self.find_element_by_xpath(driver, sub_selctor)
        item.clear()
        item.click()


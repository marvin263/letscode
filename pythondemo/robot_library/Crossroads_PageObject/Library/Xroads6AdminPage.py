import time
from robot.api import logger
from PageBase import PageBase


class Xroads6AdminPage():

    def __init__(self):
        self.xroads6_admin_page_elements = \
            {
                'Menu Frame': 'name=MenuFrame',
                'User': 'id=_celllItem23',
                'User_Profile': 'id=_celllItem22',
                'User_Profile_Add': 'id=_celllItem18',
                'User_Profile_Modify': 'id=_celllItem19',
                'User_Profile_Delete': 'id=_celllItem21',
                'Company': '_celllItem21',
                'Company_Profile': 'id=_celllItem6',
                'Company_Profile_Add': 'id=_celllItem2',
                'Company_Profile_Modify': 'id=_celllItem3',
                'Company_Profile_Delete': 'id=_celllItem5'
            }
        global selenium
        selenium = PageBase.get_selenium2libraryExtend_instance()

    def navigate_to_user_page(self, user_menu_name):
        """Go To User->Profile->Add or Modify ... Page
        :param user_menu_name: menu name like User_Profile_Add
        :return:
        """

        self._select_to_menu_frame()
        selenium.click_element(self.xroads6_admin_page_elements['User'])
        selenium.click_element(self.xroads6_admin_page_elements['User_Profile'])
        selenium.click_element(self.xroads6_admin_page_elements[user_menu_name])
        logger.info("Go To %s Page" % user_menu_name)

    def navigate_to_company_page(self, company_sub_menu_name, menu_name):
        """Go To Company Page
        :param company_sub_menu_name:
        :param menu_name:
        :return:
        """
        self._select_to_menu_frame()
        selenium.click_element(self.xroads6_admin_page_elements['Company'])
        selenium.click_element(self.xroads6_admin_page_elements[company_sub_menu_name])
        selenium.click_element(self.xroads6_admin_page_elements[menu_name])
        logger.info("Go To %s Page" % menu_name)

    def _select_to_menu_frame(self):
        time.sleep(2)  # better to wait a second when open a new window
        selenium.select_window('Admin')
        selenium.wait_until_page_contains_element(self.xroads6_admin_page_elements['Menu Frame'], 20)
        selenium.wait_until_frame_select(self.xroads6_admin_page_elements['Menu Frame'], 'Administrative')

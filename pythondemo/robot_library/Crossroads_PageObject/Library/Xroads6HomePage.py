from PageBase import PageBase
from robot.api import logger
import time


class Xroads6HomePage():

    def __init__(self):

        self.xroads6_home_page_elements = \
            {
                'Home': "xpath=//a[@id='navcenterHome']",
                'Admin': 'id=navcenterAdmin',
                'Right Arrow': "xpath=//td[@class='main_nav_tab'][4]",
                'Navigation Frame': 'name=NavFrame',
                'Reports & Analysis': "xpath=//a[@name='navcenterBI']"
            }

        global selenium
        selenium = PageBase.get_selenium2libraryExtend_instance()

    def navigate_to_apps(self, app_name):
        """Click Apps On the Navigation Bar
        :param app_name:
        :return:
        """
        # select navigatioin frame
        selenium.wait_until_page_contains_element(self.xroads6_home_page_elements['Navigation Frame'], 30)
        self._switch_to_nav_frame()

        # If Navigation Bar too long show , should click the right arrow first
        try:
            time.sleep(2)
            selenium.click_element(self.xroads6_home_page_elements[app_name])
        except:
            selenium.wait_until_page_contains_element(self.xroads6_home_page_elements['Right Arrow'], 30)
            selenium.click_element(self.xroads6_home_page_elements['Right Arrow'])
            selenium.click_element(self.xroads6_home_page_elements[app_name])

        logger.warn("Go To %s Application!" % app_name)

    def link_should_visible_in_navigation_bar(self, link_name):
        """Check If Link Is Visible In Crossroads6
        :param link_name:
        :return:
        """
        # select navigation frame
        self._switch_to_nav_frame()

        selenium.page_should_contain_element(self.xroads6_home_page_elements[link_name])
        logger.warn("%s In The Navigation Bar!" % link_name)

    def link_should_not_visible_in_navigation_bar(self, link_name):
        self._switch_to_nav_frame()

        selenium.page_should_not_contain(link_name)
        logger.warn("%s Is Not In The Navigation Bar!" % link_name)

    def link_should_visible_in_application_menu(self, app_name, link_name):
        """Some times link is in the application menu after user click app_name
        :param app_name:
        :return:
        """
        self._switch_to_nav_frame()

        selenium.page_should_contain(link_name)
    def _switch_to_nav_frame(self):
        selenium.wait_until_frame_select(self.xroads6_home_page_elements['Navigation Frame'],
                                         self.xroads6_home_page_elements['Home'] , 20)
from robot.api import logger
from PageBase import PageBase


class Xroads6LoginPage(PageBase):
    def __init__(self):

        PageBase.__init__(self)

        self.xroads6_login_page_elements = \
            {
                'User ID': 'id = username',
                'Password': 'id = password',
                'Submit': 'name = Submit'
            }
        global selenium
        selenium = self.get_selenium2libraryExtend_instance()

    def login_crossroads6(self, url, username, password, browser_type):
        """Login to the crossroads6
        :param url:
        :param username:
        :param password:
        :param browser_type:
        :return:
        """

        selenium.open_browser(url=url, browser=browser_type)
        selenium.wait_until_page_contains_element(self.xroads6_login_page_elements['Submit'])
        selenium.maximize_browser_window()
        selenium.input_text(self.xroads6_login_page_elements['User ID'], username)
        selenium.input_password(self.xroads6_login_page_elements['Password'], password)
        selenium.click_element(self.xroads6_login_page_elements['Submit'])
        logger.warn("User:%s Login Crossroads Successfully!" % username)

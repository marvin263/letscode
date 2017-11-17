from Selenium2LibraryExtend import Selenium2LibraryExtend
from robot.api import logger


class PageBase():
    ROBOT_LIBRARY_SCOPE = 'GLOBAL'
    def __init__(self):
        global selenium
        selenium = Selenium2LibraryExtend()
        logger.debug("Initialize Selenium2LibraryExtend Object!")

    @staticmethod
    def get_selenium2libraryExtend_instance():
        """Get The Selenium2Library Instance Which Created By Robot
        :return: Selenium2Library Instance
        """

        # selenium = BuiltIn().get_library_instance('Selenium2Library')
        return selenium

    @staticmethod
    def get_webdriver_instance():
        """Get The WebDriver Instance Which Created By Selenium
        :return: WebDriver instance from selenium
        """
        return selenium._current_browser()
        # return PageBase.get_selenium2library_instance()._current_browser()

    def set_selenium_default_implicit_wait_time(self, seconds):
        selenium.set_selenium_implicit_wait(seconds)
        logger.info("Set Selenium Implicit Wait Time = %s seconds" % seconds)

    def set_browser_default_impilcit_wait_time(self, seconds):
        selenium.set_browser_implicit_wait(seconds)
        logger.info("Set Current Browser's Implicit Wait Time = %s seconds" % seconds)

    def kill_browsers(self):
        selenium.kill_browsers()
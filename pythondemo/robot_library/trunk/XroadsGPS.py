__author__ = 'g706811'
__version__ = 0.1
from robot.api import logger


class XroadsGPS():
    ENV_XROADS6 = "XROADS6"
    ENV_XROADS7 = "XROADS7"
    ENV_UNKNOWN = "UNKNOWN_ENV"
    MODE_DEV = "DEV"
    MODE_TEST = "TEST"
    MODE_UNKNOWN = "UNKNOWN_MODE"
    XROADS6_ACCESS_INFO = ("URL", "USERNAME", "PASSWORD")
    XROADS7_ACCESS_INFO = ("URL", "USERNAME", "PASSWORD")
    XROADS6_LEFT_MENU_MAP = {
        'My Profile': ('0', ),
        'My Password': ('1', ),
        'Company': ('17',
                    {'Profile': ('6',
                                 {'Add': ('2',),
                                  'Modify': ('3',),
                                  'View': ('4',),
                                  'Delete': ('5',)}),
                     'Service Profile': ('11',
                                         {'Add': ('7',),
                                          'Modify': ('8',),
                                          'View': ('9',),
                                          'Delete': ('10',)}),
                     'AO/CID': ('16',
                                {'Add': ('12',),
                                 'Modify': ('13',),
                                 'View': ('14',),
                                 'Delete': ('15',)})}),
        'User': ('23',
                 {'Profile': ('22',
                              {'Add': ('18',),
                               'Modify': ('19',),
                               'View': ('20',),
                               'Delete': ('21',)})}),
        'Role Maintenance': ('24',),
        'Group Maintenance': ('25',),
        'Data Security Roles': ('26',),
        'Services': ('31',
                     {'Add': ('27',),
                      'Modify': ('28',),
                      'View': ('29',),
                      'Delete': ('30',)}),
        'Administrative': ('41',
                           {'Password': ('33',
                                         {'Reset': ('32',)}),
                            'Inactive User IDs': ('34',),
                            'Deleted User IDs': ('35',),
                            'Permissions': ('40',
                                            {'Add': ('36',),
                                             'Modify': ('37',),
                                             'View': ('38',),
                                             'Delete': ('39',)})})
    }

    def __init__(self):
        # TODO initialized should be done explicitly by user
        self._default_browser = "chrome"
        self.XROADS6_ACCESS_INFO = ("https://crossroads-dev.syniverse.com", "M000221", "Word1@dev6")
        self.XROADS7_ACCESS_INFO = (
        "https://mysyniverse-dev.syniverse.com/mysyniverse/faces/tabnavigation", "YS00003", "Word1@dev7")

    def navigate_xroads6_left_menu(self, driver, *menu_path):
        menu_frame_locator = "xpath=//frame[@name='MenuFrame']"
        driver.wait_until_page_contains_element(menu_frame_locator, "30 secs")
        driver.select_frame(menu_frame_locator)
        driver.wait_until_page_contains_element("xpath=//span[text()='Administrative']", error='Left menu could not be loaded')
        navi_map = self.XROADS6_LEFT_MENU_MAP
        menu_id_tag = 'lItem'
        for menu in menu_path:
            if menu in navi_map:
                menu_info = navi_map[menu]
                menu_ele_id = menu_id_tag + menu_info[0]
                # it is a menu folder, check if it is already open
                if len(menu_info) > 1:
                    navi_map = menu_info[1]
                    expanded_check_ele_id = "id=%strue" % menu_ele_id
                    if driver._is_visible(expanded_check_ele_id):
                        continue
                else:
                    navi_map = {}
                driver.click_element("id=%s" % menu_ele_id)
            else:
                raise AssertionError("Unable to find menu path: %s" % menu_path)
        driver.unselect_frame()

    def navigate_xroads6_left_menu_generic(self, driver, *path):
        menu_frame_locator = "xpath=//frame[@name='MenuFrame']"
        driver.wait_until_page_contains_element(menu_frame_locator)
        driver.select_frame(menu_frame_locator)#TODO safe select frame
        driver.wait_until_page_contains_element("xpath=//span[text()='Administrative']", error='Left menu could not be loaded')
        menu_item_locator = "xpath=//div/table/tbody/tr/td[span[text()='%s']]"
        menu_id_tag = 'lItem'
        parent_ele = None
        for menu in path:
            if parent_ele is not None:
                #first open the parent node if necessary
                parent_entry_index = int(parent_ele.get_attribute('id')[10:])   #_celllItem3
                expanded_check_ele_id = "id=%s%strue"%(menu_id_tag,parent_entry_index)
                while not driver._is_visible(expanded_check_ele_id):
                    driver.click_element("id=%s%s"%(menu_id_tag, parent_entry_index))
                # for all menu with the specified label, find the one that has largest index, but less than the parent
                menu_entries = driver.find_element_by_xpath(menu_item_locator % menu, False,False)
                if len(menu_entries) == 0:
                    raise RuntimeError("Not able to find menu with label %s" % menu)
                current_entry_index = 0
                for ele in menu_entries:
                    ele_index = int(ele.get_attribute('id')[10:])   #_celllItem3
                    if ele_index > current_entry_index and ele_index < parent_entry_index:
                        parent_ele = ele
                logger.debug("Found element %s for menu label %s" % (menu, parent_ele.get_attribute("id")))
            else:
                menu_entries = driver.find_element_by_xpath(menu_item_locator % menu, False,False)
                if len(menu_entries) > 1:
                    raise RuntimeError("More than one occurrence of menu entry %s found" % menu)
                elif len(menu_entries) == 0:
                    raise RuntimeError("Not able to find menu with label %s" % menu)
                else:
                    parent_ele = menu_entries[0]
                    logger.debug("Found element %s for menu label %s" % (menu, parent_ele.get_attribute("id")))
        parent_ele.click()
        driver.unselect_frame()

    @property
    def default_browser(self):
        return self._default_browser

    @default_browser.setter
    def default_browser(self, b):
        self._default_browser = b

    def check_environment(self, url):
        env = self.ENV_UNKNOWN
        mode = self.MODE_UNKNOWN
        if "mysyniverse" in url:
            env = self.ENV_XROADS7
        elif "crossroads" in url:
            env = self.ENV_XROADS6
        else:
            logger.warn("Unknown environment url %s" % url)
        if "dev" in url:
            mode = self.MODE_DEV
        elif "test" in url:
            mode = self.MODE_TEST
        else:
            logger.warn("Unknown environment url %s" % url)
        return (env, mode)

    def is_in_xroads6(self, url):
        env = self.check_environment(url)[0]
        return env == self.ENV_XROADS6

    def is_in_xroads7(self, url):
        env = self.check_environment(url)[0]
        return env == self.ENV_XROADS7

    def is_in_dev(self, url):
        mode = self.check_environment(url)[1]
        return mode == self.MODE_DEV

    def is_in_test(self, url):
        mode = self.check_environment(url)[1]
        return mode == self.MODE_TEST


if __name__ == "__main__":
    gps = XroadsGPS()
    print gps.default_browser

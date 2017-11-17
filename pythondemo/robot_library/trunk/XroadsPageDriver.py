__author__ = 'g706811'
__version__ = 0.1

import robot
from robot.api import logger
from Selenium2Library import Selenium2Library
from selenium.webdriver.common.keys import Keys

from Xroads6DBUtil import Xroads6DBUtil
from XroadsGPS import XroadsGPS
from ALMUtil import ALMUtil


class XroadsPageDriver(Selenium2Library, Xroads6DBUtil):
    def __init__(self, timeout=5.0, implicit_wait=0.0, run_on_failure='Capture Page Screenshot'):
        self.gps = XroadsGPS()
        self.alm = ALMUtil()  # lazy init the alm
        # TODO parameterize these connection information
        # self.xroads6_db = Xroads6DBUtil("xroads_app", "xroads", r'rhrac1scan.syniverse.com:1521/XRDSD1')
        self.button_xpath_candidates = ("xpath=//button[text()='%s']",
                                        "xpath=//input[@type='button' and @value='%s']",
                                        "xpath=//input[@type='submit' and @value='%s']",
                                        "xpath=//input[@type='reset' and @value='%s']",
                                        "xpath=//button[contains(text(),'%s')]",
                                        "xpath=//input[type='submit' and contains(@value,'%s')]",
                                        "xpath=//button[@id='%s']",
                                        "xpath=//input[@type='submit' and @id='%s']",
                                        "xpath=//input[@type='button' and @id='%s']")
        Selenium2Library.__init__(self, timeout, implicit_wait, run_on_failure)
        logger.warn("XroadsPageDriver initialized")

    def slow_down_selenium_by(self, delta=2):
        old_speed = self._speed_in_secs
        delta_speed = robot.utils.timestr_to_secs(delta)
        new_speed = old_speed + delta_speed
        logger.info("Selenium speed set to %s " % new_speed)
        self.set_selenium_speed(" %s seconds" % new_speed)

    def speed_up_selenium_by(self, delta=2):
        old_speed = self._speed_in_secs
        delta_speed = robot.utils.timestr_to_secs(delta)
        new_speed = old_speed - delta_speed
        if new_speed < 0:
            new_speed = 0
        logger.info("Selenium speed set to %s " % new_speed)
        self.set_selenium_speed(" %s seconds" % new_speed)

    def navigate_with_link(self, locator, verify_locator=None, timeout=None):
        """
        Perform a click action on the provided locator and verify that the verify_locator is present after navigation.
        :param locator:
        :param verify_locator:
        :return:
        """
        if timeout is None:
            timeout = self.get_selenium_timeout()
        self.click_element(locator)
        try:
            self.wait_until_page_contains_element(verify_locator, timeout)
        except AssertionError:
            self.reload_page()
            self.wait_for_adf_sync()
            self.click_element(locator)
            self.wait_until_page_contains_element(verify_locator, timeout,
                                                  error="Could not navigate with link %s" % locator)

    def verify_company_profile(self, field_name, value):
        """
        TODO migrate to use the get_adf_form_value method implementation
        Verify that the form field specified by the field_name has the specified value.
        :param field_name:
        :param value:
        :return:
        """
        val = None
        if field_name in ['Company Name', 'Comments', 'TSG Carrier ID', 'CTP Company ID', 'LSR Domain', 'LSR Password',
                          'Roamwise Path', 'NetScout PM URL', 'NetScout SI URL', 'Resource Center URL']:
            field_locator = "xpath=//tbody[tr[td[label[text()='%s']]]]/tr[2]/td/input" % field_name
            val = self.get_value(field_locator)
        elif field_name in ['Company ID', 'CTP Company Name', 'Short Name', 'Vis Carrier ID']:
            field_locator = "xpath=//tbody[tr[td[label[text()='%s']]]]/tr[2]/td" % field_name
            val = self.get_text(field_locator)
        elif field_name in ['Company Type Code', 'Group Company ID', 'Company Logo']:
            field_locator = "xpath=//tbody[tr[td[label[text()='%s']]]]/tr[2]/td/select" % field_name
            val = self.get_value(field_locator)
        elif field_name in ['Company Accessibility Status']:
            field_locator = "xpath=//tbody[tr[td[label[text()='%s']]]]/tr[2]/td/div/span/input[@type='radio']" % field_name
            status_radios = self._element_find(field_locator, False, True)
            if status_radios[0].is_selected():
                val = 'Active'
            elif status_radios[1].is_selected():
                val = 'Inactive'
            elif status_radios[2].is_selected():
                val = 'Denied'

        if val != value:
            raise AssertionError("Expected value: '%s' in field '%s', but actual is '%s' " % (value, field_name, val))

    def verify_user_profile(self, field_name, value):
        """
        Verify that the form field specified by the field_name has the specified value.
        :param field_name:
        :param value:
        :return:
        """
        # TODO this code works only for user with edit permission to the user being verified, but should be ok. Wrong?
        if field_name in ['First Name', 'Middle Name', 'Last Name', 'Access Start Date', 'Access End Date', 'E-Mail',
                          'Phone', 'Phone Ext', 'Contact First Name', 'Contact Last Name', 'Contact Middle Name',
                          'Alternate ID', 'Contact Phone', 'Contact Phone Ext']:
            val = self.get_adf_form_value(field_name, 'EDITABLE_INPUT')
        elif field_name in ['Login ID']:
            val = self.get_adf_form_value(field_name, 'READONLY_LABEL')
        elif field_name in ['User Type Code', 'Time Zone Code', 'Preferred Language']:
            val = self.get_adf_form_value(field_name, 'DROPDOWN_SELECTION')
        elif field_name in ['Access Status']:
            val = self.get_adf_form_value(field_name, 'RADIO_OPTION')
        else:
            raise RuntimeError("Field %s validation is not supported yet" % field_name)
        if val != value:
            raise AssertionError("Expected value: '%s' in field '%s', but actual is '%s' " % (value, field_name, val))

    def get_adf_form_value(self, field_label, field_type):
        # field_types = ['EDITABLE_INPUT','READONLY_LABEL','DROPDOWN_SELECTION','RADIO_OPTION']
        if field_type == 'EDITABLE_INPUT':
            field_locator = "xpath=//tbody[tr[td[label[contains(text(),'%s')]]]]/tr[2]/td/input" % field_label
            return self.get_value(field_locator)
        elif field_type == 'READONLY_LABEL':
            field_locator = "xpath=//tbody[tr[td[label[contains(text(),'%s')]]]]/tr[2]/td/span" % field_label
            return self.get_text(field_locator)
        elif field_type == 'DROPDOWN_SELECTION':
            field_locator = "xpath=//tbody[tr[td[label[contains(text(),'%s')]]]]/tr[2]/td/select" % field_label
            return self.get_selected_list_label(field_locator)
        elif field_type == 'RADIO_OPTION':
            field_locator = "xpath=//tbody[tr[td[label[contains(text(),'%s')]]]]/tr[2]/td/div/span/input[@type='radio']" % field_label
            label_locator = "xpath=//tbody[tr[td[label[contains(text(),'%s')]]]]/tr[2]/td/div/label[%s]"
            status_radios = self._element_find(field_locator, False, True)
            for idx in range(1, len(status_radios) + 1):
                if status_radios[idx - 1].is_selected():
                    return self.get_text(label_locator % (field_label, idx))
        else:
            raise RuntimeError('ADF Form filed type %s not supported, please contact autobot_dev' % field_type)

    def find_element_by_xpath(self, xpath, first_only, required):
        return self._element_find(xpath, first_only, required)

    def wait_until_not_busy(self, timeout=None, error=None):

        """Waits until the ADF busy indicator is not visible
        :param timeout:
        :param error:
        :return:
        """
        busy_div_locator = "xpath=//body/div[@class='AFBlockingGlassPane']"
        self.wait_until_element_is_not_visible(busy_div_locator, timeout, error)
        logger.info("The Blocking glass div is now not visible, busy status is off!")

    def wait_until_element_is_not_visible(self, locator, timeout=None, error=None):

        """Waits until element specified with `locator` is NOT visible.

        Fails if `timeout` expires before the element is visible. See
        `introduction` for more information about `timeout` and its
        default value.

        `error` can be used to override the default error message.

        See also `Wait Until Page Contains`, `Wait Until Page Contains
        Element`, `Wait For Condition` and BuiltIn keyword `Wait Until Keyword
        Succeeds`.
        """

        def check_visibility():
            visible = self._is_visible(locator)
            if not visible:
                return
            elif visible is None:
                return error or "Element locator '%s' did not match any elements after %s" % (
                    locator, self._format_timeout(timeout))
            else:
                logger.info("Element '%s' still visible. " % locator)
                return error or "Element '%s' is still visible in %s" % (locator, self._format_timeout(timeout))

        self._wait_until_no_error(timeout, check_visibility)

    def navigate_with_xroads6_left_menu(self, *menu_path):
        """
        handy method to navigate the xroads 6 left navigation tree
        :param menu_path:
        :raise AssertionError:
        """
        self.gps.navigate_xroads6_left_menu_generic(self, *menu_path)

    def assign_permissions(self, perm_id_list):
        """
        Add the permissions identified by the perm_id array to the company currently under modify.
        Assuming that it is now on the editing page
        Assuming that
        :param perm_id_list:
        :return:
        """
        # get the permission label and it's permission group id and lable
        logger.info("Assigning %s permissions to the current company!")
        perm_details = self.get_permission_group(perm_id_list)
        for perm_id in perm_details.keys():
            perm_detail = perm_details[perm_id]
            self.assign_permission(perm_id, perm_detail[0], perm_detail[1], perm_detail[2])

    def remove_permissions(self, perm_id_list):
        logger.info("Assigning %s permissions to the current company!")
        perm_details = self.get_permission_group(perm_id_list)
        for perm_id in perm_id_list:
            perm_detail = perm_details[perm_id]
            self.remove_permission(perm_id, perm_detail[0], perm_detail[1], perm_detail[2])

    def assign_permission(self, perm_id, perm_label, perm_group_id, perm_group_label):
        self._ensure_permission_assignment_status(perm_id, perm_label, perm_group_id, perm_group_label, True)

    def _ensure_permission_assignment_status(self, perm_id, perm_label, perm_group_id, perm_group_label,
                                             to_assigned=True):
        """

        :param perm_id:
        :param perm_label:
        :param perm_group_id:
        :param perm_group_label:
        :return:
        """
        # first make sure the perm group is open
        perm_grp_ul_xpath = "xpath=//ul[@id='%s_Group']" % perm_group_id
        perm_input_xpath = perm_grp_ul_xpath + "//input[@id='%s']" % (perm_id, )
        self.page_should_contain_element(perm_grp_ul_xpath,
                                         "Not able to find the permission group %s" % perm_group_label, 'ERROR')
        perm_grp_li_cls = self.get_element_attribute(perm_grp_ul_xpath + "/li@class")
        while "Close" in perm_grp_li_cls:
            self.click_element(perm_grp_ul_xpath + "/li/span")
            perm_grp_li_cls = self.get_element_attribute(perm_grp_ul_xpath + "/li@class")
        self.page_should_contain_element(perm_input_xpath,
                                         "Not able to find the permission %s" % perm_label, 'ERROR')
        # now lets operate on the permissions
        perm_disabled = self.get_element_attribute(perm_input_xpath + "@disabled")
        if not perm_disabled:
            ele = self._element_find(perm_input_xpath, True, True)
            if ele.is_selected() and to_assigned:
                logger.info("Permission %s already assigned, will not change." % perm_label)
            elif not ele.is_selected() and not to_assigned:
                logger.info("Permission %s already removed, will not change." % perm_label)
            else:
                perm_input_type = ele.get_attribute("type")
                if not to_assigned and "radio" in perm_input_type:
                    raise AssertionError(
                        "Permission %s is grouped with other permission and shown as radio button. " % perm_label +
                        "Can not be removed directly. Try assign other permissions in the same group!")
                self.click_element(perm_input_xpath)
        else:
            raise AssertionError("Permission %s is disabled, could not be assigned or removed!" % perm_label)

    def remove_permission(self, perm_id, perm_label, perm_group_id, perm_group_label):
        self._ensure_permission_assignment_status(perm_id, perm_label, perm_group_id, perm_group_label, False)

    def permission_group_should_be_available(self, perm_grp_id_or_label):
        """
        check if the specified permission group is available on the page
        :param perm_grp_id_or_label:
        :return:
        """
        # the queries are common for both 6 & 7
        perm_grp = self.get_permission_group_by_id(perm_grp_id_or_label)
        if perm_grp is None:
            perm_grp = self.get_permission_group_by_label(perm_grp_id_or_label)
        if perm_grp is None:
            raise AssertionError("Not able to find permission group %s" % perm_grp_id_or_label)
        perm_grp_ul_xpath = "xpath=//ul[@id='%s_Group']" % perm_grp[0]
        self.page_should_contain_element(perm_grp_ul_xpath,
                                         "Not able to find the permission group %s on page" % perm_grp[1], 'ERROR')

    def open_xroads7_admin_detail_tab(self, tab_name):
        """
        Open the permission tab for a company if it is not opened
        requires xroads7 env, and assumes the details page already opened
        :return:
        """
        tab_locator = "xpath=//div[contains(@id,'tabh')]/div/div/div/div[a[text()='%s']]" % tab_name
        perms_tab = self._element_find(tab_locator + "/a", True, True)
        perms_tab_open = "p_AFSelected" in perms_tab.get_attribute("class")
        if not perms_tab_open:
            self.click_element(tab_locator + "/a")
        self.wait_for_adf_sync()
        # function for checking if tab is opened
        def tab_open_check():
            tab = self._element_find(tab_locator + "/a", True, True)
            return "p_AFSelected" in tab.get_attribute("class")

        self._wait_until('30 secs', "Could not open the %s tab!" % tab_name, tab_open_check)

    def permission_should_be_assigned(self, perm_id):
        """
        verify that the specified permission is assigned, detects the current environment automatically
        :param perm_id:
        :return:
        """
        url = self.get_location()
        if self.gps.is_in_xroads6(url):
            if not self.is_permission_assigned_in_xroads6(perm_id):
                raise AssertionError("Permission %s is supposed to be assigned, but it's not!" % perm_id)
        elif self.gps.is_in_xroads7(url):
            if not self.is_permission_assigned_in_xroads7(perm_id):
                raise AssertionError("Permission %s is supposed to be assigned, but it's not!" % perm_id)
        else:
            raise AssertionError("Not able to recognize the current URL as crossroads environment")

    def permission_should_not_be_assigned(self, perm_id):
        """
        verify that the specified permission is assigned, detects the current environment automatically
        :param perm_id:
        :return:
        """
        url = self.get_location()
        if self.gps.is_in_xroads6(url):
            if self.is_permission_assigned_in_xroads6(perm_id):
                raise AssertionError("Permission %s is NOT supposed to be assigned, but it IS!" % perm_id)
        elif self.gps.is_in_xroads7(url):
            if self.is_permission_assigned_in_xroads7(perm_id):
                raise AssertionError("Permission %s is NOT supposed to be assigned, but it IS!" % perm_id)
        else:
            raise AssertionError("Not able to recognize the current URL as crossroads environment")

    def is_permission_assigned_in_xroads6(self, perm_id_label):
        """
        check if the permission is assigned in xroads6
        :param perm_id_label:
        :return:
        """
        # perm_info is a map from permission id to it's label and permission group information like:
        # {'GSM_AM_Hub_Operator_2': ('BICS Managed Hub Operator', 'ManagedHubCo', 'GSM Agreement Management - Managed Hub')}
        perm_info = self.get_permission_group([perm_id_label])
        perm_detail = perm_info.values()[0]
        perm_id = perm_info.keys()[0]
        perm_label = perm_detail[0]
        perm_grp_id = perm_detail[1]
        perm_grp_label = perm_detail[2]
        perm_grp_ul_xpath = "xpath=//ul[@id='%s_Group']" % perm_grp_id
        perm_input_xpath = perm_grp_ul_xpath + "/ul/li/input[@id='%s']" % perm_id
        self.page_should_contain_element(perm_grp_ul_xpath,
                                         "Not able to find the permission group %s" % perm_grp_label, 'ERROR')
        perm_grp_li_cls = self.get_element_attribute(perm_grp_ul_xpath + "/li@class")
        if "Close" in perm_grp_li_cls:
            self.click_element(perm_grp_ul_xpath + "/li/span")
        self.page_should_contain_element(perm_input_xpath,
                                         "Not able to find the permission %s" % perm_label, 'ERROR')
        ele = self._element_find(perm_input_xpath, True, True)
        return ele.is_selected()

    def is_permission_assigned_in_xroads7(self, perm_id):
        """
        Verify that the permission is checked on UI
        :param perm_id:
        :return:
        """
        perm_detail = self.get_permission_group([perm_id]).values()[0]
        perm_label = perm_detail[0]
        # perm_grp_id = perm_detail[1]
        perm_grp_label = perm_detail[2]
        perm_grp_locator = "xpath=//table[tbody[tr[td[div[h1[text()='%s']]]]]]" % perm_grp_label
        self.page_should_contain_element(perm_grp_locator, "Could not find the permission group %s" % perm_grp_label,
                                         'ERROR')
        # check if the group is expanded
        perm_grp_expand_close_locator = perm_grp_locator + "/tbody/tr/td/a[contains(@title, '%s')]" % perm_grp_label
        expanded = "Collapse" in self._element_find(perm_grp_expand_close_locator, True, True).get_attribute("title")
        while not expanded:
            self.click_element(perm_grp_expand_close_locator)
            self.wait_for_adf_sync()
            expanded = "Collapse" in self._element_find(perm_grp_expand_close_locator, True, True).get_attribute("title")
        perm_locator = "xpath=//table[tbody[tr[td[span[text()='%s']]]]]" % perm_label
        self.page_should_contain_element(perm_locator, "Could not find the permission %s " % perm_label, 'ERROR')
        perm_check_ele = self._element_find(perm_locator + "/tbody/tr/td/span/span/span/input", True, True)
        return perm_check_ele.is_selected()

    def wait_for_adf_sync(self, timeout=None):
        """
        Wait until the ADF client library finished syncing with server side. ONLY works for xroads 7.
        :param timeout:
        :return:
        """
        self.wait_until_not_busy()
        if self.gps.is_in_xroads6(self.get_location()):
            raise AssertionError("Adf Sync wait not supported for xroads6")
        js_check_adf_sync = "return window.AdfPage && window.AdfPage.PAGE && " \
                            " window.AdfPage.PAGE.isSynchronizedWithServer()"
        if timeout is None:
            timeout = self.get_selenium_timeout()
        self.wait_for_condition(js_check_adf_sync, timeout, "ADF sync not finished within %s" % timeout)

    def click_button_with_text(self, text):
        """
        Helper method for clicking a button, will try different combinations
        :param text:
        :return:
        """
        for xpath in self.button_xpath_candidates:
            ele = self._element_find(xpath % text, True, False)
            if ele:
                ele.click()
                return
        raise AssertionError("No button with text %s found" % text)

    def login_xroads6(self, requires_new=False, user=None, password=None, browser_type=None):
        open_window_found = False
        current_count = len(self._cache._connections)
        logger.info("Begin to find existing browser for xroads6, with %s browsers open" % current_count)
        if current_count > 0:
            # go through the existing windows
            for index in range(1, current_count + 1):
                logger.info("Working on browser index %s" % index)
                self.switch_browser(index)
                if self._current_browser() in self._cache._closed:
                    logger.debug("Browser already closed, skipping")
                    continue
                logger.info("Browser title %s at url %s" % (self.get_title(), self.get_location()))
                if self.gps.is_in_xroads6(self.get_location()):
                    logger.info("Reusable browser found!")
                    open_window_found = True
                    break
        if open_window_found and not requires_new:
            logger.info("Existing window for xroads6 found, reusing it!")
        else:
            if open_window_found:
                self.close_browser()
            self.open_xroads6_and_logon(browser_type, user, password)

    def login_xroads7(self, requires_new=False, user=None, password=None, browser_type=None):
        open_window_found = False
        current_count = len(self._cache._connections)
        logger.info("Begin to find existing browser for xroads7 with %s browsers open" % current_count)
        if current_count > 0:
            # go through the existing windows
            for index in range(1, current_count + 1):
                logger.info("Working on browser index %s" % index)
                self.switch_browser(index)
                if self._current_browser() in self._cache._closed:
                    logger.debug("Browser already closed, skipping")
                    continue
                logger.info("Browser title %s at url %s" % (self.get_title(), self.get_location()))
                if self.gps.is_in_xroads7(self.get_location()):
                    logger.info("Reusable browser found!")
                    open_window_found = True
                    break
        if open_window_found and not requires_new:
            logger.info("Existing window for xroads6 found, reusing it!")
        else:
            self.open_xroads7_and_logon(browser_type, user, password)

    def open_xroads7_and_logon(self, browser_type, user=None, password=None):
        info = self.gps.XROADS7_ACCESS_INFO
        if browser_type is None:
            browser_type = self.gps.default_browser
        if user is None:
            user = info[1]
        if password is None:
            password = info[2]
        self.open_browser(info[0], browser_type)
        self.wait_until_page_contains_element("name=username")
        self.input_text("name=username", user)
        self.input_text("name=password", password)
        self.click_button("xpath=//input[@type='submit']")
        self.wait_until_page_contains_element("xpath=//a[text()='My Home']", error="Could not login into crossroads 7!")
        self.wait_for_adf_sync()
        self.maximize_browser_window()

    def open_xroads6_and_logon(self, browser_type, user=None, password=None):
        """
        Open a new window with the specified browser_type and login with provided credential
        Will make sure the home tab is fully loaded.
        :param browser_type:
        :param user:
        :param password:
        :return:
        """
        info = self.gps.XROADS6_ACCESS_INFO
        if browser_type is None:
            browser_type = self.gps.default_browser
        if user is None:
            user = info[1]
        if password is None:
            password = info[2]
        self.open_browser(info[0], browser_type)
        self.input_text("name=username", user)
        self.input_text("name=password", password)
        self.click_element("xpath=//input[@type='checkbox']")
        self.click_button("name=Submit")
        self.click_button("name=Submit")#click twice
        self.wait_until_page_contains_element("xpath=//frame[@name='NavFrame']", error="Not able to successfully login")
        self.wait_until_page_contains_element("xpath=//frame[@name='ContentFrame']",
                                              error="Not able to successfully login")
        self.ensure_frames_loaded("xpath=//*[contains(text(),'View all events')]",
                                  "xpath=//frame[@name='ContentFrame']", "xpath=//frame[@name='WorkArea']")
        self.maximize_browser_window()

    def is_currently_on_xroads6_admin_page(self):
        if self._current_browser() is None:
            return False
        if self.gps.is_in_xroads6(self.get_location()):
            ele = self._element_find("xpath=//a[@id='navcenterAdmin']", True, False)
            if ele and "main_nav_selected" in ele.get_attribute("class"):
                return True
        return False

    def select_company(self, company_id, error=None):
        """
        Assumes current browser is on the company selection page
        :param company_id:
        :return:
        """
        xpath_candidates = ("xpath=//select[@name='carrieruid']",
                            "xpath=//select[@name='companyid']",
                            "xpath=//select[@name='carriername']")
        for xpath in xpath_candidates:
            logger.debug("Try xpath %s " % xpath)
            ele = self._element_find(xpath, True, False)
            if ele is not None:
                ele = self._element_find("xpath=//option[@value='%s']" % company_id, True, False)
                if ele is not None:
                    self.select_from_list(xpath, company_id)
                else:
                    ele = self._element_find("xpath=//option[contains(@value,'%s')]" % company_id, True, False)
                    if ele is not None:
                        full_value = ele.get_attribute("value")
                        self.select_from_list(xpath, full_value)
                    else:
                        raise RuntimeError('Not able to find company selection by value %s ' % company_id)
                return
        raise AssertionError("Could not find company selection")

    def select_user(self, user_id, error=None):
        """
        Assumes current browser is on the user selection page
        :param user_id:
        :return:
        """
        xpath_candidates = ("xpath=//select[@name='dnqualifier']",
                            )
        for xpath in xpath_candidates:
            logger.debug("Try xpath %s " % xpath)
            logger.warn("Try xpath %s " % xpath)
            ele = self._element_find(xpath, True, False)
            if ele is not None:
                ele = self._element_find("xpath=//option[@value='%s']" % user_id, True, False)
                if ele is not None:
                    self.select_from_list(xpath, user_id)
                else:
                    logger.warn("xpath=//option[contains(@value,'%s')]" % user_id, True)
                    ele = self._element_find("xpath=//option[contains(@value,'%s')]" % user_id, True, False)
                    if ele is not None:
                        full_value = ele.get_attribute("value")
                        self.select_from_list(xpath, full_value)
                    else:
                        raise RuntimeError('Not able to find user selection by value %s ' % user_id)
                return
        raise AssertionError("Could not find user selection")

    def wait_until_page_contains_button(self, id_or_text, timeout=None, error=None):
        """
        Helper method to avoid test engineer need to specify xpath for buttons
        :param id_or_text:
        :return:
        """

        def button_finder(btn_id_or_text):
            """
            helper method for locating the button by its id or text
            :param btn_id_or_text:
            :return:
            """
            button_found = False
            for xpath in self.button_xpath_candidates:
                # logger.warn("will check button:" + xpath + " % " + btn_id_or_text, True)
                if self._element_find(xpath % btn_id_or_text, True, False) is not None:
                    button_found = True
                    break
            if button_found:
                return
            else:
                return error or "Button with id/text %s could not be found in %s" % (
                    btn_id_or_text, self.get_selenium_timeout())

        self._wait_until_no_error(timeout, button_finder, id_or_text)

    def clear_input(self, xpath):
        self.press_key(xpath, Keys.CONTROL + "a")
        self.press_key(xpath, Keys.DELETE)

    def input_user_profile(self, att_label, att_value):
        if att_label in ['Alternate ID', 'Last Name', 'Response']:
            # input field in the second column of the same row as the label
            locator = "xpath=//tr[td[contains(text(),'%s')]][1]/td[2]/p/input" % att_label
            self.clear_input(locator)
            self.input_text(locator, att_value)
        elif att_label in ['First Name', 'Phone', 'Pager', 'E-mail']:
            locator = "xpath=//tr[td[contains(text(),'%s')]][1]/td[2]/input" % att_label
            self.clear_input(locator)
            self.input_text(locator, att_value)
        elif att_label in ['MI', 'Ext', 'Fax']:
            locator = "xpath=//tr[td[contains(text(),'%s')]][1]/td[4]/input" % att_label
            self.clear_input(locator)
            self.input_text(locator, att_value)
        elif att_label in ['User Type', 'Time Zones', 'Challenge', 'Language Preference']:
            # selection in the second column of the same row as the label
            locator = "xpath=//tr[td[contains(text(),'%s')]]/td[2]/select" % att_label
            self.select_from_list_by_label(locator, att_value)
        elif att_label in ['Comments']:
            # selection in the second column of the same row as the label
            locator = "xpath=//textarea[@id='comment']"
            self.clear_input(locator)
            self.input_text(locator, att_value)
        elif att_label in ['Start Access']:
            (d, m, y) = att_value
            self.select_from_list_by_label("//select[@id='startDay']", d)
            self.select_from_list_by_label("//select[@id='startMonth']", m)
            self.select_from_list_by_label("//select[@id='startYear']", y)
        elif att_label in ['End Access']:
            (d, m, y) = att_value
            self.select_from_list_by_label("//select[@id='endDay']", d)
            self.select_from_list_by_label("//select[@id='endMonth']", m)
            self.select_from_list_by_label("//select[@id='endYear']", y)
        elif att_label in ['Access Status']:
            xpath = "xpath=//input[@type = 'RADIO' and @value='%s']" % att_value
            self.click_element(xpath)
        elif att_label in ['Contact Last Name', 'Contact First Name', 'Contact Phone']:
            real_label = att_label[8:]
            locator = "xpath=//tr[td[contains(text(),'%s')]][2]/td[2]/input" % real_label
            self.clear_input(locator)
            self.input_text(locator, att_value)
        elif att_label in ['Contact MI', 'Contact Ext']:
            real_label = att_label[8:]
            locator = "xpath=//tr[td[contains(text(),'%s')]][2]/td[4]/input" % real_label
            self.clear_input(locator)
            self.input_text(locator, att_value)
        else:
            raise RuntimeError('User profile attribute %s not supported yet!' % att_label)

    def accept_alert(self):
        self._current_browser().switch_to_alert().accept()

    def ensure_frames_loaded(self, load_complete_indicator_locator, *frame_locators):
        """
        Wait until the specified element is present in the frame specified by frame_locators,
        :param load_complete_indicator_locator:
        :param frame_locators: nested xpath for frames down to the one that contains the load complete indicator
        :return:
        """

        def frame_loaded_check():
            for locator in frame_locators:
                frame = self._element_find(locator, True, False)
                if frame is None:
                    logger.debug("Frame %s NOT loaded" % locator)
                    self.unselect_frame()
                    return False
                self.select_frame(locator)
                logger.debug("Frame %s IS loaded" % locator)
                logger.debug(self.get_source())
            frame_content = self.get_source()
            logger.debug(frame_content)
            if len(frame_content) > 0:
                load_complete_indicator = self._element_find(load_complete_indicator_locator, True, False)
                if load_complete_indicator is not None:
                    return True
                else:
                    return False

        self.unselect_frame()
        self._wait_until(self.get_selenium_timeout(), "Not able to select frames ", frame_loaded_check)
        self.unselect_frame()

    def input_company_profile(self, att_label, att_value):
        """
        FIXME extract shared code with the input_user_profile method
        handler method for modifying company details
        :param att_label:
        :param att_value:
        :return:
        """
        if att_label in ['Company', 'TSG ID', 'CTP Company ID', 'LSR Domain',
                         'LSR Password', 'RoamWise Path', 'NetScout PM URL', 'NetScout SI URL']:
            xpath = "xpath=//tr[td[contains(text(),'%s')]]/td/input" % att_label
            self.input_text(xpath, att_value)
        elif att_label in ['Comment']:
            xpath = "xpath=//tr[td[contains(text(),'%s')]]/td/textarea" % att_label
            self.input_text(xpath, att_value)
        elif att_label in ['Company Logo', 'Company Type']:
            xpath = "xpath=//tr[td[contains(text(),'%s')]]/td/select" % att_label
            self.select_from_list_by_label(xpath, att_value)
        elif att_label in ['SPID 2', 'SPID 4', 'SPID 6', 'SPID 8', 'SPID 10', 'Alt SPID 2']:
            xpath = "xpath=//tr[td[contains(text(),'%s')]]/td[3]/input" % att_label
            self.input_text(xpath, att_value)
        elif att_label in ['SPID 1', 'SPID 3', 'SPID 5', 'SPID 7', 'SPID 9', 'Alt SPID 1']:
            # idx = att_label[5:] xpath = "xpath=//input[@name='spid'][%s]" % idx
            xpath = "xpath=//tr[td[contains(text(),'%s')]]/td[2]/table/tbody/tr/td[1]/input" % att_label
            self.input_text(xpath, att_value)
        elif att_label in ['Alt SPID 1', 'Alt SPID 2']:
            idx = att_label[9:]
            xpath = "xpath=//input[@name='altspid'][%s]" % idx
            self.input_text(xpath, att_value)
        elif att_label in ['Status']:
            xpath = "xpath=//input[@type = 'RADIO' and @value='%s']" % att_value
            self.click_element(xpath)
        else:
            raise RuntimeError('Does not support company profile attribute %s ' % att_label)

    def query_alm_entities(self, entity_type, query=None):
        self.alm.query_entity(entity_type, query)


    def fail_alm_test_instance(self, test_set_name, test_name, comment=''):
        test_instance = self.alm.get_test_instance(test_set_name, test_name)
        if None == test_instance:
            raise AssertionError(
                'Could not find test instance under test set %s with name %s' % (test_set_name, test_name))
        logger.info('Found test instance %s' % test_instance)
        run_entity = self.alm.create_run(test_instance)
        self.alm.update_run_status(run_entity, 'Failed', 'Failed by automated test', comment)


    def pass_alm_test_instance(self, test_set_name, test_name, comment=''):
        test_instance = self.alm.get_test_instance(test_set_name, test_name)
        if None == test_instance:
            raise AssertionError(
                'Could not find test instance under test set %s with name %s' % (test_set_name, test_name))
        logger.info('Found test instance %s' % test_instance)
        run_entity = self.alm.create_run(test_instance)
        self.alm.update_run_status(run_entity, 'Passed', 'Passed by automated test', comment)

    def log_out_alm(self):
        self.alm.logout()

    def set_xroads6_info(self, url, username, password):
        logger.info("Setting xroads6 connection info: %s " % ",".join((url, username,password)))
        self.gps.XROADS6_ACCESS_INFO = (url, username, password)

    def set_xroads7_info(self, url, username, password):
        logger.info("Setting xroads7 connection info: %s " % ",".join((url, username,password)))
        self.gps.XROADS7_ACCESS_INFO = (url, username, password)

    def set_default_browser(self, browser_type):
        self.gps.default_browser = browser_type

    def set_alm_credential(self, username, password, domain, project):
        self.alm.set_credential(username, password)
        self.alm.set_domain_project(domain, project)

    def set_xroads6_db_info(self, url, username, password):
        logger.debug('DB connecting to %s with %s:%s' % (url, username, password))
        self.set_connection_info(username, password, url)


    def release_resources(self):
        logger.info('Xroads Page Driver cleaning up')
        self.close_db_connection()
        self.alm.logout()


if __name__ == "__main__":
    import time

    d = XroadsPageDriver()
    d.set_selenium_timeout("2 minutes")
    d.set_default_browser('IE')
    d.login_xroads7()
    source = d.get_source()
    print source
    time.sleep(10)
    d.wait_for_adf_sync()
    # d.click_element("xpath=//a[@title='Logout']")
    d.focus("xpath=//a[text()='Administration']")
    ele = d._element_find("xpath=//a[text()='Administration']", True, False)
    ele.send_keys(Keys.ENTER)


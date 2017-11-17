__author__ = 'g705360'
from robot.api import logger
from Page import Page

class Menu_Users_Default(Page):
    ###########################################
    ## The following methdods are used in the default user page.
    ## The user attribute input field and user tables
    ##############################################

    def input_filter_LoginID(self, driver, login_id):
        elem = driver.find_elements_by_xpath('//th/span/input[@type="text"]')[0]
        elem.clear()
        elem.send_keys(login_id)
        elem.send_keys("\n")

    def input_filter_UserFullName(self, driver, full_name):
        elem = driver.find_elements_by_xpath('//th/span/input[@type="text"]')[1]
        elem.clear()
        elem.send_keys(full_name)
        elem.send_keys("\n")

    def input_filter_UserTypeDesc(self, driver, user_type_desc):
        elem = driver.find_elements_by_xpath('//th/span/input[@type="text"]')[2]
        elem.clear()
        elem.send_keys(user_type_desc)
        elem.send_keys("\n")

    def input_filter_CarrierName(self, driver, carrier_name):
        elem = driver.find_elements_by_xpath('//th/span/input[@type="text"]')[3]
        elem.clear()
        elem.send_keys(carrier_name)
        elem.send_keys("\n")

    def get_table_result(self, driver):
        row_num = len(driver.find_elements_by_xpath('//div[@class="xzw"]//table//tr'))
        column_num = len(driver.find_elements_by_xpath('//div[@class="xzw"]//table//tr[1]/td'))
        #not completed....[AZ]

    def get_table_row_number(self, driver):
        elem = driver.find_element_by_xpath('//div[@class="xzw"]//table')
        return elem.get_attribute("_rowcount")


    def get_table_column_number(self, driver):
        if self.get_table_row_number(driver)!=0:
            return len(driver.find_elements_by_xpath('//div[@class="xzw"]//table//tr[1]/td'))
        else:
            logger.warn("No content in table")
            raise AssertionError

    def get_table_cell_data(self, driver, row_num, column_num):
        max_row_num = self.get_table_row_number(driver)
        if row_num<max_row_num:
            index = r'//div[@class="xzw"]//table//tr['+row_num+r']/td'
            cell = driver.find_elements_by_xpath(index)
            return cell.text
        else:
            logger.warn("No such cell")
            raise AssertionError

    def click_on_user_in_table(self, driver, login_id):
        self.input_filter_LoginID(driver, login_id)
        self.find_element_by_link_text(driver, login_id).click()



    ###################################################
    ## The following methods are used in Add User Page
    ##
    ################################################

    def select_subtab(self, driver, tab_name='User Permissions'):
        pass
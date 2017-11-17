__author__ = 'g705360'
from Page import Page
from robot.api import logger
import string
import time
class Menu_Companies_Default(Page):

    def input_filter_company_id(self, driver, company_id):
        elem = driver.find_elements_by_xpath('//th/span/input[@type="text"]')[0]
        elem.clear()
        elem.send_keys(company_id)
        elem.send_keys("\n")

    def input_filter_TSG_crrier_id(self, driver, TSG_crrier_id):
        elem = driver.find_elements_by_xpath('//th/span/input[@type="text"]')[1]
        elem.clear()
        elem.send_keys(TSG_crrier_id)
        elem.send_keys("\n")

    def input_filter_CTP_company_id(self, driver, CTP_company_id):
        elem = driver.find_elements_by_xpath('//th/span/input[@type="text"]')[2]
        elem.clear()
        elem.send_keys(CTP_company_id)
        elem.send_keys("\n")

    def input_filter_company_name(self, driver, company_name):
        time.sleep(5)
        elem = driver.find_elements_by_xpath('//th/span/input[@type="text"]')[3]
        elem.clear()
        elem.send_keys(company_name)
        elem.send_keys("\n")

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

    def click_on_company_id(self, driver, company_id):
        self.input_filter_LoginID(driver, company_id)
        self.find_element_by_link_text(driver, company_id).click()

    def click_on_add_company_button(self, driver):
        elem = self.find_element_by_xpath(driver, "//img[@title='Add']")
        elem.click()

    def input_company_name(self, driver, company_name):
        elem = driver.find_elements_by_xpath(r"//input[@type='text']")[1]
        elem.clear()
        elem.send_keys(company_name)

    def input_TSG_carrier_id(self, driver, TSG_carrier_id):
        elem = driver.find_elements_by_xpath(r"//input[@type='text']")[3]
        elem.send_keys(TSG_carrier_id)

    def input_CTP_carrier_id(self, driver, CTP_carrier_id):
        elem = driver.find_elements_by_xpath(r"//input[@type='text']")[4]
        elem.send_keys(CTP_carrier_id)

    def click_on_save(self, driver):
        elem = self.find_element_by_xpath(driver, r"//button[@title='Save']")
        elem.click()

    def click_on_cancel(self, driver):
        elem = self.find_element_by_xpath(driver, r"//button[@title='Cancels and returns to the last page and resets the previous page']")
        elem.click()

    def get_new_company_id(self, driver):
        elem = self.find_element_by_xpath(driver, r"//div[@class='x15j']//span")
        title = elem.text
        company_id = string.split(title, ':')[1]
        logger.info(title)
        msg = "New company ID is: " + company_id
        logger.info(msg)
        return company_id


    def click_on_button_ok_to_confirm(self, driver):
        elems = self.find_elements_by_xpath(driver, r"//td//button")
        for each in elems:
            if each.text == "OK":
                each.click()

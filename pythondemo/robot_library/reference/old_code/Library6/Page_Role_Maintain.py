__author__ = 'g705360'
from Page import Page
import time

class Page_Role_Maintain():
    '''
    This test is used for daily testing, since I can not get the User Permission ID from DB.
    To keep consistant, i let company permission ID get from test case as well.
    '''
    def __init__(self):
        self.page = Page()
        self.company_name = None
        self.carrier_id = None
        self.ext_url = "/servlet/CompanySelect?action=modify_role&nextpage=/servlet/RoleMaintenance"
        self.testEnv=None

    def navigate_to_page_role_maintain(self, driver):
        full_url = self.page.get_base_url(driver) + self.ext_url
        if "crossroads-test" in full_url:
            self.testEnv = "xroad6_test"
        self.page.navigate_to_page(full_url)

    def select_company_for_role(self, driver, company_name):
        self.page.find_element_by_xpath("//input[name='searchStr']").send_keys(company_name)
        time.sleep(1)
        self.page.find_element_by_id("submitBtn").click()

    def create_role(self, driver, role_name, desc):
        self.page.find_element_by_link_text(driver, 'Create Role').click()
        time.sleep(2)
        driver.switch_to_window()
        time.sleep(1)
        self.page.find_element_by_name(driver, 'rolename').send_keys(role_name)
        self.page.find_element_by_name(driver, 'roledesc').send_keys(desc)
        self.page.find_element_by_name(driver, 'Button').click()
        driver.switch_to_window()
        self.page.find_element_by_name(driver, 'Button1').click()

    def modify_role(self, driver, role_name):
        self.page.find_element_by_id(driver, role_name).click()
        self.page.find_element_by_link_text(driver, 'Modify Roles').click()
        driver.switch_to_window()




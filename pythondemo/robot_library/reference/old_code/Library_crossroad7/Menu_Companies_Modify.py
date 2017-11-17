__author__ = 'g705360'
from Page import Page

class Menu_Companies_Modify(Page):
    def select_tab(self, driver, tab_name):
        elem = self.find_element_by_link_text(driver, tab_name)

    def select_group_permission(self, driver, group_name):
        title = "Expand "+group_name
        index = r"//a[@title='+title+']"
        elem = self.find_element_by_xpath(driver, index)
        elem.click()


        def select_detail_permission(self, driver, permission_label):
        elems = self.find_elements_by_xpath(driver, "//div[@class='xwf']//table")
        iter = 0
        j = 0
        for elem in elems:
            iter = iter+1
            if elem.text == permission_label:
                j = iter-3

        check_box_elems = self.find_elements_by_xpath(driver, "//div[@class='xwf']//input")
        check_box_elems[j].click()

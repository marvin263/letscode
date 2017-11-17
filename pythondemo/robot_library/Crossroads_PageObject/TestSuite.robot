*** Settings ***
Suite Teardown
Library           Library/Xroads6LoginPage.py
Library           Library/Xroads6HomePage.py
Library           Library/Xroads6AdminPage.py
Library           Library/Xroads6UserModifyPage.py

*** Test Cases ***
Assign User Permission
    #    log    123
    Login Crossroads6    https://crossroads-test.syniverse.com    M000889    Syniverse1!    IE
    #    Navigate To Apps    Admin
    #    Navigate To User Page    User_Profile_Modify
    #    Assign User Permission    P002869    Inbound Roaming Market Share (IRMS)    IRMS Analysis    True
    #    Login Crossroads6    https://crossroads-test.syniverse.com    P002869    Syniverse1!    IE
    #    Link Should Visible In Navigation Bar    Reports & Analysis
    Kill Browsers

Revoke User Permission
    Login Crossroads6    https://crossroads-test.syniverse.com    M000889    Syniverse1!    IE
    Navigate To Apps    Admin
    Navigate To User Page    User_Profile_Modify
    Revoke User Permission    P002869    Inbound Roaming Market Share (IRMS)    IRMS Analysis    True
    Login Crossroads6    https://crossroads-test.syniverse.com    P002869    Syniverse1!    IE
    Link Should Not Visible In Navigation Bar    Reports & Analysis

11
    Open Browser    http://www.baidu.com    IE
    Close All Browsers

'''
Created on Aug 29, 2014

@author: g705362
'''

import os
import sys
import math
from datetime import date, datetime, timedelta
import cx_Oracle
import ldap
from robot.api import logger
import string
from Call_LDAP import Call_LDAP


class xrd_db_validation(object):
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.oracle_perm=[]
        self.ldap_perm=[]
        self.conn=None
        self.perm_list = []
          
    def Get_User_Perm_from_Oracle(self,user_id):
        self.oracle_perm=[]
        xrd_db=cx_Oracle.connect('XROADS_RO', 'xroads_r34d', 'rhrac1scan.syniverse.com:1521/xrds3t')
        print "connecting to Xrd DB:%s"%xrd_db.version
        xrd_db_cursor=xrd_db.cursor()
        query="SELECT distinct aa.rsrc_id as permissionid from access_auth aa, accessor ac ,user_xroads u where aa.accessor_id = ac.accessor_id  and aa.rsrc_typ_cd='2' and ac.usr_id=u.usr_login_id and UPPER(u.usr_login_id)=UPPER('%s')"%(user_id)
        logger.info(query)
        xrd_db_cursor.execute(query)
        self.tmp_result=xrd_db_cursor.fetchall()
        for i in self.tmp_result:
            self.oracle_perm.append(i[0])       
        
        xrd_db_cursor.close()
        
    def Get_Comp_Perm_from_Oracle(self,cmp_name):
        
        self.oracle_perm=[]
        xrd_db=cx_Oracle.connect('XROADS_RO', 'xroads_r34d', 'rhrac1scan.syniverse.com:1521/xrds3t')
        print "connecting to Xrd DB:%s"%xrd_db.version
        xrd_db_cursor=xrd_db.cursor()
        query="select cr.rsrc_id from company_resource cr , company_xroads cx \
        where cr.cmpny_id = cx.cmpny_cd and cx.cmpny_name = '%s'\
         and cr.rsrc_id in ('OCSMS_AccNA_Co') and cr.rsrc_typ_cd = '2'"%(cmp_name);  

        logger.info(query)
        xrd_db_cursor.execute(query)
        self.tmp_result=xrd_db_cursor.fetchall()
        for i in self.tmp_result:
            self.oracle_perm.append(i[0])       
        
        xrd_db_cursor.close()        
        
        
        
    def Check_Perm_in_Oracle(self,perm):
        if perm in self.oracle_perm:
            return True
        else:
            return False
        
    
    def Validate_perm_in_Oracle(self,user_perm):
        
        if self.Check_Perm_in_Oracle(user_perm):
            logger.info("Permission %s exists in Oracle DB"%user_perm)
        else:
            raise AssertionError("Permission %s does not exist in Oracle DB"%user_perm)
            
    def Validate_perm_not_in_Oracle(self,user_perm):
        if not self.Check_Perm_in_Oracle(user_perm):
            logger.info("Permission %s not exists in Oracle DB"%user_perm)
        else:
            raise AssertionError("Permission %s exist in Oracle DB"%user_perm)
        
            
#LDAP operation--------------------------------------------------------------------------
            
    def login_ldap_test(self, server=None, username=None, password=None):
        if server==None:
            server = "ldap://crossroads-lb-test.syniverse.com:3889"
            dn = "uid=ldapbrowser,o=crossroads"
            pw = "#readonly#"

        try:
            self.conn =ldap.initialize('ldap://crossroads-lb-test.syniverse.com:3889')
            self.conn.simple_bind_s(dn, pw)
            logger.info("Connecting to LDAP %s"%server)            
        except ldap.LDAPError, e:
            print e.message['info']
            if type(e.message) == dict and e.message.has_key('desc'):
                print e.message['desc']

    def get_user_permissions_from_ldap(self, user_id):
        search_DN = "ou=People,o=Crossroads"
        filter = "dnqualifier="+user_id
        attribute = ["permissionid"]
        result = self.conn.search_s(search_DN, ldap.SCOPE_SUBTREE, filter)
        #print result[0][1]
        return result[0][1]
    
    def get_comp_id_by_name(self,comp_name):
        
        xrd_db=cx_Oracle.connect('XROADS_RO', 'xroads_r34d', 'rhrac1scan.syniverse.com:1521/xrds3t')
        print "connecting to Xrd DB:%s"%xrd_db.version
        xrd_db_cursor=xrd_db.cursor()
        query="select cmpny_cd from company_xroads where cmpny_name = '%s'"%comp_name
        print "executing query to get company_id: %s"%query
        logger.info(query)
        xrd_db_cursor.execute(query)
        tmp_result=xrd_db_cursor.fetchall()
        print "company id is %s"%tmp_result[0]
        xrd_db_cursor.close()
        return tmp_result[0][0]
      
        

        

        
    
    def get_comp_permission_from_ldap(self,comp_name):
        comp_id=self.get_comp_id_by_name(comp_name)
        print "cp_id=%s"%comp_id
        search_DN = "ou=Carriers,o=Crossroads"
        filter = "carrieruid="+comp_id
        #filter="carrieruid=17"
        attribute = ["permissionid"]
        result = self.conn.search_s(search_DN, ldap.SCOPE_SUBTREE, filter,attribute)
        if len(result)==0:
            raise AssertionError("No permission result get from LDAP for company '%s'"%comp_name)
        return result[0][1]  
     
          
    def Get_User_Perm_from_LDAP(self,user_id):
        self.login_ldap_test()
        self.ldap_perm=str(self.get_user_permissions_from_ldap(user_id))
        self.close_ldap_connection()
    
    def Get_Comp_Perm_from_LDAP(self,cmp_name):
        self.login_ldap_test()
        self.ldap_perm=str(self.get_comp_permission_from_ldap(cmp_name))
        self.close_ldap_connection()
        
    def close_ldap_connection(self):
        self.conn.unbind()

    
    def Check_ldap_perm(self,user_perm):
        
        if user_perm in self.ldap_perm:
            return True
        else:
            return False

     
    def Validate_perm_in_LDAP(self,user_perm):
        if self.Check_ldap_perm(user_perm):
            msg="Permission %s exists in LDAP"%user_perm
            print msg
            logger.info(msg)
        else:
            raise AssertionError("Permission %s does not exist in LDAP"%user_perm)
        
    def Validate_perm_not_in_LDAP(self,user_perm):
        if not self.Check_ldap_perm(user_perm):
            logger.info("Permission %s does not exist in LDAP"%user_perm)
        else:
            raise AssertionError("Permission %s exist in LDAP"%user_perm)
            
            
            
  
    
    

a=xrd_db_validation()

#a.Get_User_Perm_from_Oracle('P002820')
#a.Validate_perm_in_Oracle('ANM_Analyzer_User')   
#a.Validate_perm_not_in_Oracle('ANM_Analyzer_User')

a.Get_Comp_Perm_from_LDAP('VZW Wireless 0005')
a.Validate_perm_in_LDAP('OCSMS_AccNA_Co')

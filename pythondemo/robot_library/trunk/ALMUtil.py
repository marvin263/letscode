__author__ = 'g706811'
__version__ = 0.1

from robot.api import logger
import platform
from xml.dom import minidom
from datetime import datetime, date, time
import urllib
import base64
import httplib2
import urllib2
import urlparse
import httplib


class ALMUtil:
    """
    http://perfalm.syniverse.com:8080/qcbin/Help/doc_library/api_refs/REST/webframe.html
    http://perfalm.syniverse.com:8080/qcbin/rest/domains/SYNIVERSE_TRAINING/projects/DEMO_TRAINING/test-sets?query={name[%22ALM%20integration%20test%20set%22]}
    """
    URL_ALM_AUTHENTICATION_CHECK = r'http://perfalm.syniverse.com:8080/qcbin/rest/is-authenticated'
    URL_ALM_AUTHENTICATE = r'http://perfalm.syniverse.com:8080/qcbin/authentication-point/j_spring_security_check'
    URL_ALM_REST = r'http://perfalm.syniverse.com:8080/qcbin/rest'

    def __init__(self, username='g706811', password='g706811@alm', domain='SYNIVERSE_TRAINING',
                 project='DEMO_TRAINING'):
        self.headers = {}
        self.headers['Accept'] = 'application/xml'
        self.headers[
            "user-agent"] = "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11"  # a dummy value copied from chrome
        self._username = username
        self._password = password
        self._domain = domain
        self._project = project
        self._base_url = self._calculate_base_url()

    @property
    def domain(self):
        return self._domain

    @domain.setter
    def domain(self, d):
        self._domain = d
        self._base_url = self._calculate_base_url()
        print self._base_url

    @property
    def project(self):
        return self._project

    @project.setter
    def project(self, p):
        self._project = p
        self._base_url = self._calculate_base_url()
        print self._base_url

    def set_domain_project(self, domain, project):
        self._domain = domain
        self._project = project
        self._base_url = self._calculate_base_url()

    def set_credential(self, username, password):
        self._username = username
        self._password = password
        self._login()


    def _calculate_base_url(self):
        return '%s/domains/%s/projects/%s' % (self.URL_ALM_REST, self.domain, self.project)


    def get_accessible_domains(self):
        url = self.URL_ALM_REST + "/domains"
        http = httplib2.Http()
        response_header, response_content = http.request(url, "GET", headers=self.headers)
        logger.info(response_header)
        logger.info(response_content)

    def get_test_instance(self, test_set_name, test_name):
        """
        get the test instance for the test with test_name and in the test set specified by test_set_name
        :param test_set_name:
        :param test_name:
        :return:
        """
        query = 'test-set.name["%s"]; test.name["%s"]' % (test_set_name, test_name)
        logger.info(query)
        test_instances = self.query_entity('test-instance', query)
        if 0 == len(test_instances):
            return None
        return test_instances[0]

    def create_run(self, test_instance):
        """
        Create  a test run from a test instance. According to the schema in this link:
        http://perfalm.syniverse.com:8080/qcbin/rest/domains/SYNIVERSE_TRAINING/projects/DEMO_TRAINING/customization/entities/run/fields?query={required[true]}
        , it has the following required fields(cycle-id is actually the test-set id)
          <Field PhysicalName="RN_CYCLE_ID" Name="cycle-id" Label="Cycle ID">
            <Size>10</Size>
            <History>false</History>
            <Required>true</Required>
            <System>true</System>
            <Type>String</Type>
            <Verify>false</Verify>
            <Virtual>false</Virtual>
            <Active>false</Active>
            <Visible>true</Visible>
            <Editable>false</Editable>
            <Filterable>true</Filterable>
            <Groupable>false</Groupable>
            <SupportsMultivalue>false</SupportsMultivalue>
          </Field>
          <Field PhysicalName="RN_RUN_NAME" Name="name" Label="Run Name">
            <Size>255</Size>
            <History>false</History>
            <Required>true</Required>
            <System>true</System>
            <Type>String</Type>
            <Verify>false</Verify>
            <Virtual>false</Virtual>
            <Active>true</Active>
            <Visible>true</Visible>
            <Editable>true</Editable>
            <Filterable>true</Filterable>
            <Groupable>false</Groupable>
            <SupportsMultivalue>false</SupportsMultivalue>
          </Field>
          <Field PhysicalName="RN_TEST_ID" Name="test-id" Label="Test">
            <Size>10</Size>
            <History>false</History>
            <Required>true</Required>
            <System>true</System>
            <Type>String</Type>
            <Verify>false</Verify>
            <Virtual>false</Virtual>
            <Active>false</Active>
            <Visible>true</Visible>
            <Editable>false</Editable>
            <Filterable>true</Filterable>
            <Groupable>false</Groupable>
            <SupportsMultivalue>false</SupportsMultivalue>
          </Field>
          <Field PhysicalName="RN_TESTCYCL_ID" Name="testcycl-id" Label="Test Instance">
            <Size>10</Size>
            <History>false</History>
            <Required>true</Required>
            <System>true</System>
            <Type>Reference</Type>
            <Verify>false</Verify>
            <Virtual>false</Virtual>
            <Active>true</Active>
            <Visible>true</Visible>
            <Editable>false</Editable>
            <Filterable>false</Filterable>
            <Groupable>true</Groupable>
            <SupportsMultivalue>false</SupportsMultivalue>
          </Field>
          <Field PhysicalName="RN_TESTER_NAME" Name="owner" Label="Tester">
            <Size>60</Size>
            <History>true</History>
            <Required>true</Required>
            <System>true</System>
            <Type>UsersList</Type>
            <Verify>true</Verify>
            <Virtual>false</Virtual>
            <Active>true</Active>
            <Visible>true</Visible>
            <Editable>true</Editable>
            <Filterable>true</Filterable>
            <Groupable>true</Groupable>
            <SupportsMultivalue>false</SupportsMultivalue>
          </Field>
        And test during development shows that the subtype-id is also required field
        :param test_instance_id:
        :return:
        """
        now = datetime.now()
        # FIXME
        run_entity = {'_type': 'run',
                      'name': 'AutoRun Test[%s] %s' % (test_instance['id'], now.strftime("%Y-%m-%d %H:%M:%S")),
                      'testcycl-id': test_instance['id'],
                      'test-id': test_instance['test-id'],
                      'cycle-id': test_instance['cycle-id'],
                      'owner': test_instance['owner'],
                      'subtype-id': 'hp.qc.run.MANUAL',
                      'execution-date': now.strftime("%Y-%m-%d"),
                      'execution-time': now.strftime("%H:%M:%S"),
                      'host': platform.node(),
                      'os-name': '%s %s' % (platform.system(), platform.release()),
                      'os-build': platform.version(),
                      'os-sp': platform.win32_ver()[2],
        }
        run_entity = self.create_entity(run_entity)[0]
        logger.debug('test run created [%s]' % run_entity)
        return run_entity

    def update_run_status(self, run_entity, status, actual, comment):
        # query all the run steps under this run
        run_steps = self.query_entity('run-step', 'parent-id[%s]' % run_entity['id'], 'run', run_entity['id'])
        for run_step in run_steps:
            if run_step['id'] == '0':
                continue
            update_run_step = {}
            update_run_step['status'] = status
            update_run_step['actual'] = actual
            self.update_run_step(run_entity['id'], run_step['id'], update_run_step)
        update_run = {}
        update_run['status'] = status
        update_run['comments'] = comment
        self.update_run(run_entity['id'], update_run)


    def update_run(self, run_id, update_field_value_map):
        self.update_entity('run', run_id, update_field_value_map)

    def update_run_step(self, run_id, run_step_id, update_field_value_map):
        self.update_entity('run-step', run_step_id, update_field_value_map, 'run', run_id)

    def create_entity(self, entity_value_map):
        """
        take a dictionary and try covert it into xml doc and use it to create entity
        :param entity_value_map:
        :return:
        """
        type = entity_value_map['_type']
        url = '%s/%ss' % (self._base_url, type)  # triky, collection name is just entity type+'s'
        xml = self._convert_entity_to_xml(entity_value_map)
        logger.debug(url)
        local_headers = self.headers.copy()
        local_headers['Content-type'] = 'application/xml'
        http = httplib2.Http()
        resp_header, resp_content = http.request(url, 'POST', headers=local_headers, body=xml)
        logger.debug(resp_header)
        logger.debug(resp_content)
        if resp_header['status'] != '201':  # created
            raise RuntimeError(
                'Not able to create entity with fields %s, see the log for response details' % entity_value_map)
        return self._parse_alm_entities(resp_content)

    def update_entity(self, type, id, update_field_value_map, parent_type=None, parent_id=None):
        """
        The lock/unlock check-out/check-in are implemented as per the manual, but we always get 404 error
        and the update is actually successful without such operation. so we comment these for actions for now
        :param type:
        :param id:
        :param update_field_value_map:
        :param parent_type:
        :param parent_id:
        :return:
        """
        url = self._base_url
        if parent_type != None:
            url += '/%ss/%s' % (parent_type, parent_id)  # hacky~~ again TODO
        url += '/%ss/%s' % (type, id)
        # lock entity first
        # self.lock_entity(url)
        # self.check_out_entity(url,'Checking out by auto test script')
        update_field_value_map['_type'] = type
        xml = self._convert_entity_to_xml(update_field_value_map)
        logger.debug(url)
        logger.debug(xml)
        local_headers = self.headers.copy()
        local_headers['Content-type'] = 'application/xml'
        http = httplib2.Http()
        resp_header, resp_content = http.request(url, 'PUT', body=xml, headers=local_headers)
        if resp_header['status'] != '200':  # OK
            # raise RuntimeError(
            logger.warn(resp_header)
            logger.warn(resp_content)
            logger.warn(
                'Not able to update entity with fields %s, see the log for response details' % update_field_value_map)
        # self.check_in_entity(url, 'Checking out by auto test script')
        # self.unlock_entity(url)
        return self._parse_alm_entities(resp_content)

    def unlock_entity(self, entity_url):
        """
        Remove lock on the entity, if not already lock by me, will not do anything
        if already locked by others, will raise runtime exception
        :param entity_url:
        :return:
        """
        logger.debug('Unlocking entity ' + entity_url)
        http = httplib2.Http()
        resp_header, resp_content = http.request(entity_url + "/lock", 'GET', headers=self.headers)
        lock_entity = None
        if resp_header['status'] != '404':  # not found
            lock_entity = resp_content  # it's not the flat entity xml
            logger.debug(lock_entity)
            # TODO check if lock is owned by me before do anything
            http = httplib2.Http()
            resp_header, resp_content = http.request(entity_url + "/lock", 'DELETE', headers=self.headers)
        else:
            # raise RuntimeError('No lock exists on entity %s ' % entity_url)
            logger.warn('No lock exists on entity %s ' % entity_url)
        return lock_entity

    def check_out_entity(self, entity_url, comment):
        logger.debug('Checking out entity ' + entity_url)
        check_out_param = '<?xml version="1.0" ?><CheckOutParameters><Comment>%s</Comment></CheckOutParameters>' % comment
        http = httplib2.Http()
        resp_header, resp_content = http.request(entity_url + "/versions/check-out", 'POST', headers=self.headers,
                                                      body=check_out_param)
        logger.debug(resp_header)
        logger.debug(resp_content)
        return self._parse_alm_entities(resp_content)

    def check_in_entity(self, entity_url, comment):
        logger.debug('Checking in entity ' + entity_url)
        check_out_param = '<?xml version="1.0" ?><CheckInParameters><Comment>Updated by automated test script</Comment></CheckInParameters>'
        http = httplib2.Http()
        resp_header, resp_content = http.request(entity_url + "/versions/check-in", 'POST', headers=self.headers,
                                                      body=check_out_param)
        logger.debug(resp_header)
        logger.debug(resp_content)
        return self._parse_alm_entities(resp_content)

    def lock_entity(self, entity_url):
        """
        Acquire lock on the entity, if already lock by me, will not do anything
        if already locked by others, will raise runtime exception
        :param entity_url:
        :return:
        """
        logger.debug('Locking entity ' + entity_url)
        http = httplib2.Http()
        resp_header, resp_content = http.request(entity_url + "/lock", 'GET', headers=self.headers)
        lock_entity = None
        if resp_header['status'] != '404':  # not found
            lock_entity = resp_content  # it's not the flat entity xml
            logger.debug(lock_entity)
            # TODO check if lock is owned by me before raise anything
            raise RuntimeError('Entity %s already locked.' % entity_url)
        else:
            http = httplib2.Http()
            resp_header, resp_content = http.request(entity_url + "/lock", 'POST', headers=self.headers)
            logger.debug(resp_content)
        return lock_entity


    def query_entity(self, entity_type, query=None, parent_type=None, parent_id=None):
        url = self._base_url
        if parent_type != None:
            url += '/%ss/%s' % (parent_type, parent_id)  # hacky~~ again TODO
        url = "%s/%ss" % (url, entity_type)
        if query != None:
            s = '?query={%s}' % urllib.quote(query)
            url += s
        logger.debug(url)
        logger.debug(self.headers)
        http = httplib2.Http()
        resp_header, resp_content = http.request(url, 'GET', headers=self.headers)
        logger.debug(resp_header)
        entities = self._parse_alm_entities(resp_content)
        logger.debug(entities)
        return entities


    def logout(self):
        logout_url = "%s/authentication_point/logout" % self.URL_ALM_REST
        http = httplib2.Http()
        header,content = http.request(logout_url, 'GET', headers=self.headers)

    def _login(self):
        headers = self.headers.copy()
        login_form_data = urllib.quote('j_username=%s&j_password=%s' % (self._username, self._password), '=&')
        headers['content-length'] = '%s' % len(login_form_data)
        headers['content-type'] = 'application/x-www-form-urlencoded'
        logger.debug(login_form_data)
        logger.debug(headers)
        http = httplib2.Http()
        header, content = http.request(self.URL_ALM_AUTHENTICATE, 'POST', headers=headers, body=login_form_data)
        logger.debug(header)
        logger.debug(content)
        headers['Cookie'] = header['set-cookie']
        del headers['content-length']
        del headers['content-type']
        header, content = http.request(self.URL_ALM_AUTHENTICATION_CHECK, 'GET', headers=headers)
        logger.debug(header)
        logger.debug(content)
        if header['status'] != '200':
            raise RuntimeError('Not able to successfully logon to ALM')
        else:
            self.headers['Cookie'] = headers['Cookie']
        logger.info("ALM login successful")

    def _convert_entity_to_xml(self, entity_value_map):
        """
        Convert the dictionary to xml format.
        :param entity_value_map:
        :return:
        """
        type = entity_value_map['_type']
        dom = minidom.parseString('<Entity Type="%s" />' % type)
        entity_ele = dom.getElementsByTagName('Entity')[0]
        fields_ele = dom.createElement('Fields')
        for n, v in entity_value_map.iteritems():
            if n.startswith('_'):  # means non-field value
                continue
            field_ele = dom.createElement('Field')
            field_ele.setAttribute('Name', n)
            value_ele = dom.createElement('Value')
            value_ele.appendChild(dom.createTextNode(v))
            field_ele.appendChild(value_ele)
            fields_ele.appendChild(field_ele)
        entity_ele.appendChild(fields_ele)
        logger.debug(dom.toxml())
        return dom.toxml()

    def _parse_alm_entities(self, xml):
        """
        Parse an AML entity list in the xml format into dictionary
        :param xml:
        :return:
        """
        entities = []
        entities_doc = minidom.parseString(xml)
        entity_list = entities_doc.getElementsByTagName('Entity')
        for entity in entity_list:
            e = {}
            e['_type'] = entity.attributes['Type'].value
            for ent_field in entity.childNodes[0].childNodes:
                # logger.info('Processing field %s' % ent_field.toxml())
                field_name = ent_field.attributes['Name'].value
                field_value = None
                value_nodes = ent_field.childNodes
                if len(value_nodes) > 0 and value_nodes[0].hasChildNodes():
                    # logger.info(value_nodes[0].childNodes[0].nodeValue)
                    field_value = value_nodes[0].childNodes[0].nodeValue
                if field_value != None:
                    e[field_name] = field_value
            entities.append(e)

        return entities

if __name__ == '__main__':
    # a = ALMUtil()
    # a.login('g706811','syl410@a')
    http = httplib2.Http()
    headers = {}
    base64string = base64.encodestring('%s:%s' % ('g706811', 'syl410@a')).replace('\n', '')
    headers["authorization"] = 'Basic %s' % base64string
    headers['Accept'] = 'application/xml'
    print base64string
    # header, content = http.request("http://perfalm.syniverse.com:8080/qcbin/authentication-point/login.jsp", 'GET',
    # headers=headers)
    #print header
    #print content
    #headers = header.copy()
    #headers['Cookie'] = header['set-cookie']
    headers["user-agent"] = "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11"
    headers['content-length'] = '40'
    headers['content-type'] = 'application/x-www-form-urlencoded'
    login_form_data = urllib.quote('j_username=g706811&j_password=syl410@a', '=&')
    print login_form_data
    print headers
    header, content = http.request(
        "http://perfalm.syniverse.com:8080/qcbin/authentication-point/j_spring_security_check", 'POST',
        headers=headers, body=login_form_data)
    print header
    print content
    headers['Cookie'] = header['set-cookie']
    del headers['content-length']
    del headers['content-type']
    header, content = http.request("http://perfalm.syniverse.com:8080/qcbin/rest/is-authenticated", 'GET',
                                   headers=headers)
    print header
    print content


__author__ = 'g705360'

from jpype import *
import os.path

if __name__=='__main__':

    jarpath = os.path.join(os.path.abspath('.'), 'd:/Python27/')
    startJVM(r"d:\Program Files (x86)\Java\jdk1.7.0_55\jre\bin\client\jvm.dll", "-ea", "-Djava.class.path=%s" % (jarpath + 'JavaCode.jar'))

    JDClass = JClass("jpd.JavaPythonDemo")
    jd = JDClass()
    jd.sayHi("Alan", "Mr.")
    #
    # jprint = java.lang.System.out.println()
    # jprint()

    res = jd.sayHi("Alan", "Mr.")
    print "Python print "+ res
    shutdownJVM()
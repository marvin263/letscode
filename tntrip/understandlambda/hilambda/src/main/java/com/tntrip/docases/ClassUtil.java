package com.tntrip.docases;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 获取接口的所有实现类 理论上也可以用来获取类的所有子类
 * <p>
 * 查询路径有限制，只局限于接口所在模块下，比如pandora-gateway,而非整个pandora（会递归搜索该文件夹下所以的实现类）
 * <p>
 * 路径中不可含中文，否则会异常。若要支持中文路径，需对该模块代码中url.getPath() 返回值进行urldecode.
 * Created by wangzhen3 on 2017/6/23.
 */
public class ClassUtil {
    //private static final Logger LOG = LoggerFactory.getLogger(ClassUtil.class);

    public static ArrayList<Class<?>> getAllClassByInterface(Class<?> inf) {
        ArrayList<Class<?>> list = new ArrayList<>();
        // 判断是否是一个接口
        if (inf.isInterface()) {
            try {
                ArrayList<Class<?>> allClass = getAllClass(inf.getPackage().getName());
                /*
                 * 循环判断路径下的所有类是否实现了指定的接口 并且排除接口类自己
                 */
                for (int i = 0; i < allClass.size(); i++) {
                    /*
                     * 判断是不是同一个接口
                     */
                    // isAssignableFrom:判定此 Class 对象所表示的类或接口与指定的 Class
                    // 参数所表示的类或接口是否相同，或是否是其超类或超接口
                    if (inf.isAssignableFrom(allClass.get(i))) {
                        if (!inf.equals(allClass.get(i))) {
                            // 自身并不加进去
                            list.add(allClass.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                //LOG.error("出现异常{}", e.getMessage());
                System.out.println(e.getMessage());
                throw new RuntimeException("出现异常" + e.getMessage());
            }
        }
        //LOG.info("class list size :" + list.size());
        System.out.println("These have implemented interface=" + inf.getName() + ", classes count:" + list.size());
        return list;
    }


    /**
     * 从一个指定路径下查找所有的类
     *
     * @param packagename
     */
    private static ArrayList<Class<?>> getAllClass(String packagename) {
        //LOG.info("packageName to search：" + packagename);
        System.out.println("packageName to search：" + packagename); //com.tntrip.docases
        List<String> classNameList = getClassName(packagename);
        ArrayList<Class<?>> list = new ArrayList<>();

        for (String className : classNameList) {
            try {
                list.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                //LOG.error("load class from name failed:" + className + e.getMessage());
                System.out.println("load class from name failed:" + className + e.getMessage());
                throw new RuntimeException("load class from name failed:" + className + e.getMessage());
            }
        }
        //LOG.info("find list size :" + list.size());
        System.out.println("Under packageName=" + packagename + ", total classes count:" + list.size());
        return list;
    }

    /**
     * 获取某包下所有类
     *
     * @param packageName 包名
     * @return 类的完整名称
     */
    // packageName: com.tntrip.docases
    public static List<String> getClassName(String packageName) {
        List<String> fileNames = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        //com/tntrip/docases
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();
            //LOG.debug("file type : " + type);
            System.out.println("file type : " + type);
            if (type.equals("file")) {

                String fileSearchPath = url.getPath();
                //LOG.debug("fileSearchPath: " + fileSearchPath);
                System.out.println("packageName=" + packageName + ", packageNameFullPath=" + ": " + fileSearchPath);

                String classesfolder = "/classes/java/main";
                if(fileSearchPath.contains(classesfolder)){
                    classesfolder = "/classes/java/main";
                }else{
                    classesfolder = "/classes";
                }


                fileSearchPath = fileSearchPath.substring(0, fileSearchPath.indexOf(classesfolder));
                //LOG.debug("fileSearchPath: " + fileSearchPath);
                System.out.println("fileSearchPath: " + fileSearchPath);
                fileNames = getClassNameByFile(fileSearchPath, packagePath);

            } else if (type.equals("jar")) {
                try {
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    JarFile jarFile = jarURLConnection.getJarFile();
                    fileNames = getClassNameByJar(jarFile, packagePath);
                } catch (java.io.IOException e) {
                    throw new RuntimeException("open Package URL failed：" + e.getMessage());
                }

            } else {
                throw new RuntimeException("file system not support! cannot load MsgProcessor！");
            }
        }
        return fileNames;
    }

    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath 文件路径
     * @return 类的完整名称
     */
    private static List<String> getClassNameByFile(String filePath, String packagePath) {
        List<String> myClassName = new ArrayList<String>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        if (childFiles == null) {
            return myClassName;
        }

        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                myClassName.addAll(getClassNameByFile(childFile.getPath(), packagePath));
            } else {
                String childFilePath = childFile.getPath();
                // D:/eden/gitworkspace/letscode/tntrip/understandlambda/hilambda/out/production/classes/com/tntrip/docases/AbstractDoCases$LsCase.class
                String fullPath = childFilePath.replaceAll("\\\\", "/");
                if (fullPath.endsWith(".class") && fullPath.contains(packagePath)) {
                    String classesfolder = "/classes/java/main";
                    if(fullPath.contains(classesfolder)){
                        classesfolder = "/classes/java/main";
                    }else{
                        classesfolder = "/classes";
                    }
                    // com.tntrip.docases.AbstractDoCases$LsCase
                    String fullyQualifiedCls = fullPath.substring(fullPath.indexOf(classesfolder) + classesfolder.length()+1, fullPath.lastIndexOf(".")).replace("/", ".");
                    myClassName.add(fullyQualifiedCls);
                }
            }
        }

        return myClassName;
    }

    /**
     * 从jar获取某包下所有类
     *
     * @return 类的完整名称
     */
    // packagePath: com/tntrip/docases
    private static List<String> getClassNameByJar(JarFile jarFile, String packagePath) {
        List<String> myClassName = new ArrayList<String>();
        try {
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                // com/tntrip/understand/generic/ThreeTypeParameter.class
                String entryName = jarEntry.getName();
                //LOG.info("entrys jarfile:"+entryName);
                if (entryName.endsWith(".class") && entryName.startsWith(packagePath)) {
                    entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                    myClassName.add(entryName);
                    //LOG.debug("Find Class :"+entryName);
                    System.out.println("Under packagePath=" + packagePath + ", find Class:" + entryName);
                }
            }
        } catch (Exception e) {
            //LOG.error("发生异常:" + e.getMessage());
            System.out.println("发生异常:" + e.getMessage());
            throw new RuntimeException("发生异常:" + e.getMessage());
        }
        return myClassName;
    }

    public static void main(String[] args) {
        System.out.println(ClassUtil.class.getPackage().getName());
        System.out.println(getClassName(ClassUtil.class.getPackage().getName()));
        System.out.println(getAllClassByInterface(EachCase.class));
    }
}

package com.tntrip.tidyfile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Upload2Synology {

    private static final String ALREADY_UPLOADED_FILES = "D:\\eden\\gitworkspace\\letscode\\tntrip\\understandlambda\\hilambda\\src\\main\\java\\com\\tntrip\\tidyfile\\alreadyUploaded.txt";
    private static final ExecutorService ES = Executors.newFixedThreadPool(2);

    public static final FileWriter ALREADY_UPLOADED_FILE_WRITER = createSuccessFile();

    public static final CloseableHttpClient HTTP_CLIENT = createSharedHttpClient();

    private static CloseableHttpClient createSharedHttpClient() {
        try {
            return HttpClients.custom()
                    .setDefaultRequestConfig(
                            RequestConfig.copy(RequestConfig.DEFAULT)
                                    .setSocketTimeout(20 * 60 * 1000)
                                    .setConnectTimeout(1 * 60 * 1000)
                                    .setConnectionRequestTimeout(1 * 60 * 1000)
                                    .build())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Cannot createSharedHttpClient", e);
        }
    }


    // 本次执行，已经上传的文件
    private static final AtomicInteger ALREADY_UPLOADED = new AtomicInteger(0);

    private static FileWriter createSuccessFile() {
        try {
            return new FileWriter(ALREADY_UPLOADED_FILES, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getLoginUrl() {
        try {
            return "http://192.168.0.105:5000/webapi/auth.cgi?api=SYNO.API.Auth&version=3&method=login" +
                    "&account=" + URLEncoder.encode("marvin", "utf-8") +
                    "&passwd=" + URLEncoder.encode("Susan263", "utf-8") +
                    "&session=FileStation&format=cookie";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String getLogoutUrl(String loginCookie) {
        try {
            return "http://192.168.0.105:5000/webapi/auth.cgi?api=SYNO.API.Auth&version=3&method=logout" +
                    "&session=" + URLEncoder.encode(loginCookie, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getLoginCookie() throws Exception {
        CloseableHttpResponse rsp = HTTP_CLIENT.execute(new HttpGet(getLoginUrl()));
        // {"data":{"sid":"eNfL.iMPUCwX.1780NNN555507"},"success":true}
        String strRsp = EntityUtils.toString(rsp.getEntity());
        rsp.close();
        System.out.println("Login:" + strRsp);
        JSONObject rtn = JSON.parseObject(strRsp);
        boolean success = rtn.getBooleanValue("success");
        if (!success) {
            throw new RuntimeException("getLoginCookie failed. " + strRsp);
        }
        return rtn.getJSONObject("data").getString("sid");
    }

    private static void executeLogout(String sid) throws Exception {
        CloseableHttpResponse rsp = HTTP_CLIENT.execute(new HttpGet(getLogoutUrl(sid)));
        // {"success":true}
        String strRsp = EntityUtils.toString(rsp.getEntity());
        rsp.close();
        System.out.println("Logout:" + strRsp);
    }


    public static void uploadOneFile(String fullPath, String loginCookie) throws Exception {
        long begin = System.currentTimeMillis();
        HttpPost httppost = new HttpPost("http://192.168.0.105:5000/webapi/entry.cgi?api=SYNO.FileStation.Upload&method=upload&version=2");
        httppost.addHeader("Cookie", "id=" + loginCookie);

        File f = new File(fullPath);
        FileBody fileBody = new FileBody(f, ContentType.APPLICATION_OCTET_STREAM, f.getName());

        String byFileName = extractByFileName(f);
        String byLastModified = extractByLastModified(f);
        String finalDestFolder = "/photo/bydate/" + extractDestFolder(byFileName, byLastModified);

        HttpEntity reqEntity = MultipartEntityBuilder.create()
                .setCharset(StandardCharsets.UTF_8)
                .addPart("overwrite", new StringBody("false", ContentType.TEXT_PLAIN))
                .addPart("path", new StringBody(finalDestFolder, ContentType.TEXT_PLAIN))
                .addPart("create_parents", new StringBody("true", ContentType.TEXT_PLAIN))
                .addPart("filename", fileBody)
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .build();

        magicallyModifyEntityContentType(reqEntity);
        httppost.setEntity(reqEntity);

        CloseableHttpResponse rsp = HTTP_CLIENT.execute(httppost);
        System.out.println("Begin: " + f.getName() + " --> " + finalDestFolder);
        System.out.println("    1. " + (byLastModified.equals(byFileName) ? "" : "NOT ") + "equal : " + byFileName + "====" + byLastModified);
        //{"data":{"blSkip":false,"file":"1-160P4230Q1311.jpg","pid":12774,"progress":1},"success":true}
        String strRsp = EntityUtils.toString(rsp.getEntity());
        System.out.println("    2. " + strRsp);
        rsp.close();

        JSONObject rtn = JSON.parseObject(strRsp);
        boolean success = rtn.getBooleanValue("success");
        if (!success) {
            throw new RuntimeException("Upload file failed: " + f.getAbsolutePath());
        }
        System.out.println("    3. cost: " + (System.currentTimeMillis() - begin) + "ms, file size: " + f.length());
        System.out.println("End: " + f.getName() + " --> " + finalDestFolder);
        System.out.println("Already uploaded: " + ALREADY_UPLOADED.incrementAndGet() + "\r\n");
        // 肯定成功了
        recordSuccessFile(f);
    }

    private static void magicallyModifyEntityContentType(HttpEntity reqEntity) throws NoSuchFieldException, IllegalAccessException {
        // org.apache.http.entity.mime.MultipartFormEntity.contentType
        // private final Header contentType
        Field field_ContentType = reqEntity.getClass().getDeclaredField("contentType");

        Field field_modifiers = Field.class.getDeclaredField("modifiers");
        field_modifiers.setAccessible(true);

        field_modifiers.setInt(field_ContentType, field_ContentType.getModifiers() & ~Modifier.FINAL);

        if (!field_ContentType.isAccessible()) {
            field_ContentType.setAccessible(true);
        }

        // multipart/form-data; boundary=v1icVJEt-7kP1epngnd0mxQfrXROeAaotsn; charset=UTF-8
        // 剔除掉后面的 "; charset=UTF-8"，否则，FileStation就返回401
        String originalEntityContentType = reqEntity.getContentType().getValue();
        int pos = originalEntityContentType.lastIndexOf(";");
        // multipart/form-data; boundary=v1icVJEt-7kP1epngnd0mxQfrXROeAaotsn
        String newEntityContentType = originalEntityContentType.substring(0, pos);

        field_ContentType.set(reqEntity, new BasicHeader(HTTP.CONTENT_TYPE, newEntityContentType));
    }

    private static Pattern PTN = Pattern.compile("20\\d{6,6}");

    private static String extractDestFolder(String byFileName, String byLastModified) {
        return !TnStringUtils.isEmpty(byFileName) ? byFileName : byLastModified;
    }


    private static String extractByLastModified(File f) {
        // 正则找到的不匹配。使用lastModified
        int[] ymd = DateUtil.getYearMonthDay(new Date(f.lastModified()));
        int y = ymd[0];
        int m = ymd[1];
        // month是 0--11，得mapping成1--12
        m++;
        return y + "-" + (m <= 9 ? ("0" + m) : ("" + m));
    }

    private static String extractByFileName(File f) {
        Matcher matcher = PTN.matcher(f.getName());
        // 文件名上的日期 最准确了
        if (matcher.find()) {
            String str = matcher.group();
            int year = TnStringUtils.str2int(str.substring(0, 4), -1);
            int month = TnStringUtils.str2int(str.substring(4, 6), -1);
            if (month >= 1 && month <= 12) {
                return year + "-" + (month <= 9 ? ("0" + month) : ("" + month));
            }
        }
        return "";
    }

    public static final String DELIMITER = ", ";

    private static synchronized void recordSuccessFile(File f) {
        try {
            ALREADY_UPLOADED_FILE_WRITER.append(DateUtil.date2String(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS) + DELIMITER + f.getName() + "\r\n");
            ALREADY_UPLOADED_FILE_WRITER.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> collectFullPaths(String fullPathFolder) throws Exception {
        Set<String> alreadyUploadedFileNames = loadAlreadyUploadedFileNames();

        List<String> allFullPath = new ArrayList<>();
        AtomicInteger totalFileCountUnderFolder = new AtomicInteger(0);

        Files.walkFileTree(Paths.get(fullPathFolder), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                File f = file.toFile();
                if (f.isFile()) {
                    totalFileCountUnderFolder.incrementAndGet();
                    if (alreadyUploadedFileNames.contains(f.getName())) {
                        // 该文件名已经存在，忽略掉
                    } else {
                        allFullPath.add(f.getAbsolutePath());
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });

        String msg = "alreadyUploaded: {0}, totalFileCountUnderFolder: {1}, needUploadCount: {2}";
        System.out.println(TnStringUtils.format(msg, alreadyUploadedFileNames.size() + "", totalFileCountUnderFolder.get() + "", allFullPath.size() + ""));
        return allFullPath;
    }

    private static void testUploadOneFile() throws Exception {
        String loginInCookie = getLoginCookie();
        uploadOneFile("E:\\tidypics\\data\\VID_20181006_153115.mp4", loginInCookie);
        executeLogout(loginInCookie);
    }

    private static Set<String> loadAlreadyUploadedFileNames() throws Exception {
        Set<String> alreadyUploadedSet = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader(new File(ALREADY_UPLOADED_FILES)));
        String line = br.readLine();
        while (line != null) {
            int pos = line.indexOf(DELIMITER);
            if (pos >= 0) {
                int endExclusive = pos + DELIMITER.length();
                String fileName = line.substring(endExclusive);
                alreadyUploadedSet.add(fileName);
            }
            line = br.readLine();
        }
        br.close();
        return alreadyUploadedSet;
    }

    private static void updateFilesUnderFolder(String fullPathFolder) throws Exception {
        long begin = System.currentTimeMillis();
        List<String> fullPaths = collectFullPaths(fullPathFolder);
        System.out.println("Total file count: " + fullPaths.size());
        if (fullPaths.size() == 0) {
            return;
        }

        String loginInCookie = getLoginCookie();
        CountDownLatch cdl = new CountDownLatch(fullPaths.size());

        for (String fp : fullPaths) {
            ES.submit(() -> {
                try {
                    uploadOneFile(fp, loginInCookie);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cdl.countDown();
                }
            });
        }
        cdl.await();
        System.out.println("Done. File count=" + fullPaths.size() +
                ", total cost:" + (System.currentTimeMillis() - begin) + "ms");
        executeLogout(loginInCookie);
        ES.shutdownNow();
    }


    public static void main(String[] args) throws Exception {
        // testUploadOneFile();

        String fullPathFolder = "E:\\tidypics\\data";
        updateFilesUnderFolder(fullPathFolder);
    }
}
